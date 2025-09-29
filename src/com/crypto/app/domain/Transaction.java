package com.crypto.app.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Transaction implements Identifiable, Comparable<Transaction> {
    private final String id;
    private final CryptoType type;
    private final String fromAddress;
    private final String toAddress;
    private final BigDecimal amount;
    private final Priority priority;
    private final LocalDateTime createdAt;
    private BigDecimal fee;
    private TransactionStatus status;

    protected Transaction(CryptoType type, String fromAddress, String toAddress, BigDecimal amount, Priority priority) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }

    public String getId() { return id; }
    public CryptoType getType() { return type; }
    public String getFromAddress() { return fromAddress; }
    public String getToAddress() { return toAddress; }
    public BigDecimal getAmount() { return amount; }
    public Priority getPriority() { return priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public abstract BigDecimal calculateFee();

    @Override
    public int compareTo(Transaction o) {
        // Higher fee first
        int cmp = o.getFee().compareTo(this.getFee());
        if (cmp != 0) return cmp;
        // Earlier created first
        return this.createdAt.compareTo(o.createdAt);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", from='" + fromAddress + '\'' +
                ", to='" + toAddress + '\'' +
                ", amount=" + amount +
                ", priority=" + priority +
                ", fee=" + fee +
                ", status=" + status +
                '}';
    }
}



