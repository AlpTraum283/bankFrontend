package org.example.model;

import com.google.gson.Gson;
import org.example.annotation.Attribute;


import java.util.Date;

import static org.example.Constants.TRANSACTION_STATUS_NEW;


public class TransferRequestEntity extends BasicEntity {

    @Attribute(value = 7)
    private int sender;

    @Attribute(value = 8)
    private int recipient;

    @Attribute(value = 5)
    private long sum;

    @Attribute(value = 9)
    private String status = TRANSACTION_STATUS_NEW;

    @Attribute(value = 10)
    private String message = "";

    public TransferRequestEntity() {
    }

    public TransferRequestEntity(Integer owner, String name, Date date, String type, int sender, int recipient, long sum) {
        super(owner, name, date, type);
        this.sender = sender;
        this.recipient = recipient;
        this.sum = sum;
    }

    public TransferRequestEntity(Integer objId, Integer owner, String name, Date date, String type) {
        super(objId, owner, name, date, type);
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getRecipient() {
        return recipient;
    }

    public void setRecipient(int recipient) {
        this.recipient = recipient;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
