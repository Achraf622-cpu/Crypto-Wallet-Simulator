package com.crypto.app.service;

import com.crypto.app.domain.*;
import com.crypto.app.repository.TransactionRepository;
// import com.crypto.app.util.RandomData;

// import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
// import java.util.Random;
import java.util.LinkedList;
import java.util.Queue;

public class MempoolService {
    private static MempoolService INSTANCE;

    private final TransactionRepository txRepository;
    // private final Random random = new Random();
    private final Queue<Transaction> deterministicQueue = new LinkedList<Transaction>();
    private final Map<String, Long> txRemainingMinutes = new HashMap<String, Long>();
    private long currentMinutes = 0L;

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
        // Deterministic mode: only enqueue provided transaction
        if (include != null) {
            enqueue(include);
        }
    }

    public void enqueue(Transaction tx) {
        deterministicQueue.add(tx);
        int position = calculatePosition(tx);
        txRemainingMinutes.put(tx.getId(), position * 10L);
    }

    public long getCurrentMinutes() {
        return currentMinutes;
    }

    public void advanceTime(long minutes) {
        if (minutes <= 0) return;
        currentMinutes += minutes;
    }

    public void advanceAndConfirm(long minutes, com.crypto.app.service.DbPersistenceService db) {
        if (minutes <= 0) return;
        currentMinutes += minutes;
        // Decrease remaining time for queued transactions
        for (Transaction t : new ArrayList<Transaction>(deterministicQueue)) {
            Long remain = txRemainingMinutes.get(t.getId());
            if (remain == null) {
                int pos = calculatePosition(t);
                remain = pos * 10L;
            }
            txRemainingMinutes.put(t.getId(), remain - minutes);
        }
        // Confirm transactions whose remaining time elapsed
        List<Transaction> toRemove = new ArrayList<Transaction>();
        for (Transaction t : deterministicQueue) {
            Long remain = txRemainingMinutes.get(t.getId());
            if (remain != null && remain <= 0L) {
                t.setStatus(TransactionStatus.CONFIRMED);
                if (db != null) {
                    db.updateTransactionStatus(t.getId(), TransactionStatus.CONFIRMED);
                }
                toRemove.add(t);
            }
        }
        for (Transaction t : toRemove) {
            deterministicQueue.remove(t);
            txRemainingMinutes.remove(t.getId());
        }
    }

    public Long getRemainingMinutes(String txId) {
        return txRemainingMinutes.get(txId);
    }

    public int calculatePosition(Transaction tx) {
        List<Transaction> list = new ArrayList<Transaction>(txRepository.findAll());
        list.addAll(deterministicQueue);
        Collections.sort(list);
        int index = 1;
        for (Transaction t : list) {
            if (t.getId().equals(tx.getId())) {
                return index;
            }
            index++;
        }
        // If not present, compute hypothetical position without side effects
        list.add(tx);
        Collections.sort(list);
        index = 1;
        for (Transaction t : list) {
            if (t.getId().equals(tx.getId())) return index;
            index++;
        }
        return list.size();
    }

    public int getPendingCount() {
        return txRepository.findAll().size() + deterministicQueue.size();
    }

    public List<Transaction> getPendingTransactionsOrdered() {
        List<Transaction> list = new ArrayList<Transaction>(txRepository.findAll());
        list.addAll(deterministicQueue);
        Collections.sort(list);
        return list;
    }

    public double getTotal
}


