package io.github.johannesrabauer.vaadin.routes.export.writer;

import io.github.johannesrabauer.vaadin.routes.export.model.RouteDescriptor;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Writes route descriptors as human-readable YAML.
 */
public class YamlRouteWriter implements RouteWriter {

    @Override
    public void write(List<RouteDescriptor> routes, File outputFile) throws IOException {
        outputFile.getParentFile().mkdirs();

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setIndent(2);
        Yaml yaml = new Yaml(options);

        List<Map<String, Object>> data = new ArrayList<>();
        for (RouteDescriptor route : routes) {
            data.add(toMap(route));
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            yaml.dump(data, writer);
        }
    }

    private Map<String, Object> toMap(RouteDescriptor route) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("path", route.getPath());
        map.put("className", route.getClassName());
        if (route.getLayouts() != null) {
            map.put("layouts", route.getLayouts());
        }
        if (route.getRoles() != null) {
            map.put("roles", route.getRoles());
        }
        if (route.getAccess() != null) {
            map.put("access", route.getAccess().name());
        }
        if (route.getAliases() != null && !route.getAliases().isEmpty()) {
            map.put("aliases", route.getAliases());
        }
        if (route.getSecuritySource() != null) {
            map.put("securitySource", route.getSecuritySource().name());
        }
        if (route.isDynamic()) {
            map.put("dynamic", true);
        }
        return map;
    }
}
