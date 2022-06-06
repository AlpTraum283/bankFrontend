package org.example.model;

import com.google.gson.Gson;
import org.example.annotation.Attribute;

import java.util.Date;

public class AccountEntity extends BasicEntity{

    @Attribute(value = 6)
    private String currency;

    @Attribute(value = 1)
    private long balance;

    @Attribute(value = 2)
    private long draft;

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void setDraft(long draft) {
        this.draft = draft;
    }

    public String getCurrency() {
        return currency;
    }

    public long getBalance() {
        return balance;
    }

    public long getDraft() {
        return draft;
    }

    public AccountEntity() {
    }

    public AccountEntity(Integer objId, Integer owner, String name, Date date, String type) {
        super(objId, owner, name, date, type);
    }

    public AccountEntity(int owner, String name, Date date, String type, String currency, long balance, long draft) {
        super(owner, name, date, type);
        this.currency = currency;
        this.balance = balance;
        this.draft = draft;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();

        return gson.toJson(this);
    }

}
