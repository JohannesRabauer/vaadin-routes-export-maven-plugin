package io.github.johannesrabauer.vaadin.routes.export.scanner;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValue;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import io.github.johannesrabauer.vaadin.routes.export.model.AccessType;
import io.github.johannesrabauer.vaadin.routes.export.model.RouteDescriptor;
import io.github.johannesrabauer.vaadin.routes.export.model.SecuritySource;
import org.apache.maven.plugin.logging.Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Scans compiled bytecode for Vaadin {@code @Route}-annotated classes and
 * builds a list of {@link RouteDescriptor} instances.
 *
 * <p>The scanner uses ClassGraph for bytecode-only scanning so that no class
 * is actually loaded into the JVM, avoiding unwanted static initializers.</p>
 */
public class RouteScanner {

    // Annotation names
    private static final String ANNOTATION_ROUTE         = "com.vaadin.flow.router.Route";
    private static final String ANNOTATION_ROUTE_ALIAS   = "com.vaadin.flow.router.RouteAlias";
    private static final String ANNOTATION_ROLES_ALLOWED = "jakarta.annotation.security.RolesAllowed";
    private static final String ANNOTATION_PERMIT_ALL    = "jakarta.annotation.security.PermitAll";
    private static final String ANNOTATION_DENY_ALL      = "jakarta.annotation.security.DenyAll";
    private static final String ANNOTATION_ANON_ALLOWED  = "com.vaadin.flow.server.auth.AnonymousAllowed";

    // Vaadin RouterLayout interface
    private static final String ROUTER_LAYOUT_INTERFACE  = "com.vaadin.flow.router.RouterLayout";

    private final URL[]   classpathUrls;
    private final String  basePackage;
    private final boolean includeLayouts;
    private final boolean includeAnonymousAccess;
    private final boolean scanDependencies;
    private final Log     log;

    public RouteScanner(
            URL[]   classpathUrls,
            String  basePackage,
            boolean includeLayouts,
            boolean includeAnonymousAccess,
            boolean scanDependencies,
            Log     log) {
        this.classpathUrls         = classpathUrls;
        this.basePackage           = basePackage;
        this.includeLayouts        = includeLayouts;
        this.includeAnonymousAccess = includeAnonymousAccess;
        this.scanDependencies      = scanDependencies;
        this.log                   = log;
    }

    /**
     * Runs the scan and returns all discovered route descriptors.
     *
     * @return immutable list of route descriptors, never {@code null}
     */
    public List<RouteDescriptor> scan() {
        ClassGraph classGraph = new ClassGraph()
                .overrideClasspath((Object[]) classpathUrls)
                .enableAnnotationInfo()
                .enableClassInfo()
                .enableMethodInfo()
                .acceptPackages(basePackage);

        if (!scanDependencies) {
            classGraph = classGraph.disableJarScanning();
        }

        List<RouteDescriptor> results = new ArrayList<>();

        try (ScanResult scanResult = classGraph.scan()) {
            ClassInfoList routeClasses = scanResult.getClassesWithAnnotation(ANNOTATION_ROUTE);
            log.info("Found " + routeClasses.size() + " class(es) annotated with @Route");

            for (ClassInfo classInfo : routeClasses) {
                List<RouteDescriptor> descriptors = buildDescriptors(classInfo, scanResult);
                results.addAll(descriptors);
            }
        }

        return Collections.unmodifiableList(results);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private List<RouteDescriptor> buildDescriptors(ClassInfo classInfo, ScanResult scanResult) {
        List<RouteDescriptor> descriptors = new ArrayList<>();

        // Primary @Route
        AnnotationInfo routeAnnotation = classInfo.getAnnotationInfo(ANNOTATION_ROUTE);
        if (routeAnnotation != null) {
            RouteDescriptor descriptor = buildSingleDescriptor(classInfo, routeAnnotation, scanResult);
            if (descriptor != null) {
                descriptors.add(descriptor);
            }
        }

        // Collect @RouteAlias values and attach them to the primary descriptor
        List<String> aliases = extractAliases(classInfo);
        if (!aliases.isEmpty() && !descriptors.isEmpty()) {
            descriptors.get(0).setAliases(aliases);
        }

        return descriptors;
    }

    private RouteDescriptor buildSingleDescriptor(
            ClassInfo classInfo,
            AnnotationInfo routeAnnotation,
            ScanResult scanResult) {

        RouteDescriptor descriptor = new RouteDescriptor();
        descriptor.setClassName(classInfo.getName());

        // --- Path ---
        String path = extractAnnotationStringValue(routeAnnotation, "value", "");
        descriptor.setPath(path);

        // --- Layouts ---
        if (includeLayouts) {
            String layoutClassName = extractAnnotationClassValue(routeAnnotation, "layout");
            List<String> layoutChain = resolveLayoutChain(layoutClassName, scanResult);
            descriptor.setLayouts(layoutChain);
        }

        // --- Security ---
        applySecurityAnnotations(classInfo, descriptor);

        // --- Anonymous access filter ---
        if (!includeAnonymousAccess && descriptor.getAccess() == AccessType.PUBLIC) {
            return null;
        }

        return descriptor;
    }

    /**
     * Resolves the full layout chain for a given layout class name.
     *
     * <p>Walks up the {@code RouterLayout} hierarchy until no more parent layouts
     * are found.  The returned list is ordered outermost-first.</p>
     */
    private List<String> resolveLayoutChain(String layoutClassName, ScanResult scanResult) {
        List<String> chain = new ArrayList<>();
        if (layoutClassName == null || layoutClassName.isEmpty()) {
            return chain;
        }

        String current = layoutClassName;
        while (current != null && !current.isEmpty()) {
            chain.add(current);
            ClassInfo layoutInfo = scanResult.getClassInfo(current);
            if (layoutInfo == null) {
                break;
            }
            // Look for a @Route annotation that declares a parent layout
            AnnotationInfo parentRoute = layoutInfo.getAnnotationInfo(ANNOTATION_ROUTE);
            if (parentRoute != null) {
                current = extractAnnotationClassValue(parentRoute, "layout");
            } else {
                // Check if the layout itself implements RouterLayout and has a declared parent
                boolean implementsRouterLayout = layoutInfo.implementsInterface(ROUTER_LAYOUT_INTERFACE);
                if (implementsRouterLayout) {
                    // No further navigation target annotation – stop traversal
                    break;
                }
                break;
            }
        }

        return chain;
    }

    private void applySecurityAnnotations(ClassInfo classInfo, RouteDescriptor descriptor) {
        // @DenyAll
        if (classInfo.hasAnnotation(ANNOTATION_DENY_ALL)) {
            descriptor.setRoles(Collections.emptyList());
            descriptor.setAccess(AccessType.DENIED);
            descriptor.setSecuritySource(SecuritySource.ANNOTATION);
            return;
        }

        // @PermitAll
        if (classInfo.hasAnnotation(ANNOTATION_PERMIT_ALL)) {
            descriptor.setRoles(List.of("*"));
            descriptor.setAccess(AccessType.PUBLIC);
            descriptor.setSecuritySource(SecuritySource.ANNOTATION);
            return;
        }

        // @AnonymousAllowed (Vaadin-specific)
        if (classInfo.hasAnnotation(ANNOTATION_ANON_ALLOWED)) {
            descriptor.setRoles(List.of("*"));
            descriptor.setAccess(AccessType.PUBLIC);
            descriptor.setSecuritySource(SecuritySource.ANNOTATION);
            return;
        }

        // @RolesAllowed
        AnnotationInfo rolesAllowed = classInfo.getAnnotationInfo(ANNOTATION_ROLES_ALLOWED);
        if (rolesAllowed != null) {
            List<String> roles = extractRolesAllowed(rolesAllowed);
            descriptor.setRoles(roles);
            descriptor.setAccess(AccessType.RESTRICTED);
            descriptor.setSecuritySource(SecuritySource.ANNOTATION);
            return;
        }

        // No annotation
        descriptor.setRoles(null);
        descriptor.setAccess(AccessType.UNKNOWN);
        descriptor.setSecuritySource(null);
    }

    private List<String> extractRolesAllowed(AnnotationInfo rolesAllowed) {
        AnnotationParameterValue valueParam = rolesAllowed.getParameterValues().get("value");
        if (valueParam == null) {
            return Collections.emptyList();
        }
        Object rawValue = valueParam.getValue();
        if (rawValue instanceof Object[] arr) {
            List<String> roles = new ArrayList<>();
            for (Object item : arr) {
                roles.add(String.valueOf(item));
            }
            return roles;
        }
        if (rawValue instanceof String s) {
            return List.of(s);
        }
        return Collections.emptyList();
    }

    private List<String> extractAliases(ClassInfo classInfo) {
        List<String> aliases = new ArrayList<>();

        // @RouteAlias may appear once or be repeated (collected via @RouteAlias.Container).
        for (AnnotationInfo aliasInfo : classInfo.getAnnotationInfoRepeatable(ANNOTATION_ROUTE_ALIAS)) {
            String alias = extractAnnotationStringValue(aliasInfo, "value", null);
            if (alias != null) {
                aliases.add(alias);
            }
        }
        return aliases;
    }

    private String extractAnnotationStringValue(AnnotationInfo annotationInfo, String key, String defaultValue) {
        AnnotationParameterValue param = annotationInfo.getParameterValues().get(key);
        if (param == null) {
            return defaultValue;
        }
        Object value = param.getValue();
        return value != null ? String.valueOf(value) : defaultValue;
    }

    private String extractAnnotationClassValue(AnnotationInfo annotationInfo, String key) {
        AnnotationParameterValue param = annotationInfo.getParameterValues().get(key);
        if (param == null) {
            return null;
        }
        Object value = param.getValue();
        if (value == null) {
            return null;
        }
        // ClassGraph returns class references as ClassInfo or as a class-ref string
        String raw = String.valueOf(value);
        // Remove ClassGraph "class reference" wrapper if present, e.g. "class com.example.Foo"
        if (raw.startsWith("class ")) {
            raw = raw.substring("class ".length()).trim();
        }
        // ClassGraph AnnotationClassRef may include a trailing ".class" suffix
        if (raw.endsWith(".class")) {
            raw = raw.substring(0, raw.length() - ".class".length());
        }
        return raw;
    }
}
