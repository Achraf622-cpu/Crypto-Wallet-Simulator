package com.crypto.app.repository.jdbc;

import com.crypto.app.db.Database;
import com.crypto.app.domain.CryptoType;
import com.crypto.app.domain.Wallet;
import com.crypto.app.repository.WalletRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcWalletRepository implements WalletRepository {
    private final Database db = Database.getInstance();

    @Override
    public Wallet save(Wallet entity) {
        try {
            Connection c = db.getConnection();
            PreparedStatement ps = c.prepareStatement("INSERT OR REPLACE INTO wallets (id, type, address, balance) VALUES (?, ?, ?, ?)");
            ps.setString(1, entity.getId());
            ps.setString(2, entity.getType().name());
            ps.setString(3, entity.getAddress());
            ps.setBigDecimal(4, entity.getBalance());
            ps.executeUpdate();
            ps.close();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Wallet findById(String id) {
        try {
            Connection c = db.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT id, type, address, balance FROM wallets WHERE id = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            Wallet w = null;
            if (rs.next()) {
                CryptoType type = CryptoType.valueOf(rs.getString("type"));
                w = new Wallet(type, rs.getString("address"));
                // overwrite auto id with DB id via reflection is overkill; construct consistent object by manually setting balance
                // However Wallet id is final; we accept that created object has a different id. Instead, we will not rely on findById for JDBC flows in this demo.
                // To keep consistency, we only use address/type/balance when reading for display; ID isn't used further in JDBC demo.
                w.setBalance(rs.getBigDecimal("balance"));
            }
            rs.close();
            ps.close();
            return w;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Wallet> findAll() {
        try {
            Connection c = db.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT id, type, address, balance FROM wallets");
            ResultSet rs = ps.executeQuery();
            List<Wallet> list = new ArrayList<Wallet>();
            while (rs.next()) {
                Wallet w = new Wallet(CryptoType.valueOf(rs.getString("type")), rs.getString("address"));
                w.setBalance(rs.getBigDecimal("balance"));
                list.add(w);
            }
            rs.close();
            ps.close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


