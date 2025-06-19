package com.luxuryproductsholding.api.dto;

import com.luxuryproductsholding.api.models.Giftcard.GiftcardStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GiftcardDTO {
    public String code;
    public BigDecimal initialValue;
    public BigDecimal currentBalance;
    public LocalDateTime expirationDate;
    public LocalDateTime createdAt;
    public LocalDateTime activatedAt;
    public String message;
    public String recipientEmail;
    public String linkedUserEmail;
    public GiftcardStatus status;

    public GiftcardDTO(String code, BigDecimal initialValue, BigDecimal currentBalance,
                       LocalDateTime expirationDate, LocalDateTime createdAt, LocalDateTime activatedAt,
                       String message, String recipientEmail, String linkedUserEmail, GiftcardStatus status) {
        this.code = code;
        this.initialValue = initialValue;
        this.currentBalance = currentBalance;
        this.expirationDate = expirationDate;
        this.createdAt = createdAt;
        this.activatedAt = activatedAt;
        this.message = message;
        this.recipientEmail = recipientEmail;
        this.linkedUserEmail = linkedUserEmail;
        this.status = status;
    }
}