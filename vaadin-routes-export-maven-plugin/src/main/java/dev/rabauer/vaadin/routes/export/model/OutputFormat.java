package dev.rabauer.vaadin.routes.export.model;

/**
 * Supported output formats for the routes export.
 */
public enum OutputFormat {
    /** Pretty-printed JSON (default). */
    JSON,
    /** Comma-separated values. */
    CSV,
    /** YAML for human-readable DevOps contexts. */
    YAML
}
