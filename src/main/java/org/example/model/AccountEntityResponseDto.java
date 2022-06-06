package org.example.model;

import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;

public class AccountEntityResponseDto {

    private AccountEntity account;

    private List<TransferEntity> operations = new ArrayList<>();

    public AccountEntityResponseDto(AccountEntity account, List<TransferEntity> operations) {
        this.account = account;
        this.operations = operations;
    }

    public AccountEntityResponseDto(AccountEntity account) {
        this.account = account;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public List<TransferEntity> getOperations() {
        return operations;
    }

    public void addOperation(TransferEntity entity) {
        this.operations.add(entity);
    }

    public void setOperations(List<TransferEntity> operations) {
        this.operations = operations;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();

        return gson.toJson(this);
    }
}
