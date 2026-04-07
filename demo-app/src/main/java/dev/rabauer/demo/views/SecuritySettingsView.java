package dev.rabauer.demo.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.rabauer.demo.layouts.SettingsLayout;
import jakarta.annotation.security.RolesAllowed;

/**
 * Security settings – nested under the settings section
 * ({@code settings/security}).
 *
 * <p>Accessible by users with the {@code USER} or {@code ADMIN} role.</p>
 */
@Route(value = "security", layout = SettingsLayout.class)
@PageTitle("Security Settings")
@RolesAllowed({"USER", "ADMIN"})
public class SecuritySettingsView extends VerticalLayout {

    public SecuritySettingsView() {
        add(
            new H2("Security Settings"),
            new Paragraph("Manage your password and two-factor authentication here.")
        );
    }
}
