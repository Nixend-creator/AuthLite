package com.n1xend.authlite.listener;

import com.n1xend.authlite.AuthLite;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final AuthLite plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public PlayerJoinListener(AuthLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        var uuid = player.getUniqueId().toString();
        var ip = player.getAddress().getAddress().getHostAddress();

        // Попытка автологина
        var autoLoginData = plugin.getStorage().getAutoLoginData(uuid);
        // PlayerJoinListener.java
if (autoLoginData != null) {
    long now = System.currentTimeMillis();
    if (now < autoLoginData.expiresAt) {
        String currentIpHash = plugin.getStorage().hashIp(ip);
        if (currentIpHash.equals(autoLoginData.ipHash)) {
            plugin.setLoggedIn(player.getUniqueId(), true);
            
            // ЕДИНСТВЕННОЕ сообщение — через локализацию
            String msg = getLocalizedMessage(player, "auto-login-success");
            player.sendMessage(mm.deserialize(msg));
            return;
        }
    }
}

        // Обычный вход
        int timeout = plugin.getConfig().getInt("login-timeout", 60);
        player.sendMessage(mm.deserialize(
            plugin.getConfig().getString("messages.prefix", "") +
            "<red>Please log in within " + timeout + " seconds: /login <password>"
        ));
    }
}
