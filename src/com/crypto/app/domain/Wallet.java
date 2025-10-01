package com.crypto.app.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Wallet implements Identifiable {
    private final String id;
    private final CryptoType type;
    private final String address;
    private BigDecimal balance;

    public Wallet(CryptoType type, String address) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.address = address;
        this.balance = BigDecimal.ZERO;
    }

    public String getId() { return id; }
    public CryptoType getType() { return type; }
    public String getAddress() { return address; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    @Override
    public String toString() {
        return "Wallet{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", address='" + address + '\'' +
                ", balance=" + balance +
                '}';
    }
}



