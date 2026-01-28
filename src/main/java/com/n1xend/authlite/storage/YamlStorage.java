package com.n1xend.authlite.storage;

import com.n1xend.authlite.AuthLite;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class YamlStorage implements StorageProvider {
    private final AuthLite plugin;
    private final File playersFile;
    private YamlConfiguration playersConfig;

    public YamlStorage(AuthLite plugin) {
        this.plugin = plugin;
        this.playersFile = new File(plugin.getDataFolder(), "players.yml");
        reloadPlayersConfig();
    }

    private void reloadPlayersConfig() {
        if (!playersFile.exists()) {
            plugin.saveResource("players.yml", false);
        }
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    }

    private void savePlayersConfig() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save players.yml: " + e.getMessage());
        }
    }

    @Override
    public String getPasswordHash(String uuid) {
        return playersConfig.getString(uuid + ".password");
    }

    @Override
    public void savePasswordHash(String uuid, String passwordHash) {
        playersConfig.set(uuid + ".password", passwordHash);
        savePlayersConfig();
    }

    @Override
    public void saveAutoLogin(String uuid, String token, long expiresAt, String ipHash) {
        playersConfig.set(uuid + ".auto-login.token", token);
        playersConfig.set(uuid + ".auto-login.expires-at", expiresAt);
        playersConfig.set(uuid + ".auto-login.ip-hash", ipHash);
        savePlayersConfig();
    }

    @Override
    public AutoLoginData getAutoLoginData(String uuid) {
        String token = playersConfig.getString(uuid + ".auto-login.token");
        if (token == null) return null;
        long expiresAt = playersConfig.getLong(uuid + ".auto-login.expires-at");
        String ipHash = playersConfig.getString(uuid + ".auto-login.ip-hash");
        return new AutoLoginData(token, expiresAt, ipHash);
    }

    @Override
    public String hashIp(String ip) {
        return Integer.toString(ip.hashCode());
    }

    @Override
    public void logBruteForce(String message) {
        // Можно реализовать запись в bruteforce.log
    }

    // Вложенный класс для данных автологина
    public static class AutoLoginData {
        public final String token;
        public final long expiresAt;
        public final String ipHash;

        public AutoLoginData(String token, long expiresAt, String ipHash) {
            this.token = token;
            this.expiresAt = expiresAt;
            this.ipHash = ipHash;
        }
    }
}