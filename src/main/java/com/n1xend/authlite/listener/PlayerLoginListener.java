package com.n1xend.authlite.listener;

import com.n1xend.authlite.AuthLite;
import com.n1xend.authlite.session.SessionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLoginListener implements Listener {

    private final AuthLite plugin;
    private final SessionManager sessionManager;

    public PlayerLoginListener(AuthLite plugin, SessionManager sessionManager) {
        this.plugin = plugin;
        this.sessionManager = sessionManager;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        // Проверка сессии происходит здесь или в другом листенере — базовая заглушка
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        sessionManager.removeSession(event.getPlayer().getUniqueId());
    }
}