package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class BruteForceProtectionModule implements Module, Listener {
    private final AuthLite plugin;

    public BruteForceProtectionModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.brute-force-protection", true);
    }

    @Override
    public void enable() {
        if (isEnabled()) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        // Здесь можно добавить проверку по IP (например, через Redis или in-memory map)
        // Для простоты — логирование уже в LoginCommand
    }
}
