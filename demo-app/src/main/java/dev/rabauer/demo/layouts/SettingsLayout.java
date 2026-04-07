package dev.rabauer.demo.layouts;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import dev.rabauer.demo.views.AccountSettingsView;
import dev.rabauer.demo.views.SecuritySettingsView;
import jakarta.annotation.security.RolesAllowed;

/**
 * Settings section layout – acts as both a navigation target
 * ({@code @Route("settings")}) and a parent layout for the nested
 * settings views.
 *
 * <p>Child routes such as {@code settings/account} and
 * {@code settings/security} are rendered inside this layout via the
 * {@link RouterLayout} mechanism.</p>
 */
@Route(value = "settings", layout = MainLayout.class)
@PageTitle("Settings")
@RolesAllowed({"USER", "ADMIN"})
public class SettingsLayout extends VerticalLayout implements RouterLayout {

    public SettingsLayout() {
        add(
                new H2("Settings"),
                new RouterLink("Account", AccountSettingsView.class),
                new RouterLink("Security", SecuritySettingsView.class),
                new Hr()
        );
    }
}
