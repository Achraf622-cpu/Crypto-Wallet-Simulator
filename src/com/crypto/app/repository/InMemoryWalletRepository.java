package com.crypto.app.repository;

import com.crypto.app.domain.Wallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryWalletRepository implements Repository<Wallet> {
    private final Map<String, Wallet> store = new HashMap<String, Wallet>();

    @Override
    public Wallet save(Wallet entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Wallet findById(String id) {
        return store.get(id);
    }

    @Override
    public List<Wallet> findAll() {
        return new ArrayList<Wallet>(store.values());
    }
}





