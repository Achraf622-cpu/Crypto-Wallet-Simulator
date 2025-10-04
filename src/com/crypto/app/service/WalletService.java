package com.crypto.app.service;

import com.crypto.app.domain.CryptoType;
import com.crypto.app.domain.Wallet;
import com.crypto.app.repository.InMemoryWalletRepository;
import com.crypto.app.util.RandomData;

public class WalletService {
    private final InMemoryWalletRepository walletRepository;

    public WalletService(InMemoryWalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet createWallet(CryptoType type) {
        String address = type == CryptoType.BITCOIN
                ? RandomData.randomBitcoinAddress()
                : RandomData.randomEthereumAddress();
        Wallet w = new Wallet(type, address);
        walletRepository.save(w);
        return w;
    }

    public Wallet findById(String id) {
        return walletRepository.findById(id);
    }
}







