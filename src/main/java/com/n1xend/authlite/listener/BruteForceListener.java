package com.n1xend.authlite.listener;

import com.n1xend.authlite.AuthLite;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BruteForceListener implements Listener {
    private final AuthLite plugin;
    private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();

    public BruteForceListener(AuthLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String cmd = event.getMessage().toLowerCase();
        String ip = player.getAddress().getAddress().getHostAddress();

        if (!cmd.startsWith("/login ")) return;

        // Подсчёт попыток входа
        int attempts = loginAttempts.getOrDefault(ip, 0) + 1;
        loginAttempts.put(ip, attempts);

        // Логирование при подозрении (5+ попыток)
        if (attempts >= 5) {
            plugin.getStorage().logBruteForce(
                "[BRUTEFORCE] IP=" + ip + " Attempts=" + attempts + " Player=" + player.getName()
            );
            plugin.getLogger().warning("Possible brute-force attack from IP: " + ip);
        }
    }
}