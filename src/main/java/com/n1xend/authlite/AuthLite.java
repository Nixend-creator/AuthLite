package com.n1xend.authlite;

import com.n1xend.authlite.module.ModuleManager;
import com.n1xend.authlite.storage.StorageProvider;
import com.n1xend.authlite.storage.YamlStorage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthLite extends JavaPlugin {
    private StorageProvider storage;
    private ModuleManager moduleManager;
    private final Map<UUID, Boolean> loggedIn = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages_en.yml", false);
        saveResource("messages_ru.yml", false);
        saveResource("messages_uk.yml", false);
        saveResource("messages_de.yml", false);
        saveResource("messages_es.yml", false);

        storage = new YamlStorage(this);
        moduleManager = new ModuleManager(this);
        moduleManager.loadModules(); // ← Исправлено: было loadAll()
        moduleManager.enableModules();

        getLogger().info("AuthLite v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        loggedIn.clear();
    }

    public StorageProvider getStorage() {
        return storage;
    }

    public boolean isLoggedIn(UUID uuid) {
        return Boolean.TRUE.equals(loggedIn.get(uuid));
    }

    public void setLoggedIn(UUID uuid, boolean status) {
        loggedIn.put(uuid, status);
    }

    // ← Добавлен геттер
    public ModuleManager getModuleManager() {
        return moduleManager;
    }
}