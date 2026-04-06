package dev.rabauer.demo.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import dev.rabauer.demo.layouts.MainLayout;

/**
 * User profile page – accessible by users with either
 * {@code USER} or {@code ADMIN} role.
 */
@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile")
@RolesAllowed({"USER", "ADMIN"})
public class ProfileView extends VerticalLayout {

    public ProfileView() {
        add(
            new H2("Profile"),
            new Paragraph("This view requires the USER or ADMIN role.")
        );
    }
}
