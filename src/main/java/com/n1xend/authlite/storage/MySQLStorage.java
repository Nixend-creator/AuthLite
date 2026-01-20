package com.n1xend.authlite.storage;

import com.n1xend.authlite.AuthLite;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Base64;
import java.util.logging.Logger;

public class MySQLStorage implements StorageProvider {

    private final HikariDataSource dataSource;
    private final AuthLite plugin;
    private final String tableName;
    private final Logger logger;

    public MySQLStorage(AuthLite plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        var cfg = plugin.getConfig().getConfigurationSection("storage.mysql");
        this.tableName = cfg.getString("table", "players");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=%s&serverTimezone=UTC",
            cfg.getString("host", "127.0.0.1"),
            cfg.getInt("port", 3306),
            cfg.getString("database", "authlite"),
            cfg.getBoolean("use-ssl", false)
        ));
        config.setUsername(cfg.getString("username", "root"));
        config.setPassword(cfg.getString("password", ""));
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(5000);
        this.dataSource = new HikariDataSource(config);

        createTables();
        logger.info("MySQL storage initialized. Table: " + tableName);
    }

    private void createTables() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS `""" + tableName + """` (
                    `uuid` CHAR(36) NOT NULL PRIMARY KEY,
                    `password_hash` TEXT NOT NULL,
                    `auto_login_token` VARCHAR(64) NULL,
                    `auto_login_expires` BIGINT NULL,
                    `auto_login_ip_hash` VARCHAR(64) NULL,
                    `ip_hash` VARCHAR(64) NOT NULL,
                    `created_at` BIGINT NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS `ip_account_count` (
                    `ip_hash` VARCHAR(64) NOT NULL PRIMARY KEY,
                    `count` INT NOT NULL DEFAULT 1
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """);
        } catch (SQLException e) {
            logger.severe("Failed to create MySQL tables: " + e.getMessage());
        }
    }

    @Override
    public boolean hasAccount(String uuid) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT 1 FROM `" + tableName + "` WHERE uuid = ?")) {
            ps.setString(1, uuid);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            logger.warning("hasAccount failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getPasswordHash(String uuid) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT password_hash FROM `" + tableName + "` WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("password_hash") : null;
        } catch (SQLException e) {
            logger.warning("getPasswordHash failed: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void savePasswordHash(String uuid, String hash, String ip) {
        String ipHash = hashIp(ip);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO `" + tableName + "` (uuid, password_hash, ip_hash, created_at) VALUES (?, ?, ?, ?) " +
                 "ON DUPLICATE KEY UPDATE password_hash = ?")) {
            long now = System.currentTimeMillis();
            ps.setString(1, uuid);
            ps.setString(2, hash);
            ps.setString(3, ipHash);
            ps.setLong(4, now);
            ps.setString(5, hash);
            ps.executeUpdate();
            incrementIpCount(ipHash, conn);
        } catch (SQLException e) {
            logger.severe("savePasswordHash failed: " + e.getMessage());
        }
    }

    private void incrementIpCount(String ipHash, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO ip_account_count (ip_hash, count) VALUES (?, 1) " +
            "ON DUPLICATE KEY UPDATE count = count + 1")) {
            ps.setString(1, ipHash);
            ps.executeUpdate();
        }
    }

    @Override
    public int getAccountCountByIp(String ip) {
        String ipHash = hashIp(ip);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT count FROM ip_account_count WHERE ip_hash = ?")) {
            ps.setString(1, ipHash);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("count") : 0;
        } catch (SQLException e) {
            logger.warning("getAccountCountByIp failed: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public AutoLoginData getAutoLoginData(String uuid) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT auto_login_token, auto_login_expires, auto_login_ip_hash FROM `" + tableName + "` WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String token = rs.getString("auto_login_token");
                Long expires = rs.getLong("auto_login_expires");
                String ipHash = rs.getString("auto_login_ip_hash");
                if (token != null && expires != 0 && ipHash != null) {
                    return new AutoLoginData(token, expires, ipHash);
                }
            }
        } catch (SQLException e) {
            logger.warning("getAutoLoginData failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void saveAutoLogin(String uuid, String token, long expiresAt, String ipHash) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE `" + tableName + "` SET auto_login_token = ?, auto_login_expires = ?, auto_login_ip_hash = ? WHERE uuid = ?")) {
            ps.setString(1, token);
            ps.setLong(2, expiresAt);
            ps.setString(3, ipHash);
            ps.setString(4, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("saveAutoLogin failed: " + e.getMessage());
        }
    }

    @Override
    public void clearAutoLogin(String uuid) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE `" + tableName + "` SET auto_login_token = NULL, auto_login_expires = NULL, auto_login_ip_hash = NULL WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.warning("clearAutoLogin failed: " + e.getMessage());
        }
    }

    @Override
    public void logAuth(String message) {
        writeLog("auth.log", message);
    }

    @Override
    public void logBruteForce(String message) {
        writeLog("bruteforce.log", message);
    }

    private void writeLog(String filename, String message) {
        try {
            Files.write(Paths.get(plugin.getDataFolder().toString(), filename),
                (message + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.warning("Failed to write " + filename + ": " + e.getMessage());
        }
    }

    @Override
    public String hashIp(String ip) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ip.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            logger.warning("Failed to hash IP: " + e.getMessage());
            return ip;
        }
    }
}
