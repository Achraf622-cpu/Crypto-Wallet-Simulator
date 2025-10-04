package com.crypto.app.service;

import com.crypto.app.db.Database;
import com.crypto.app.domain.Transaction;
import com.crypto.app.domain.Wallet;
import com.crypto.app.domain.TransactionStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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

    public void updateTransactionStatus(String txId, TransactionStatus status) {
        try {
            Connection c = db.getConnection();
            PreparedStatement ps = c.prepareStatement("UPDATE transactions SET status = ? WHERE id = ?");
            ps.setString(1, status.name());
            ps.setString(2, txId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateWalletBalance(String walletId, java.math.BigDecimal delta) {
        try {
            Connection c = db.getConnection();
            // add delta to balance
            PreparedStatement ps = c.prepareStatement("UPDATE wallets SET balance = balance + ? WHERE id = ?");
            ps.setBigDecimal(1, delta);
            ps.setString(2, walletId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String[]> listWallets() {
        try {
            Connection c = db.getConnection();
            java.sql.PreparedStatement ps = c.prepareStatement("SELECT id, type, address, balance FROM wallets ORDER BY id");
            ResultSet rs = ps.executeQuery();
            List<String[]> rows = new ArrayList<String[]>();
            while (rs.next()) {
                rows.add(new String[] {
                        rs.getString("id"),
                        rs.getString("type"),
                        rs.getString("address"),
                        rs.getBigDecimal("balance").toPlainString()
                });
            }
            rs.close();
            ps.close();
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String[]> listTransactionsByWallet(String walletId) {
        try {
            Connection c = db.getConnection();
            java.sql.PreparedStatement ps = c.prepareStatement(
                    "SELECT id, crypto_type, from_address, to_address, amount, priority, fee, status, created_at " +
                            "FROM transactions WHERE wallet_id = ? ORDER BY created_at DESC");
            ps.setString(1, walletId);
            ResultSet rs = ps.executeQuery();
            List<String[]> rows = new ArrayList<String[]>();
            while (rs.next()) {
                rows.add(new String[] {
                        rs.getString("id"),
                        rs.getString("crypto_type"),
                        rs.getString("from_address"),
                        rs.getString("to_address"),
                        rs.getBigDecimal("amount").toPlainString(),
                        rs.getString("priority"),
                        rs.getBigDecimal("fee").toPlainString(),
                        rs.getString("status"),
                        String.valueOf(rs.getTimestamp("created_at"))
                });
            }
            rs.close();
            ps.close();
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}




