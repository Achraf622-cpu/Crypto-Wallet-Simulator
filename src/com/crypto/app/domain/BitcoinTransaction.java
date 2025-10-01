package com.crypto.app.domain;

import java.math.BigDecimal;

public class BitcoinTransaction extends Transaction {
    public BitcoinTransaction(String from, String to, BigDecimal amount, Priority priority) {
        super(CryptoType.BITCOIN, from, to, amount, priority);
        setFee(calculateFee());
    }

    @Override
    public BigDecimal calculateFee() {
        // Fee = estimatedSizeBytes * satoshiPerByte
        // Use realistic-ish fictional values depending on priority
        int estimatedSizeBytes = 250; // average small tx
        int satoshiPerByte;
        switch (getPriority()) {
            case RAPIDE:
                satoshiPerByte = 50; // fast
                break;
            case STANDARD:
                satoshiPerByte = 20;
                break;
            default:
                satoshiPerByte = 5; // economical
        }
        // Convert satoshis to BTC: 1 BTC = 100_000_000 satoshis
        long satoshis = (long) estimatedSizeBytes * satoshiPerByte;
        return new BigDecimal(satoshis).divide(new BigDecimal(100_000_000L), 8, java.math.RoundingMode.HALF_UP);
    }
}





