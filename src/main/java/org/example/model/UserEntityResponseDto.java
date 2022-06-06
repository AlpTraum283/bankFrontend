package org.example.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class UserEntityResponseDto {
    private UserEntity user;

    private List<AccountEntity> accounts = new ArrayList<>();

    public UserEntityResponseDto(UserEntity user, List<AccountEntity> accounts) {
        this.user = user;
        this.accounts = accounts;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<AccountEntity> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountEntity> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();

        return gson.toJson(this);
    }
}
