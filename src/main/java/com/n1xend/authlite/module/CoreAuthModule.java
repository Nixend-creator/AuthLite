package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;
import com.n1xend.authlite.command.*;
import com.n1xend.authlite.listener.PlayerJoinListener;
import com.n1xend.authlite.listener.ProtectionListener;
import org.bukkit.Bukkit;

public class CoreAuthModule implements Module {
    private final AuthLite plugin;

    public CoreAuthModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.core-auth", true);
    }

    @Override
    public void enable() {
        if (!isEnabled()) return;
        plugin.getCommand("register").setExecutor(new RegisterCommand(plugin));
        plugin.getCommand("login").setExecutor(new LoginCommand(plugin));
        plugin.getCommand("changepassword").setExecutor(new ChangePasswordCommand(plugin));
        plugin.getCommand("auth").setExecutor(new AuthCommand(plugin));
        plugin.getCommand("authsessions").setExecutor(new AuthSessionsCommand(plugin));
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new ProtectionListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new BruteForceListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new SessionLoggerListener(plugin), plugin);
    }
}
