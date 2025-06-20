package com.luxuryproductsholding.api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gift_cards")
public class Giftcard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private BigDecimal initialValue;

    @Column(nullable = false)
    private BigDecimal currentBalance;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime activatedAt;

    @Column
    private String message;

    @Column
    private String recipientEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private CustomUser linkedUser;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GiftcardStatus status;

    @Column(nullable = false)
    private int failedAttempts;

    @Column
    private LocalDateTime lastFailedAttempt;

    public enum GiftcardStatus {
        ACTIVE,
        EXPIRED,
        USED,
        CANCELLED
    }

    public Giftcard() {
        this.status = GiftcardStatus.ACTIVE;
        this.failedAttempts = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(BigDecimal initialValue) {
        this.initialValue = initialValue;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(LocalDateTime activatedAt) {
        this.activatedAt = activatedAt;
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

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getLastFailedAttempt() {
        return lastFailedAttempt;
    }

    public void setLastFailedAttempt(LocalDateTime lastFailedAttempt) {
        this.lastFailedAttempt = lastFailedAttempt;
    }

    public GiftcardStatus getStatus() {
        return status;
    }

    public void setStatus(GiftcardStatus status) {
        this.status = status;
    }

    public CustomUser getLinkedUser() {
        return linkedUser;
    }

    public void setLinkedUser(CustomUser linkedUser) {
        this.linkedUser = linkedUser;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        expirationDate = createdAt.plusYears(2); // 2 years validity
        status = GiftcardStatus.ACTIVE;
        failedAttempts = 0;
    }
}