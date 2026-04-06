package dev.rabauer.vaadin.routes.export.model;

/**
 * Indicates the origin of the security information for a route.
 */
public enum SecuritySource {
    /** Security information was determined from a direct annotation on the class. */
    ANNOTATION,
    /** Security information was inferred heuristically (best-effort). */
    INFERRED
}
