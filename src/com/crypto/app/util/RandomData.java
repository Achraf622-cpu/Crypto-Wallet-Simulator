package com.crypto.app.util;

import com.crypto.app.domain.CryptoType;
import com.crypto.app.domain.Priority;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public final class RandomData {
    private static final String BTC_CHARS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final String HEX = "0123456789abcdef";
    private static final Random RAND = new Random();

    private RandomData() {}

    public static String randomBitcoinAddress() {
        // Simplified: start with 1 or 3 or bc1; we'll generate base58-like characters
        String prefix = RAND.nextInt(3) == 0 ? "3" : (RAND.nextInt(2) == 0 ? "1" : "bc1");
        int len = prefix.equals("bc1") ? 39 : 33; // typical lengths vary
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < len; i++) {
            sb.append(BTC_CHARS.charAt(RAND.nextInt(BTC_CHARS.length())));
        }
        return sb.toString();
    }

    public static String randomEthereumAddress() {
        StringBuilder sb = new StringBuilder("0x");
        for (int i = 0; i < 40; i++) {
            sb.append(HEX.charAt(RAND.nextInt(HEX.length())));
        }
        return sb.toString();
    }

    public static Priority randomPriority() {
        int r = RAND.nextInt(3);
        return r == 0 ? Priority.ECONOMIQUE : (r == 1 ? Priority.STANDARD : Priority.RAPIDE);
    }

    public static BigDecimal randomAmount(CryptoType type) {
        if (type == CryptoType.BITCOIN) {
            // 0.0001 - 0.5 BTC
            double v = 0.0001 + (0.5 - 0.0001) * RAND.nextDouble();
            return new BigDecimal(v).setScale(8, RoundingMode.HALF_UP);
        } else {
            // 0.001 - 5 ETH
            double v = 0.001 + (5.0 - 0.001) * RAND.nextDouble();
            return new BigDecimal(v).setScale(9, RoundingMode.HALF_UP);
        }
    }
}







