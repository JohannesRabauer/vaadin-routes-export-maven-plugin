package io.github.johannesrabauer.demo.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.DenyAll;
import io.github.johannesrabauer.demo.layouts.MainLayout;

/**
 * Internal-only view that is completely locked down ({@code @DenyAll}).
 *
 * <p>This demonstrates how the plugin reports a denied route.</p>
 */
@Route(value = "internal", layout = MainLayout.class)
@PageTitle("Internal")
@DenyAll
public class InternalView extends VerticalLayout {

    public InternalView() {
        add(
            new H2("Internal"),
            new Paragraph("This route is completely blocked via @DenyAll.")
        );
    }
}
