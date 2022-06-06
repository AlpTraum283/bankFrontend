package org.example.model;

import com.google.gson.Gson;

import static org.example.Constants.TRANSACTION_STATUS_NEW;

public class TransferRequestResponseDto {
    private Integer transactionId;

    private String status = TRANSACTION_STATUS_NEW;

    private String message;

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
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

    public TransferRequestResponseDto() {
    }

    public TransferRequestResponseDto(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public TransferRequestResponseDto(Integer transactionId, String status) {
        this.transactionId = transactionId;
        this.status = status;
    }

    public TransferRequestResponseDto(Integer transactionId, String status, String message) {
        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
