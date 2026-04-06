package dev.rabauer.vaadin.routes.export.fixtures.views;

import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

/**
 * Fixture: a permit-all route.
 */
@Route("public-info")
@PermitAll
public class PublicInfoView {
}
