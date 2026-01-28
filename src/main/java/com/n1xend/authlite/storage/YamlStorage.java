package com.n1xend.authlite.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class YamlStorage implements StorageProvider {
    private final File authFile;
    private final File sessionsFile;
    private final FileConfiguration authConfig;
    private final FileConfiguration sessionsConfig;
    private final JavaPlugin plugin;

    public YamlStorage(File authFile, File sessionsFile) {
        this.authFile = authFile;
        this.sessionsFile = sessionsFile;
        this.plugin = JavaPlugin.getPlugin(JavaPlugin.class);
        
        // Создаём файлы если не существуют
        if (!authFile.exists()) {
            try {
                authFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Не удалось создать auth.yml", e);
            }
        }
        if (!sessionsFile.exists()) {
            try {
                sessionsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Не удалось создать sessions.yml", e);
            }
        }
        
        this.authConfig = YamlConfiguration.loadConfiguration(authFile);
        this.sessionsConfig = YamlConfiguration.loadConfiguration(sessionsFile);
    }

    @Override
    public boolean isRegistered(String username) {
        return authConfig.contains(username + ".password");
    }

    @Override
    public String getPasswordHash(String username) {
        return authConfig.getString(username + ".password");
    }

    @Override
    public String getSalt(String username) {
        return authConfig.getString(username + ".salt", "");
    }

    @Override
    public void savePasswordHash(String username, String passwordHash, String salt) {
        authConfig.set(username + ".password", passwordHash);
        authConfig.set(username + ".salt", salt);
        saveAuthFile();
    }

    @Override
    public void logAuth(String username) {
        plugin.getLogger().info("[AuthLite] Пользователь аутентифицирован: " + username);
    }

    @Override
    public long getLastPasswordChange(UUID uuid) {
        return sessionsConfig.getLong("sessions." + uuid.toString() + ".lastPasswordChange", 0L);
    }

    @Override
    public void setLastPasswordChange(UUID uuid, long timestamp) {
        sessionsConfig.set("sessions." + uuid.toString() + ".lastPasswordChange", timestamp);
        saveSessionsFile();
    }

    @Override
    public AutoLoginData getAutoLoginData(String username) {
        String path = username + ".autoLogin";
        if (!authConfig.contains(path)) return null;
        
        String ip = authConfig.getString(path + ".ip");
        long expires = authConfig.getLong(path + ".expires", 0L);
        
        if (ip == null || expires <= System.currentTimeMillis()) {
            authConfig.set(path, null);
            saveAuthFile();
            return null;
        }
        
        return new AutoLoginData(ip, expires);
    }

    @Override
    public void saveAutoLoginData(String username, String ipAddress, long expiresAt) {
        String path = username + ".autoLogin";
        authConfig.set(path + ".ip", ipAddress);
        authConfig.set(path + ".expires", expiresAt);
        saveAuthFile();
    }

    @Override
    public boolean hasAutoLogin(String username, String ipAddress) {
        AutoLoginData data = getAutoLoginData(username);
        return data != null && data.ipAddress().equals(ipAddress);
    }

    private void saveAuthFile() {
        try {
            authConfig.save(authFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Ошибка сохранения auth.yml", e);
        }
    }

    private void saveSessionsFile() {
        try {
            sessionsConfig.save(sessionsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Ошибка сохранения sessions.yml", e);
        }
    }
}