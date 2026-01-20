package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class GeoIpBlockingModule implements Module, Listener {
    private final AuthLite plugin;

    public GeoIpBlockingModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.geoip-blocking", false);
    }

    @Override
    public void enable() {
        if (isEnabled()) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        // Проверка через GeoIpService (если файл базы существует)
        // Пример: если страна в blocked-countries → e.disallow(...)
    }
}
