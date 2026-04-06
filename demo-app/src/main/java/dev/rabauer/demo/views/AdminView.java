package dev.rabauer.demo.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;
import dev.rabauer.demo.layouts.MainLayout;

/**
 * Admin panel – only accessible by users with the {@code ADMIN} role.
 *
 * <p>Also reachable via the alias {@code /admin-panel}.</p>
 */
@Route(value = "admin", layout = MainLayout.class)
@RouteAlias(value = "admin-panel", layout = MainLayout.class)
@PageTitle("Admin")
@RolesAllowed("ADMIN")
public class AdminView extends VerticalLayout {

    public AdminView() {
        add(
            new H2("Admin Panel"),
            new Paragraph("This view is restricted to users with the ADMIN role.")
        );
    }
}
