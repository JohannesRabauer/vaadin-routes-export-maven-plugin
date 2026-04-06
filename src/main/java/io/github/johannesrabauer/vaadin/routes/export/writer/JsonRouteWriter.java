package io.github.johannesrabauer.vaadin.routes.export.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.johannesrabauer.vaadin.routes.export.model.RouteDescriptor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Writes route descriptors as pretty-printed UTF-8 JSON.
 */
public class JsonRouteWriter implements RouteWriter {

    private final ObjectMapper objectMapper;

    public JsonRouteWriter() {
        this.objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void write(List<RouteDescriptor> routes, File outputFile) throws IOException {
        outputFile.getParentFile().mkdirs();
        objectMapper.writeValue(outputFile, routes);
    }
}
