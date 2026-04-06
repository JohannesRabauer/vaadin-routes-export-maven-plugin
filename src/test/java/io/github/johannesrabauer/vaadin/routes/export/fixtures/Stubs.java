package io.github.johannesrabauer.vaadin.routes.export.fixtures;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Minimal stub for {@code com.vaadin.flow.router.Route} so that the test
 * fixtures can be compiled without a Vaadin runtime dependency.
 */
public final class Stubs {

    private Stubs() {}

    // -------------------------------------------------------------------------
    // Vaadin stubs
    // -------------------------------------------------------------------------

    /** Stub for com.vaadin.flow.router.Route */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Route {
        String value() default "";
        Class<?> layout() default Void.class;
    }

    /** Stub for com.vaadin.flow.router.RouteAlias */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RouteAlias {
        String value() default "";
        Class<?> layout() default Void.class;
    }

    /** Stub for com.vaadin.flow.router.RouterLayout */
    public interface RouterLayout {}

    /** Stub for com.vaadin.flow.server.auth.AnonymousAllowed */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface AnonymousAllowed {}

    // -------------------------------------------------------------------------
    // Jakarta security stubs
    // -------------------------------------------------------------------------

    /** Stub for jakarta.annotation.security.RolesAllowed */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RolesAllowed {
        String[] value();
    }

    /** Stub for jakarta.annotation.security.PermitAll */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface PermitAll {}

    /** Stub for jakarta.annotation.security.DenyAll */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface DenyAll {}
}
