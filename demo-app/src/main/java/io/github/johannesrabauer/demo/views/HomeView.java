package io.github.johannesrabauer.demo.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import io.github.johannesrabauer.demo.layouts.MainLayout;

/**
 * Public home page – accessible by anyone ({@code @AnonymousAllowed}).
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Home")
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    public HomeView() {
        add(
            new H2("Welcome!"),
            new Paragraph("This is the public home page of the demo application.")
        );
    }
}
