package io.github.johannesrabauer.vaadin.routes.export.fixtures.views;

import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

/**
 * Fixture: a restricted admin view.
 */
@Route("admin")
@RolesAllowed({"ADMIN", "SUPERUSER"})
public class AdminView {
}
