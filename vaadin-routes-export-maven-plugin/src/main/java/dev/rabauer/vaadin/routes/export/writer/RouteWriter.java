package dev.rabauer.vaadin.routes.export.writer;

import dev.rabauer.vaadin.routes.export.model.RouteDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Strategy interface for writing route descriptors to a file.
 */
public interface RouteWriter {

    /**
     * Writes the given route descriptors to {@code outputFile}.
     *
     * @param routes     non-null list of descriptors to write
     * @param outputFile target file; parent directories are created automatically
     * @throws IOException if writing fails
     */
    void write(List<RouteDescriptor> routes, File outputFile) throws IOException;
}
