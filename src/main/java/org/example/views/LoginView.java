package org.example.views;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletResponse;
import com.vaadin.flow.theme.Theme;
import org.example.views.user.UserInfoView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * The main view contains a button and a click listener.
 */
@Route(value = "login")
//@Theme("Login page")
public class LoginView extends VerticalLayout {

    Label label = new Label("Invalid user or password");

    Button loginButton = new Button("Log in");
    TextField login = new TextField();
    PasswordField password = new PasswordField();

    public LoginView() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        label.setVisible(false);
        configureButton();
        add(login);
        add(password);
        add(label);
        add(loginButton);
    }

    public void configureButton() {
        loginButton.addClickListener(
                event -> {
                    label.setVisible(false);
                    String authHeader = "Basic " + new String(
                            Base64.getEncoder().encode(
                                    (login.getValue()
                                            + ":"
                                            + password.getValue()).getBytes(StandardCharsets.UTF_8)));
                    var httpClient = HttpClient.newHttpClient();
                    var request = HttpRequest
                            .newBuilder(URI.create("http://localhost:8081/login"))
                            .header("Authorization", authHeader)
                            .build();
                    try {

                        String token = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();

                        Cookie cookie = new Cookie("token", token);
                        cookie.setMaxAge(60*30);
                        cookie.setPath("http://localhost:8080");
                        HttpServletResponse httpServletResponse = VaadinServletResponse.getCurrent().getHttpServletResponse();
                        httpServletResponse.addCookie(cookie);

                        if (!"".equals(token)) {
                            ComponentUtil.setData(UI.getCurrent(), "token", token);
                            UI.getCurrent().navigate(UserInfoView.class);
                        }
                        throw new IOException();


                    } catch (IOException | InterruptedException e) {
                        label.setVisible(true);
                        login.setValue("");
                        password.setValue("");
//                        e.printStackTrace();
                    }
                });
    }
}
