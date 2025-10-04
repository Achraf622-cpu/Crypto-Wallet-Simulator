package com.crypto.app.service;

import com.crypto.app.db.Database;
import com.crypto.app.domain.Transaction;
import com.crypto.app.domain.Wallet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbPersistenceService {
    private final Database db = Database.getInstance();

    public void saveWallet(Wallet w) {
        try {
            Connection c = db.getConnection();
            PreparedStatement ps = c.prepareStatement("INSERT INTO wallets (id, type, address, balance) VALUES (?, ?, ?, ?)\nON CONFLICT (id) DO UPDATE SET type = EXCLUDED.type, address = EXCLUDED.address, balance = EXCLUDED.balance");
            ps.setString(1, w.getId());
            ps.setString(2, w.getType().name());
            ps.setString(3, w.getAddress());
            ps.setBigDecimal(4, w.getBalance());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveTransaction(Transaction t, String walletId) {
        try {
            Connection c = db.getConnection();
            PreparedStatement ps = c.prepareStatement("INSERT INTO transactions (id, wallet_id, crypto_type, from_address, to_address, amount, priority, fee, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)\nON CONFLICT (id) DO UPDATE SET wallet_id = EXCLUDED.wallet_id, crypto_type = EXCLUDED.crypto_type, from_address = EXCLUDED.from_address, to_address = EXCLUDED.to_address, amount = EXCLUDED.amount, priority = EXCLUDED.priority, fee = EXCLUDED.fee, status = EXCLUDED.status");
            ps.setString(1, t.getId());
            ps.setString(2, walletId);
            ps.setString(3, t.getType().name());
            ps.setString(4, t.getFromAddress());
            ps.setString(5, t.getToAddress());
            ps.setBigDecimal(6, t.getAmount());
            ps.setString(7, t.getPriority().name());
            ps.setBigDecimal(8, t.getFee());
            ps.setString(9, t.getStatus().name());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}




