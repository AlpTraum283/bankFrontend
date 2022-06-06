package org.example.model;

import com.google.gson.Gson;

public class CreateTransferRequestDto {
    private int sender;

    private long sum;

    private int recipient;

    public CreateTransferRequestDto() {
    }

    public CreateTransferRequestDto(int sender, long sum, int recipient) {
        this.sender = sender;
        this.sum = sum;
        this.recipient = recipient;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public int getRecipient() {
        return recipient;
    }

    public void setRecipient(int recipient) {
        this.recipient = recipient;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();

        return gson.toJson(this);
    }
}
