package com.luxuryproductsholding.api.dto;

import java.math.BigDecimal;

public class CreateGiftcardRequest {
    private BigDecimal value;
    private String message;
    private String recipientEmail;

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
}