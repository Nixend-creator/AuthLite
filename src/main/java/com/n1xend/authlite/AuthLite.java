package com.n1xend.authlite;

import com.n1xend.authlite.module.ModuleManager;
import com.n1xend.authlite.storage.YamlStorage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthLite extends JavaPlugin {

    private YamlStorage storage;
    private final Set<UUID> loggedInPlayers = new HashSet<>();
    private SessionLogger sessionLogger;
    private ModuleManager moduleManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages_en.yml", false);
        saveResource("messages_ru.yml", false);
        saveResource("messages_uk.yml", false);
        saveResource("messages_de.yml", false);
        saveResource("messages_es.yml", false);

        this.sessionLogger = new SessionLogger(this);
        this.storage = new YamlStorage(this);
        this.moduleManager = new ModuleManager(this);
        moduleManager.loadAll();

        getLogger().info("AuthLite v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        loggedInPlayers.clear();
    }

    public YamlStorage getStorage() { return storage; }
    public SessionLogger getSessionLogger() { return sessionLogger; }
    public boolean isLoggedIn(UUID uuid) { return loggedInPlayers.contains(uuid); }
    public void setLoggedIn(UUID uuid, boolean logged) {
        if (logged) loggedInPlayers.add(uuid);
        else loggedInPlayers.remove(uuid);
    }
}
