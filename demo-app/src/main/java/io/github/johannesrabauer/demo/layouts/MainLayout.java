package io.github.johannesrabauer.demo.layouts;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

/**
 * Main application layout with navigation drawer.
 *
 * <p>This layout wraps all views and provides a common header and
 * navigation sidebar.</p>
 */
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Demo App");
        logo.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);
    }

    private void createDrawer() {
        VerticalLayout nav = new VerticalLayout(
                new RouterLink("Home", io.github.johannesrabauer.demo.views.HomeView.class),
                new RouterLink("Dashboard", io.github.johannesrabauer.demo.views.DashboardView.class),
                new RouterLink("Admin", io.github.johannesrabauer.demo.views.AdminView.class),
                new RouterLink("Profile", io.github.johannesrabauer.demo.views.ProfileView.class),
                new RouterLink("Login", io.github.johannesrabauer.demo.views.LoginView.class)
        );
        addToDrawer(nav);
    }
}
