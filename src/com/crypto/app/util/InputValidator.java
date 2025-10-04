package com.crypto.app.util;

import com.crypto.app.domain.CryptoType;

public final class InputValidator {
    private InputValidator() {}

    public static boolean isValidAddress(String address, CryptoType type) {
        if (address == null) return false;
        if (type == CryptoType.ETHEREUM) {
            return address.matches("0x[0-9a-fA-F]{40}");
        } else {
            // Simplified Bitcoin formats: starts with 1, 3, or bc1
            return address.startsWith("1") || address.startsWith("3") || address.startsWith("bc1");
        }
    }
}







