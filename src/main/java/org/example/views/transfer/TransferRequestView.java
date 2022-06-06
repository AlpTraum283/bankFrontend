package org.example.views.transfer;

import com.google.gson.Gson;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.example.Constants;
import org.example.model.AccountEntity;
import org.example.model.CreateTransferRequestDto;
import org.example.model.TransferRequestResponseDto;
import org.example.model.UserEntityResponseDto;
import org.example.views.LoginView;
import org.example.views.MainView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;


@Route(value = "transfer", layout = MainView.class)
public class TransferRequestView extends VerticalLayout implements BeforeEnterObserver {
    Select<AccountEntity> accountEntitySelect = new Select<>();
    AccountEntity currentAccount = null;
    TextField sum = new TextField("Sum");
    TextField recipient = new TextField("Recipient");
    Button send = new Button("Submit");
    Button cancel = new Button("Cancel");
    Label reminder = new Label();
    HorizontalLayout buttonLayout = new HorizontalLayout();

    public TransferRequestView() throws InterruptedException, IOException, URISyntaxException {
        String token = (String) ComponentUtil.getData(UI.getCurrent(), "token");
        if (token == null) {
            token = Constants.getTokenFromCookie();
        }
        configureSelect(token);
        configureButton(token);
        configureFields(token);
        reminder.setVisible(false);

        add(
                accountEntitySelect
                , sum
                , recipient
                , reminder
                , buttonLayout
        );
    }

    private void configureButton(String token) {
        send.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        send.addClickListener(click -> {
            reminder.setVisible(false);

            if (accountEntitySelect.getValue() == null
                    || accountEntitySelect.getValue().getObjId() == Integer.parseInt(recipient.getValue())) {
                reminder.setText("Аккаунты отправителя и получателя должны различаться");
                reminder.setVisible(true);
                return;
            }
            if ((!sum.isEmpty() && !"".equals(sum.getValue()))
                    && (!recipient.isEmpty() && !"".equals(recipient.getValue()))
                    && !(accountEntitySelect.getValue() == null)
            ) {

                CreateTransferRequestDto transferRequest = new CreateTransferRequestDto(
                        accountEntitySelect.getValue().getObjId()
                        , Long.parseLong(sum.getValue())
                        , Integer.parseInt(recipient.getValue())
                );

                try {
                    CloseableHttpClient httpClient = HttpClients.createDefault();
                    HttpPost httpPost = new HttpPost("http://localhost:8081/transfer");
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Authorization", "Bearer " + token);
                    httpPost.setHeader("Content-Type", "application/json");
                    httpPost.setEntity(new StringEntity(transferRequest.toString()));

                    CloseableHttpResponse response = httpClient.execute(httpPost);
                    HttpEntity entity = response.getEntity();
                    InputStream stream = entity.getContent();

                    InputStreamReader reader = new InputStreamReader(stream);
                    BufferedReader br = new BufferedReader(reader);
                    StringBuilder sb = new StringBuilder();
                    String str;
                    while ((str = br.readLine()) != null) {
                        sb.append(str);
                    }
//                    здесь лежит ответ сервера в виде сущности
                    TransferRequestResponseDto transferRequestResponseDto = new Gson().fromJson(sb.toString(), TransferRequestResponseDto.class);
                    reminder.setText(transferRequestResponseDto.getMessage());
                    reminder.setVisible(true);
                    httpClient.close();
                    if (transferRequestResponseDto.getTransactionId() != null) {
                        reminder.setText("Your request`s id: " + transferRequestResponseDto.getTransactionId());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                reminder.setText("Поля не должны быть пустыми");
                reminder.setVisible(true);
            }


        });

        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addClickListener(click -> {
            sum.clear();
            recipient.clear();
            accountEntitySelect.setValue(null);
        });

        buttonLayout.add(
                send
                , cancel
        );
        buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
    }

    private void configureSelect(String token) throws URISyntaxException, IOException, InterruptedException {
        accountEntitySelect.setLabel("Выберите аккаунт");
        var httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8081/user"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        String responseEntity = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        Gson gson = new Gson();
        UserEntityResponseDto entity = gson.fromJson(responseEntity, UserEntityResponseDto.class);
        List<AccountEntity> accountEntityList = entity.getAccounts();
        accountEntitySelect.setItemLabelGenerator(AccountEntity::getName);
        accountEntitySelect.setItems(accountEntityList);
        accountEntitySelect.setValue(accountEntityList.get(0));
    }

    private void configureFields(String token) {
        accountEntitySelect.addValueChangeListener(change -> {
            var httpClient = HttpClient.newHttpClient();

            HttpRequest request = null;
            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8081/account/" + accountEntitySelect.getValue().getObjId()))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                String responseEntity = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
                Gson gson = new Gson();
                currentAccount = gson.fromJson(responseEntity, AccountEntity.class);

            } catch (IOException | URISyntaxException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        sum.addValueChangeListener(change -> {
            sum.setInvalid(false);
            try {

                if (currentAccount != null
                        && (Integer.parseInt(sum.getValue()) > currentAccount.getBalance())) {
                    sum.setInvalid(true);
                }
            } catch (NumberFormatException e) {
                sum.setInvalid(true);

            }

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
