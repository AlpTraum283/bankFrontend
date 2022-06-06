package org.example.views.user;

import com.google.gson.Gson;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import org.example.Constants;
import org.example.model.AccountEntity;
import org.example.model.AccountEntityResponseDto;
import org.example.model.TransferEntity;
import org.example.views.LoginView;
import org.example.views.MainView;

import javax.servlet.http.Cookie;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route(value = "details", layout = MainView.class)
//@Theme("Account details")
public class AccountOperationsView extends VerticalLayout implements BeforeEnterObserver {

    HorizontalLayout accountHeader = new HorizontalLayout();
    HorizontalLayout operationsInfo = new HorizontalLayout();
    VerticalLayout operationLayout = new VerticalLayout();
    private AccountEntity currentAccount;
    private List<TransferEntity> operationList;
    private Icon angleLeft = new Icon(VaadinIcon.ANGLE_LEFT);
    private Label pageNumber = new Label("1");
    private Icon angleRight = new Icon(VaadinIcon.ANGLE_RIGHT);
    private Select<Integer> operationPerPage = new Select<>();

    public AccountOperationsView() {

        String accountResponseDto = (String) ComponentUtil.getData(UI.getCurrent(), "accountResponse");
        Gson gson = new Gson();
        AccountEntityResponseDto accountEntityResponseDto = gson.fromJson(accountResponseDto, AccountEntityResponseDto.class);
        this.currentAccount = accountEntityResponseDto.getAccount();
        this.operationList = accountEntityResponseDto.getOperations();
        operationPerPage.setItems(new ArrayList<>(Arrays.asList(10, 25, 50)));
        operationPerPage.setValue(10);
        operationPerPage.setLabel("Operations per page");
        configureAccountHeader();
        configureOperationsInfo(operationPerPage.getValue());
        configureIcons();

        add(
                accountHeader,
                operationsInfo
        );
    }

    private void configureIcons() {
        angleLeft.addClickListener(click -> {
            if (Integer.parseInt(pageNumber.getText()) > 1) {
                pageNumber.setText(String.valueOf(Integer.parseInt(pageNumber.getText()) - 1));
            }
            printOperations();
        });
        operationPerPage.addValueChangeListener(change -> {
            pageNumber.setText("1");
            printOperations();
        });
        angleRight.addClickListener(click -> {
            if ((Integer.parseInt(pageNumber.getText()) <= operationList.size() / operationPerPage.getValue())
                    && (operationList.size() % operationPerPage.getValue()) > 0) {
                pageNumber.setText(String.valueOf(Integer.parseInt(pageNumber.getText()) + 1));
            }
            printOperations();
        });
    }


    private void configureOperationsInfo(Integer value) {
        operationPerPage.clear();
        operationPerPage.setItems(new ArrayList<>(Arrays.asList(10, 25, 50)));
        operationPerPage.setValue(value);
        VerticalLayout toolbar = configureToolbar();
        printOperations();

        operationLayout.setWidthFull();

        operationsInfo.add(
                operationLayout,
                toolbar
        );
        operationsInfo.setFlexGrow(4, operationLayout);
        operationsInfo.setFlexGrow(1, toolbar);
        operationsInfo.setSizeFull();

    }

    private void printOperations() {
        operationLayout.removeAll();
        Integer rightBorder = Integer.min(operationList.size(), operationPerPage.getValue() * Integer.parseInt(pageNumber.getText()));
        int start_index;
        if (Integer.parseInt(pageNumber.getText()) > 1) {
            start_index = Integer.parseInt(pageNumber.getText()) * operationPerPage.getValue() - operationPerPage.getValue();
//            rightBorder = Integer.min(operationList.size() + operationPerPage.getValue(), operationPerPage.getValue() * Integer.parseInt(pageNumber.getText()));
        } else {
            start_index = 0;
        }
        for (int i = start_index; i < rightBorder; i++) {
            TransferEntity entity = operationList.get(i);
            HorizontalLayout layout = defineOperationLayout(entity);
            layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
            layout.setWidthFull();
            operationLayout.add(layout);
        }

    }

    private VerticalLayout configureToolbar() {
        VerticalLayout toolbar = new VerticalLayout();
        toolbar.setWidth("20em");

        HorizontalLayout bottomLayout = new HorizontalLayout();
        operationPerPage.clear();
        operationPerPage.setItems(new ArrayList<>(Arrays.asList(10, 25, 50)));
        operationPerPage.setValue(10);
        bottomLayout.add(angleLeft, pageNumber, angleRight, operationPerPage);
        bottomLayout.setAlignItems(Alignment.CENTER);
        bottomLayout.setSizeFull();

        toolbar.add(bottomLayout);
        toolbar.setAlignSelf(Alignment.END, bottomLayout);

        toolbar.setHeightFull();

        return toolbar;

    }

    private HorizontalLayout defineOperationLayout(TransferEntity entity) {
        HorizontalLayout layout = new HorizontalLayout();
        Label sum = new Label("" + setMark(entity));
        Label type = new Label("" + entity.getOperation());
        Label recipientOrSender = new Label();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Label date = new Label("Made on: " + dateFormat.format(entity.getDate()));
        if ("OUT".equals(entity.getOperation())) {
            recipientOrSender.setText("To: " + entity.getRecipient());
        } else {
            recipientOrSender.setText("From: " + entity.getSender());
        }
        layout.add(sum, type, recipientOrSender, date);
        return layout;
    }

    private String setMark(TransferEntity entity) {
        if ("OUT".equals(entity.getOperation())) {
            return "" + (entity.getSum() - 2 * entity.getSum());
        }
        return "" + entity.getSum();
    }

    private void configureAccountHeader() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(
                new Label(currentAccount.getCurrency()),
                new Label("Account id: " + currentAccount.getObjId())
        );
        VerticalLayout balance = new VerticalLayout(
                new Label("Balance: " + currentAccount.getBalance())
        );
        verticalLayout.setSizeFull();
        balance.setWidth("20em");
        accountHeader.add(
                verticalLayout,
                balance
        );
        accountHeader.setFlexGrow(4, verticalLayout);
        accountHeader.setFlexGrow(1, balance);
        accountHeader.setSizeFull();
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
