package org.example.views.request;

import com.google.gson.Gson;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import org.example.Constants;
import org.example.model.TransferRequestEntity;
import org.example.model.UserEntity;
import org.example.model.UserRequestsResponseDto;
import org.example.views.LoginView;
import org.example.views.MainView;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Route(value = "request", layout = MainView.class)
//@Theme("Requests")
@CssImport("./styles/styles.css")
public class RequestView extends VerticalLayout implements BeforeEnterObserver {
    UserEntity currentUser;
    List<TransferRequestEntity> requests;

    HorizontalLayout mainLayout = new HorizontalLayout();
    VerticalLayout operationLayout = new VerticalLayout();
    VerticalLayout toolbarLayout = new VerticalLayout();
    HorizontalLayout header = new HorizontalLayout();

    public RequestView() throws URISyntaxException, IOException, InterruptedException {
        String token = (String) ComponentUtil.getData(UI.getCurrent(), "token");
        if (token == null) {
            token = Constants.getTokenFromCookie();
        }
        UserRequestsResponseDto entity = doGetRequest(token);
        currentUser = entity.getUser();
        requests = entity.getRequests();


        header.add(new Label("REQUESTS"));
        header.setWidthFull();

        configureOperationLayout();
        configureToolbarLayout();
        configureMainLayout();
        add(
                header
                , mainLayout
        );
    }

    private void configureMainLayout() {
        mainLayout.add(operationLayout, toolbarLayout);
        mainLayout.setFlexGrow(4, operationLayout);
        mainLayout.setFlexGrow(1, toolbarLayout);
        mainLayout.setSizeFull();
    }

    private void configureToolbarLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        TextField searchFilterField = new TextField();
        searchFilterField.setPlaceholder("Search by request id..");
        searchFilterField.setClearButtonVisible(true);
        searchFilterField.setValueChangeMode(ValueChangeMode.LAZY);
        Icon search = new Icon(VaadinIcon.SEARCH_PLUS);
        search.addClickListener(click -> {
            printRequests(searchFilterField.getValue());
        });
        layout.add(
                searchFilterField,
                search
        );
        layout.setAlignItems(Alignment.CENTER);
        layout.setSizeFull();
        toolbarLayout.add(layout);
        toolbarLayout.setWidth("20em");
        toolbarLayout.setSizeFull();
    }

    private void printRequests(String filter) {
        if (filter == null || "".equals(filter)) {
            configureOperationLayout();
            return;
        }

        operationLayout.removeAll();
        for (TransferRequestEntity request : requests) {
            HorizontalLayout layout = new HorizontalLayout();

            layout.addClassName("operation");
            layout.setJustifyContentMode(JustifyContentMode.EVENLY);
            if (request.getObjId() == Integer.parseInt(filter)) {
                layout.add(
                        new Label("Id: " + request.getObjId()),
                        new Label("Sum: " + request.getSum()),
                        new Label("Status:" + request.getStatus())
                );
                layout.setJustifyContentMode(JustifyContentMode.EVENLY);
                layout.setWidthFull();
                operationLayout.add(layout);
            }

        }


    }

    private void configureOperationLayout() {
        operationLayout.removeAll();
        for (TransferRequestEntity request : requests) {
            HorizontalLayout layout = new HorizontalLayout();
            layout.addClassName("operation");
            layout.add(
                    new Label("Id")
                    , new Label("Sum")
                    , new Label("Status")
            );
            layout.add(
                    new Label("" + request.getObjId()),
                    new Label(String.valueOf(request.getSum())),
                    new Label(request.getStatus())
            );
            layout.setJustifyContentMode(JustifyContentMode.EVENLY);
            layout.setWidthFull();
            operationLayout.add(layout);
        }
        operationLayout.setSizeFull();

    }

    private UserRequestsResponseDto doGetRequest(String token) throws URISyntaxException, IOException, InterruptedException {
        var httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8081/transfer"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        String responseEntity = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        Gson gson = new Gson();
        UserRequestsResponseDto entity = gson.fromJson(responseEntity, UserRequestsResponseDto.class);

        return entity;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        String token = (String) ComponentUtil.getData(UI.getCurrent(), "token");
        if (token == null) {
            token = Constants.getTokenFromCookie();
        }
        if (token == null
                || "".equals(token)) {
            beforeEnterEvent.forwardTo(LoginView.class);
        }
    }
}
