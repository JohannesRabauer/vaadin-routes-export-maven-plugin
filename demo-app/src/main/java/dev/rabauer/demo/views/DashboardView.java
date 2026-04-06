package dev.rabauer.demo.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import dev.rabauer.demo.layouts.MainLayout;

/**
 * Dashboard – accessible by any authenticated user ({@code @PermitAll}).
 */
@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard")
@PermitAll
public class DashboardView extends VerticalLayout {

    public DashboardView() {
        add(
            new H2("Dashboard"),
            new Paragraph("This view is accessible by any authenticated user.")
        );
    }
}
