package com.crypto.app.repository;

import com.crypto.app.domain.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTransactionRepository implements TransactionRepository {
    private final Map<String, Transaction> store = new HashMap<String, Transaction>();

    @Override
    public Transaction save(Transaction entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Transaction findById(String id) {
        return store.get(id);
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<Transaction>(store.values());
        Collections.sort(list);
        return list;
    }
}


