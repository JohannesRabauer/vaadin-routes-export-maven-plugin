package dev.rabauer.vaadin.routes.export;

import dev.rabauer.vaadin.routes.export.model.AccessType;
import dev.rabauer.vaadin.routes.export.model.RouteDescriptor;
import dev.rabauer.vaadin.routes.export.model.SecuritySource;
import dev.rabauer.vaadin.routes.export.scanner.RouteScanner;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RouteScanner}.
 *
 * <p>The test classpath includes compiled fixtures under
 * {@code dev.rabauer.vaadin.routes.export.fixtures.views}.
 * ClassGraph is configured to scan only that package so that test isolation
 * is maintained.</p>
 */
class RouteScannerTest {

    private static final String FIXTURES_PACKAGE =
            "dev.rabauer.vaadin.routes.export.fixtures.views";

    private static URL[] testClasspath;

    @BeforeAll
    static void setupClasspath() throws Exception {
        // Use the current thread classloader's URLs as the classpath for ClassGraph.
        // Maven Surefire puts all test-scoped JARs on the classloader.
        URL classUrl = RouteScannerTest.class.getProtectionDomain()
                .getCodeSource().getLocation();
        testClasspath = new URL[]{classUrl};
    }

    private RouteScanner scanner(boolean includeLayouts, boolean includeAnonymous, boolean scanDeps) {
        return new RouteScanner(testClasspath, FIXTURES_PACKAGE, includeLayouts,
                includeAnonymous, scanDeps, new SystemStreamLog());
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    void scanFindsAllAnnotatedViews() {
        List<RouteDescriptor> routes = scanner(false, true, false).scan();
        // Expect: HomeView, AdminView, PublicInfoView, SecretView, RootView
        assertThat(routes).hasSize(5);
    }

    @Test
    void homeViewHasUnknownAccess() {
        RouteDescriptor home = findByPath(scanner(false, true, false).scan(), "home");
        assertThat(home).isNotNull();
        assertThat(home.getClassName()).endsWith("HomeView");
        assertThat(home.getAccess()).isEqualTo(AccessType.UNKNOWN);
        assertThat(home.getRoles()).isNull();
        assertThat(home.getSecuritySource()).isNull();
    }

    @Test
    void adminViewIsRestricted() {
        RouteDescriptor admin = findByPath(scanner(false, true, false).scan(), "admin");
        assertThat(admin).isNotNull();
        assertThat(admin.getAccess()).isEqualTo(AccessType.RESTRICTED);
        assertThat(admin.getRoles()).containsExactlyInAnyOrder("ADMIN", "SUPERUSER");
        assertThat(admin.getSecuritySource()).isEqualTo(SecuritySource.ANNOTATION);
    }

    @Test
    void publicInfoViewIsPermitAll() {
        RouteDescriptor pub = findByPath(scanner(false, true, false).scan(), "public-info");
        assertThat(pub).isNotNull();
        assertThat(pub.getAccess()).isEqualTo(AccessType.PUBLIC);
        assertThat(pub.getRoles()).containsExactly("*");
        assertThat(pub.getSecuritySource()).isEqualTo(SecuritySource.ANNOTATION);
    }

    @Test
    void secretViewIsDenied() {
        RouteDescriptor secret = findByPath(scanner(false, true, false).scan(), "secret");
        assertThat(secret).isNotNull();
        assertThat(secret.getAccess()).isEqualTo(AccessType.DENIED);
        assertThat(secret.getRoles()).isEmpty();
        assertThat(secret.getSecuritySource()).isEqualTo(SecuritySource.ANNOTATION);
    }

    @Test
    void secretViewHasAlias() {
        RouteDescriptor secret = findByPath(scanner(false, true, false).scan(), "secret");
        assertThat(secret).isNotNull();
        assertThat(secret.getAliases()).containsExactly("hidden");
    }

    @Test
    void rootViewIsAnonymousAllowed() {
        RouteDescriptor root = findByPath(scanner(false, true, false).scan(), "");
        assertThat(root).isNotNull();
        assertThat(root.getAccess()).isEqualTo(AccessType.PUBLIC);
        assertThat(root.getRoles()).containsExactly("*");
    }

    @Test
    void excludingAnonymousAccessFiltersPublicRoutes() {
        List<RouteDescriptor> routes = scanner(false, false, false).scan();
        // PublicInfoView and RootView should be excluded
        assertThat(routes).noneMatch(r -> "public-info".equals(r.getPath()));
        assertThat(routes).noneMatch(r -> "".equals(r.getPath()));
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private RouteDescriptor findByPath(List<RouteDescriptor> routes, String path) {
        return routes.stream()
                .filter(r -> path.equals(r.getPath()))
                .findFirst()
                .orElse(null);
    }
}
