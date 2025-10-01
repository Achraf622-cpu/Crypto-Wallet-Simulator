package com.crypto.app.repository;

import com.crypto.app.domain.Identifiable;

import java.util.List;

public interface Repository<T extends Identifiable> {
    T save(T entity);
    T findById(String id);
    List<T> findAll();
}





