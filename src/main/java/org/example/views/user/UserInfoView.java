package org.example.views.user;

import com.google.gson.Gson;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.example.Constants;
import org.example.model.AccountEntity;
import org.example.model.UserEntityResponseDto;
import org.example.views.LoginView;
import org.example.views.MainView;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Route(value = "user", layout = MainView.class)
public class UserInfoView extends VerticalLayout implements BeforeEnterObserver {

    Label label = new Label();
    HorizontalLayout content = new HorizontalLayout();
    VerticalLayout accountList = new VerticalLayout();
    AccountDetailsForm detailsForm = new AccountDetailsForm();

    public UserInfoView() throws URISyntaxException, IOException, InterruptedException {
        String token = (String) ComponentUtil.getData(UI.getCurrent(), "token");
        if (token == null) {
            token = Constants.getTokenFromCookie();
        }

//         в переменной token лежит подписанный бэком токен,
//         а в id - id пользователя, который вошел в систему

        UserEntityResponseDto entity = doGetRequest(token);

        label.setText("Добро пожаловать, " + entity.getUser().getName());

        configureAccountsLayout(entity);
        add(
                label
                , configureContent()
        );
    }
    private Component configureContent() {
        content.add(accountList);

        content.setSizeFull();
        return content;
    }

    private UserEntityResponseDto doGetRequest(String token) throws URISyntaxException, IOException, InterruptedException {
        var httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8081/user"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        String responseEntity = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        Gson gson = new Gson();
        UserEntityResponseDto entity = gson.fromJson(responseEntity, UserEntityResponseDto.class);
        return entity;
    }

    private void configureAccountsLayout(UserEntityResponseDto entity) {
        List<AccountEntity> accountEntityList = entity.getAccounts();
        accountEntityList.forEach(account -> {

            VerticalLayout layout = new VerticalLayout(
                    new Label(account.getBalance() + "\t" + account.getCurrency())
                    , new Label(account.getName())
            );
            layout.addClassName("account");
            layout.addClickListener(click -> {

                try {
                    String token = (String) ComponentUtil.getData(UI.getCurrent(), "token");
                    if (token == null) {
                        token = Constants.getTokenFromCookie();
                    }
                    var httpClient = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI("http://localhost:8081/account/" + account.getObjId() + "/operations"))
                            .header("Authorization", "Bearer " + token)
                            .GET()
                            .build();
                    String responseEntity = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();

//                  передаем полученную сущность на страницу подробностей
                    ComponentUtil.setData(UI.getCurrent(), "accountResponse", responseEntity);
                    UI.getCurrent().navigate(AccountOperationsView.class);

                } catch (URISyntaxException | IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            });

            accountList.add(layout);

        });
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
