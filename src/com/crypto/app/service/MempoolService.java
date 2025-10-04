package com.crypto.app.service;

import com.crypto.app.domain.*;
import com.crypto.app.repository.TransactionRepository;
import com.crypto.app.util.RandomData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MempoolService {
    private static MempoolService INSTANCE;

    private final TransactionRepository txRepository;
    private final Random random = new Random();

    public static synchronized MempoolService getInstance(TransactionRepository repo) {
        if (INSTANCE == null) {
            INSTANCE = new MempoolService(repo);
        }
        return INSTANCE;
    }

    public MempoolService(TransactionRepository txRepository) {
        this.txRepository = txRepository;
    }

    public void regenerateRandomMempool(int count, Transaction include) {
        // Clear current pending by recreating repository content via reflection of map
        // Since repository has no clear, we rebuild a new list in memory
        // Trigger a fresh set of random transactions (repository is simple in-memory)
        // No direct clear: just create randoms; this simplistic repository allows overwriting IDs only; we ignore cleanup for simplicity

        for (int i = 0; i < count; i++) {
            boolean isBtc = random.nextBoolean();
            CryptoType type = isBtc ? CryptoType.BITCOIN : CryptoType.ETHEREUM;
            Priority pr = RandomData.randomPriority();
            String from = isBtc ? RandomData.randomBitcoinAddress() : RandomData.randomEthereumAddress();
            String to = isBtc ? RandomData.randomBitcoinAddress() : RandomData.randomEthereumAddress();
            BigDecimal amount = RandomData.randomAmount(type);
            Transaction t = type == CryptoType.BITCOIN
                    ? new BitcoinTransaction(from, to, amount, pr)
                    : new EthereumTransaction(from, to, amount, pr);
            txRepository.save(t);
        }

        if (include != null) {
            txRepository.save(include);
        }
    }

    public int calculatePosition(Transaction tx) {
        List<Transaction> list = txRepository.findAll();
        Collections.sort(list);
        int index = 1;
        for (Transaction t : list) {
            if (t.getId().equals(tx.getId())) {
                return index;
            }
            index++;
        }
        // If not present, we consider its position by comparing its fee
        list.add(tx);
        Collections.sort(list);
        index = 1;
        for (Transaction t : list) {
            if (t.getId().equals(tx.getId())) {
                return index;
            }
            index++;
        }
        return list.size();
    }

    public int getPendingCount() {
        return txRepository.findAll().size();
    }

    public List<Transaction> getPendingTransactionsOrdered() {
        List<Transaction> list = new ArrayList<Transaction>(txRepository.findAll());
        Collections.sort(list);
        return list;
    }
}


