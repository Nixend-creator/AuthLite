package com.n1xend.authlite;

import com.n1xend.authlite.command.*;
import com.n1xend.authlite.listener.PlayerLoginListener;
import com.n1xend.authlite.listener.SessionLoggerListener;
import com.n1xend.authlite.storage.StorageProvider;
import com.n1xend.authlite.storage.YamlStorage;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;

public class AuthLite extends JavaPlugin {
    private StorageProvider storage;
    private SessionLogger sessionLogger;
    private static AuthLite instance;
    private Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        
        // Создаём директорию данных
        getDataFolder().mkdirs();
        
        // Инициализация хранилища
        File authDataFile = new File(getDataFolder(), "auth.yml");
        File sessionsFile = new File(getDataFolder(), "sessions.yml");
        this.storage = new YamlStorage(authDataFile, sessionsFile);
        
        // Инициализация логгера сессий
        this.sessionLogger = new SessionLogger(this, sessionsFile);
        
        // Регистрация команд
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("changepassword").setExecutor(new ChangePasswordCommand(this));
        getCommand("authsessions").setExecutor(new AuthSessionsCommand(this));
        
        // Регистрация листенеров
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(this), this);
        getServer().getPluginManager().registerEvents(new SessionLoggerListener(this), this);
        
        logger.info("AuthLite успешно загружен!");
    }

    @Override
    public void onDisable() {
        logger.info("AuthLite выгружен.");
    }

    // Геттеры для доступа из команд и листенеров
    public StorageProvider getStorage() {
        return storage;
    }

    public SessionLogger getSessionLogger() {
        return sessionLogger;
    }

    public long getLastPasswordChange(UUID uuid) {
        return storage.getLastPasswordChange(uuid);
    }

    public void setLastPasswordChange(UUID uuid, long timestamp) {
        storage.setLastPasswordChange(uuid, timestamp);
    }

    public static AuthLite getInstance() {
        return instance;
    }
}