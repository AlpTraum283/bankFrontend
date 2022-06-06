package org.example.views.user;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.example.model.TransferEntity;

import java.util.List;

public class AccountDetailsForm extends VerticalLayout {
    Label history = new Label("История");
    List<TransferEntity> operations;

    public AccountDetailsForm() {
    }

    public AccountDetailsForm(List<TransferEntity> operations) {
        this.operations = operations;
        add(history);
        displayRecentOperations();

    }

    public void displayRecentOperations() {
        int border = 3;
        if (operations.size() > 3) {

        } else {
            border = operations.size();
        }

        for (int i = 0; i < border; i++) {
            TransferEntity transferEntity = operations.get(i);
            VerticalLayout verticalLayout = new VerticalLayout(
                    new Label("Отправитель:  " + transferEntity.getSender()),
                    new Label("Сумма:  " + transferEntity.getSum()),
                    new Label("Тип операции:  " + transferEntity.getOperation())

            );
            add(verticalLayout);
        }

    }
}
