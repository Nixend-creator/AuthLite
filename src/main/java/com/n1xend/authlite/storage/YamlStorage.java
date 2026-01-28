package com.n1xend.authlite.storage;

import com.n1xend.authlite.AuthLite;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.security.MessageDigest;

public class YamlStorage implements StorageProvider {
    private final AuthLite plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;

    public YamlStorage(AuthLite plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "players.yml");
        reload();
    }

    public void reload() {
        plugin.getDataFolder().mkdirs();
        if (!dataFile.exists()) {
            try { dataFile.createNewFile(); } catch (IOException e) { /* ignore */ }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    @Override
    public boolean hasAccount(String uuid) {
        return dataConfig.contains(uuid);
    }

    @Override
    public String getPasswordHash(String uuid) {
        return dataConfig.getString(uuid + ".password");
    }

    @Override
    public void savePasswordHash(String uuid, String passwordHash) {
    // Убери третий параметр (ip)
    playersConfig.set(uuid + ".password", passwordHash);
    savePlayersConfig();
}
    }

    private void incrementIpCount(String ipHash) {
        String path = "ip-account-count." + ipHash;
        int current = dataConfig.getInt(path, 0);
        dataConfig.set(path, current + 1);
    }

    @Override
    public int getAccountCountByIp(String ip) {
        String ipHash = hashIp(ip);
        return dataConfig.getInt("ip-account-count." + ipHash, 0);
    }

    @Override
    public AutoLoginData getAutoLoginData(String uuid) {
        String token = dataConfig.getString(uuid + ".auto-login.token");
        Long expires = dataConfig.getLong(uuid + ".auto-login.expires-at", 0L);
        String ipHash = dataConfig.getString(uuid + ".auto-login.ip-hash");
        if (token != null && expires != 0 && ipHash != null) {
            return new AutoLoginData(token, expires, ipHash);
        }
        return null;
    }

    @Override
    public void saveAutoLogin(String uuid, String token, long expiresAt, String ipHash) {
        dataConfig.set(uuid + ".auto-login.token", token);
        dataConfig.set(uuid + ".auto-login.expires-at", expiresAt);
        dataConfig.set(uuid + ".auto-login.ip-hash", ipHash);
        save();
    }

    @Override
    public void clearAutoLogin(String uuid) {
        dataConfig.set(uuid + ".auto-login", null);
        save();
    }

    @Override
    public void logAuth(String message) { writeLog("auth.log", message); }
    @Override
    public void logBruteForce(String message) { writeLog("bruteforce.log", message); }

    private void writeLog(String filename, String message) {
        try {
            File logFile = new File(plugin.getDataFolder(), filename);
            java.nio.file.Files.write(logFile.toPath(), (message + System.lineSeparator()).getBytes(java.nio.charset.StandardCharsets.UTF_8),
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException ignored) {}
    }

    @Override
    public String hashIp(String ip) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ip.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return ip;
        }
    }

    private void save() {
        try { dataConfig.save(dataFile); } catch (IOException ignored) {}
    }
}
