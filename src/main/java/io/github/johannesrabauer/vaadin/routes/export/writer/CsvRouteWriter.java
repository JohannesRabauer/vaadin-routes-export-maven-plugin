package io.github.johannesrabauer.vaadin.routes.export.writer;

import io.github.johannesrabauer.vaadin.routes.export.model.RouteDescriptor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Writes route descriptors as a UTF-8 CSV file.
 *
 * <p>Columns: {@code path,className,roles,layouts,access}</p>
 */
public class CsvRouteWriter implements RouteWriter {

    private static final String HEADER = "path,className,roles,layouts,access";

    @Override
    public void write(List<RouteDescriptor> routes, File outputFile) throws IOException {
        File parent = outputFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            writer.write(HEADER);
            writer.newLine();
            for (RouteDescriptor route : routes) {
                writer.write(toCsvLine(route));
                writer.newLine();
            }
        }
    }

    private String toCsvLine(RouteDescriptor route) {
        return String.join(",",
                escapeCsv(route.getPath()),
                escapeCsv(route.getClassName()),
                escapeCsv(joinList(route.getRoles())),
                escapeCsv(joinList(route.getLayouts())),
                escapeCsv(route.getAccess() != null ? route.getAccess().name() : ""));
    }

    private String joinList(List<String> list) {
        if (list == null) {
            return "";
        }
        return list.stream().collect(Collectors.joining("|"));
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
