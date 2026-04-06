package dev.rabauer.vaadin.routes.export.fixtures.views;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.DenyAll;

/**
 * Fixture: a denied route with a route alias.
 */
@Route("secret")
@RouteAlias("hidden")
@DenyAll
public class SecretView {
}
