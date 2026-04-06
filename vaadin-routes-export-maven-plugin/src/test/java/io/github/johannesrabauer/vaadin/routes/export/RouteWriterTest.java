package io.github.johannesrabauer.vaadin.routes.export;

import io.github.johannesrabauer.vaadin.routes.export.model.AccessType;
import io.github.johannesrabauer.vaadin.routes.export.model.RouteDescriptor;
import io.github.johannesrabauer.vaadin.routes.export.model.SecuritySource;
import io.github.johannesrabauer.vaadin.routes.export.writer.CsvRouteWriter;
import io.github.johannesrabauer.vaadin.routes.export.writer.JsonRouteWriter;
import io.github.johannesrabauer.vaadin.routes.export.writer.YamlRouteWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the three {@link io.github.johannesrabauer.vaadin.routes.export.writer.RouteWriter}
 * implementations.
 */
class RouteWriterTest {

    @TempDir
    File tempDir;

    // -------------------------------------------------------------------------
    // Fixtures
    // -------------------------------------------------------------------------

    private RouteDescriptor adminRoute() {
        RouteDescriptor d = new RouteDescriptor();
        d.setPath("admin");
        d.setClassName("com.example.AdminView");
        d.setRoles(List.of("ADMIN", "SUPERUSER"));
        d.setAccess(AccessType.RESTRICTED);
        d.setSecuritySource(SecuritySource.ANNOTATION);
        d.setLayouts(List.of("com.example.MainLayout"));
        return d;
    }

    private RouteDescriptor rootRoute() {
        RouteDescriptor d = new RouteDescriptor();
        d.setPath("");
        d.setClassName("com.example.RootView");
        d.setRoles(List.of("*"));
        d.setAccess(AccessType.PUBLIC);
        d.setSecuritySource(SecuritySource.ANNOTATION);
        return d;
    }

    // -------------------------------------------------------------------------
    // JSON writer
    // -------------------------------------------------------------------------

    @Test
    void jsonWriterProducesValidJson() throws Exception {
        File out = new File(tempDir, "routes.json");
        new JsonRouteWriter().write(List.of(adminRoute(), rootRoute()), out);

        String content = Files.readString(out.toPath(), StandardCharsets.UTF_8);
        assertThat(content).contains("\"path\" : \"admin\"");
        assertThat(content).contains("\"className\" : \"com.example.AdminView\"");
        assertThat(content).contains("ADMIN");
        assertThat(content).contains("SUPERUSER");
        assertThat(content).contains("RESTRICTED");
    }

    @Test
    void jsonWriterCreatesParentDirectories() throws Exception {
        File out = new File(tempDir, "deep/nested/routes.json");
        new JsonRouteWriter().write(List.of(adminRoute()), out);
        assertThat(out).exists();
    }

    @Test
    void jsonWriterHandlesEmptyList() throws Exception {
        File out = new File(tempDir, "empty.json");
        new JsonRouteWriter().write(List.of(), out);
        String content = Files.readString(out.toPath(), StandardCharsets.UTF_8);
        assertThat(content.trim()).isEqualTo("[ ]");
    }

    // -------------------------------------------------------------------------
    // CSV writer
    // -------------------------------------------------------------------------

    @Test
    void csvWriterProducesHeaderAndRows() throws Exception {
        File out = new File(tempDir, "routes.csv");
        new CsvRouteWriter().write(List.of(adminRoute(), rootRoute()), out);

        List<String> lines = Files.readAllLines(out.toPath(), StandardCharsets.UTF_8);
        assertThat(lines.get(0)).isEqualTo("path,className,roles,layouts,access");
        assertThat(lines.get(1)).startsWith("admin,com.example.AdminView,ADMIN|SUPERUSER");
        assertThat(lines.get(2)).startsWith(",com.example.RootView,*");
    }

    @Test
    void csvWriterEscapesCommasInValues() throws Exception {
        RouteDescriptor route = new RouteDescriptor();
        route.setPath("a,b");
        route.setClassName("com.example.View");
        route.setAccess(AccessType.UNKNOWN);

        File out = new File(tempDir, "comma.csv");
        new CsvRouteWriter().write(List.of(route), out);

        List<String> lines = Files.readAllLines(out.toPath(), StandardCharsets.UTF_8);
        assertThat(lines.get(1)).startsWith("\"a,b\"");
    }

    // -------------------------------------------------------------------------
    // YAML writer
    // -------------------------------------------------------------------------

    @Test
    void yamlWriterProducesValidYaml() throws Exception {
        File out = new File(tempDir, "routes.yaml");
        new YamlRouteWriter().write(List.of(adminRoute()), out);

        String content = Files.readString(out.toPath(), StandardCharsets.UTF_8);
        assertThat(content).contains("path: admin");
        assertThat(content).contains("className: com.example.AdminView");
        assertThat(content).contains("ADMIN");
        assertThat(content).contains("RESTRICTED");
    }

    @Test
    void yamlWriterHandlesEmptyList() throws Exception {
        File out = new File(tempDir, "empty.yaml");
        new YamlRouteWriter().write(List.of(), out);
        assertThat(out).exists();
    }
}
