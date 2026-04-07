package dev.rabauer.demo.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.rabauer.demo.layouts.SettingsLayout;
import jakarta.annotation.security.RolesAllowed;

/**
 * Account settings – nested under the settings section
 * ({@code settings/account}).
 *
 * <p>Accessible by users with the {@code USER} or {@code ADMIN} role.</p>
 */
@Route(value = "account", layout = SettingsLayout.class)
@PageTitle("Account Settings")
@RolesAllowed({"USER", "ADMIN"})
public class AccountSettingsView extends VerticalLayout {

    public AccountSettingsView() {
        add(
            new H2("Account Settings"),
            new Paragraph("Manage your account details here.")
        );
    }
}
