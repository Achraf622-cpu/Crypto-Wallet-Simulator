package com.crypto.app.service;

import com.crypto.app.domain.*;
import com.crypto.app.repository.TransactionRepository;
import com.crypto.app.repository.InMemoryWalletRepository;
import com.crypto.app.util.InputValidator;

import java.math.BigDecimal;

public class TransactionService {
    private final TransactionRepository txRepository;
    private final InMemoryWalletRepository walletRepository;

    public TransactionService(TransactionRepository txRepository, InMemoryWalletRepository walletRepository) {
        this.txRepository = txRepository;
        this.walletRepository = walletRepository;
    }

    public Transaction createTransaction(Wallet wallet, String from, String to, BigDecimal amount, Priority priority) {
        if (!InputValidator.isValidAddress(from, wallet.getType())) {
            throw new IllegalArgumentException("Adresse source invalide");
        }
        if (!InputValidator.isValidAddress(to, wallet.getType())) {
            throw new IllegalArgumentException("Adresse destination invalide");
        }
        Transaction tx = buildTransaction(wallet.getType(), from, to, amount, priority);
        txRepository.save(tx);
        return tx;
    }

    public Transaction createDraftTransaction(Wallet wallet, String from, String to, BigDecimal amount, Priority priority) {
        return buildTransaction(wallet.getType(), from, to, amount, priority);
    }

    private Transaction buildTransaction(CryptoType type, String from, String to, BigDecimal amount, Priority priority) {
        if (type == CryptoType.BITCOIN) {
            return new BitcoinTransaction(from, to, amount, priority);
        } else {
            return new EthereumTransaction(from, to, amount, priority);
        }
    }

    public Transaction findById(String id) {
        return txRepository.findById(id);
    }

    public InMemoryWalletRepository getWalletRepository() {
        return walletRepository;
    }
}







