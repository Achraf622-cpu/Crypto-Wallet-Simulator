package com.crypto.app.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EthereumTransaction extends Transaction {
    public EthereumTransaction(String from, String to, BigDecimal amount, Priority priority) {
        super(CryptoType.ETHEREUM, from, to, amount, priority);
        setFee(calculateFee());
    }

    @Override
    public BigDecimal calculateFee() {
        // Fee = gasLimit * gasPrice (in ETH). We'll simulate with fictional values.
        long gasLimit = 21000; // standard ETH transfer
        long gasPriceGwei;
        switch (getPriority()) {
            case RAPIDE:
                gasPriceGwei = 80; // fast
                break;
            case STANDARD:
                gasPriceGwei = 30;
                break;
            default:
                gasPriceGwei = 10; // economical
        }
        // Convert gwei to ETH: 1 ETH = 1e9 gwei
        BigDecimal gas = new BigDecimal(gasLimit);
        BigDecimal priceEth = new BigDecimal(gasPriceGwei).divide(new BigDecimal(1_000_000_000L), 9, RoundingMode.HALF_UP);
        return gas.multiply(priceEth).setScale(9, RoundingMode.HALF_UP);
    }
}





