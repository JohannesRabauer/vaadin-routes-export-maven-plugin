package io.github.johannesrabauer.vaadin.routes.export.model;

/**
 * Describes the effective access level of a route.
 */
public enum AccessType {
    /** Route allows any authenticated user with one of the listed roles. */
    RESTRICTED,
    /** Route is publicly accessible ({@code @PermitAll} or {@code @AnonymousAllowed}). */
    PUBLIC,
    /** Route is completely blocked ({@code @DenyAll}). */
    DENIED,
    /** No security annotation present; access policy is unknown. */
    UNKNOWN
}
