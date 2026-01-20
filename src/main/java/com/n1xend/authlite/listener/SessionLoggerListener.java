package com.n1xend.authlite.listener;

import com.n1xend.authlite.AuthLite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class SessionLoggerListener implements Listener {
    private final AuthLite plugin;
    private final Map<String, Long> sessionStartTimes = new HashMap<>();

    public SessionLoggerListener(AuthLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        sessionStartTimes.put(uuid, System.currentTimeMillis());

        plugin.getSessionLogger().log("SESSION_START", java.util.Map.of(
            "PLAYER", e.getPlayer().getName(),
            "UUID", uuid,
            "IP", maskIp(e.getPlayer().getAddress().getAddress().getHostAddress()),
            "MODE", "manual"
        ));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        Long start = sessionStartTimes.remove(uuid);
        long duration = (start != null) ? (System.currentTimeMillis() - start) / 1000 : 0;

        plugin.getSessionLogger().log("SESSION_END", java.util.Map.of(
            "PLAYER", e.getPlayer().getName(),
            "UUID", uuid,
            "REASON", "disconnect",
            "DURATION_SEC", String.valueOf(duration)
        ));
    }

    private String maskIp(String ip) {
        if (ip == null || ip.isEmpty()) return "unknown";
        if (ip.contains(":")) {
            int lastColon = ip.lastIndexOf(':');
            return lastColon > 0 ? ip.substring(0, lastColon) + ":xxx" : ip;
        } else {
            int lastDot = ip.lastIndexOf('.');
            return lastDot > 0 ? ip.substring(0, lastDot) + ".xxx.xxx" : ip;
        }
    }
}
