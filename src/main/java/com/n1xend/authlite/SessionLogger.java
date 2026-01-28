package com.n1xend.authlite;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class SessionLogger {
    private final AuthLite plugin;
    private final File sessionsFile;
    private final FileConfiguration sessionsConfig;

    public SessionLogger(AuthLite plugin, File sessionsFile) {
        this.plugin = plugin;
        this.sessionsFile = sessionsFile;
        this.sessionsConfig = YamlConfiguration.loadConfiguration(sessionsFile);
    }

    public void logLogin(Player player) {
        UUID uuid = player.getUniqueId();
        String path = "sessions." + uuid.toString();
        
        sessionsConfig.set(path + ".username", player.getName());
        sessionsConfig.set(path + ".ip", player.getAddress().getAddress().getHostAddress());
        sessionsConfig.set(path + ".loginTime", System.currentTimeMillis());
        sessionsConfig.set(path + ".logoutTime", null);
        
        saveSessions();
        plugin.getStorage().logAuth(player.getName());
    }

    public void logLogout(Player player) {
        UUID uuid = player.getUniqueId();
        String path = "sessions." + uuid.toString();
        
        if (sessionsConfig.contains(path)) {
            sessionsConfig.set(path + ".logoutTime", System.currentTimeMillis());
            saveSessions();
        }
    }

    private void saveSessions() {
        try {
            sessionsConfig.save(sessionsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Ошибка сохранения сессий", e);
        }
    }
}