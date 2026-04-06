package dev.rabauer.vaadin.routes.export;

import dev.rabauer.vaadin.routes.export.model.AccessType;
import dev.rabauer.vaadin.routes.export.model.OutputFormat;
import dev.rabauer.vaadin.routes.export.model.RouteDescriptor;
import dev.rabauer.vaadin.routes.export.scanner.RouteScanner;
import dev.rabauer.vaadin.routes.export.writer.CsvRouteWriter;
import dev.rabauer.vaadin.routes.export.writer.JsonRouteWriter;
import dev.rabauer.vaadin.routes.export.writer.RouteWriter;
import dev.rabauer.vaadin.routes.export.writer.YamlRouteWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Exports all Vaadin {@code @Route}-annotated classes discovered in the
 * compiled project bytecode as a structured artifact (JSON, CSV, or YAML).
 *
 * <p>Binds by default to the {@code process-classes} phase so that compiled
 * {@code .class} files are available without executing the Vaadin runtime.</p>
 */
@Mojo(
        name = "export-routes",
        defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        threadSafe = true
)
public class ExportRoutesMojo extends AbstractMojo {

    /** The Maven project (injected by Maven). */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Root package to scan for {@code @Route}-annotated classes.
     */
    @Parameter(property = "vaadin.routes.basePackage", required = true)
    private String basePackage;

    /**
     * Path of the output file.
     * Defaults to {@code ${project.build.directory}/vaadin-routes.json}.
     */
    @Parameter(
            property = "vaadin.routes.outputFile",
            defaultValue = "${project.build.directory}/vaadin-routes.json",
            required = true
    )
    private File outputFile;

    /**
     * Output format: {@code JSON} (default), {@code CSV}, or {@code YAML}.
     */
    @Parameter(property = "vaadin.routes.outputFormat", defaultValue = "JSON")
    private OutputFormat outputFormat = OutputFormat.JSON;

    /**
     * Whether to resolve and include the {@code RouterLayout} hierarchy.
     */
    @Parameter(property = "vaadin.routes.includeLayouts", defaultValue = "true")
    private boolean includeLayouts = true;

    /**
     * Whether to include routes that are publicly accessible (no role restriction).
     */
    @Parameter(property = "vaadin.routes.includeAnonymousAccess", defaultValue = "true")
    private boolean includeAnonymousAccess = true;

    /**
     * When {@code true}, the build fails if any route has no security annotation.
     */
    @Parameter(property = "vaadin.routes.failOnMissingRoles", defaultValue = "false")
    private boolean failOnMissingRoles = false;

    /**
     * When {@code true}, external JARs on the compile classpath are also scanned.
     */
    @Parameter(property = "vaadin.routes.scanDependencies", defaultValue = "false")
    private boolean scanDependencies = false;

    // -------------------------------------------------------------------------
    // Mojo entry point
    // -------------------------------------------------------------------------

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("vaadin-routes-export: starting scan (basePackage=" + basePackage + ")");

        URL[] classpathUrls = buildClasspathUrls();
        RouteScanner scanner = new RouteScanner(
                classpathUrls,
                basePackage,
                includeLayouts,
                includeAnonymousAccess,
                scanDependencies,
                getLog()
        );

        List<RouteDescriptor> routes = scanner.scan();
        getLog().info("vaadin-routes-export: discovered " + routes.size() + " route(s)");

        if (failOnMissingRoles) {
            checkForMissingRoles(routes);
        }

        writeOutput(routes);

        getLog().info("vaadin-routes-export: output written to " + outputFile.getAbsolutePath());
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private URL[] buildClasspathUrls() throws MojoExecutionException {
        List<URL> urls = new ArrayList<>();
        try {
            for (String element : project.getCompileClasspathElements()) {
                urls.add(new File(element).toURI().toURL());
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to resolve compile classpath", e);
        }
        return urls.toArray(URL[]::new);
    }

    private void checkForMissingRoles(List<RouteDescriptor> routes) throws MojoFailureException {
        List<String> missing = new ArrayList<>();
        for (RouteDescriptor route : routes) {
            if (route.getAccess() == AccessType.UNKNOWN) {
                missing.add(route.getPath() + " (" + route.getClassName() + ")");
            }
        }
        if (!missing.isEmpty()) {
            for (String entry : missing) {
                getLog().error("Route '" + entry + "' has no explicit security annotation");
            }
            throw new MojoFailureException(
                    "Build failed: " + missing.size() + " route(s) have no security annotation. "
                            + "Add @RolesAllowed, @PermitAll, or @DenyAll to fix this, "
                            + "or set failOnMissingRoles=false to suppress.");
        }
    }

    private void writeOutput(List<RouteDescriptor> routes) throws MojoExecutionException {
        RouteWriter writer = resolveWriter();
        try {
            writer.write(routes, outputFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write output to " + outputFile, e);
        }
    }

    private RouteWriter resolveWriter() {
        return switch (outputFormat) {
            case CSV  -> new CsvRouteWriter();
            case YAML -> new YamlRouteWriter();
            default   -> new JsonRouteWriter();
        };
    }
}
