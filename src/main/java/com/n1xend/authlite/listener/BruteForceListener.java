package com.n1xend.authlite.listener;

import com.n1xend.authlite.AuthLite;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BruteForceListener implements Listener {
    private final AuthLite plugin;
    private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();
    private final Map<String, Long> lastAttemptTime = new ConcurrentHashMap<>();

    public BruteForceListener(AuthLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        // Можно использовать для ранней блокировки по IP
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String cmd = e.getMessage().toLowerCase();
        String ip = p.getAddress().getAddress().getHostAddress();

        if (!cmd.startsWith("/login ")) return;

        long now = System.currentTimeMillis();
        long lastTime = lastAttemptTime.getOrDefault(ip, 0L);
        int attempts = loginAttempts.getOrDefault(ip, 0);

        // Сброс счётчика, если прошло больше 5 минут
        if (now - lastTime > TimeUnit.MINUTES.toMillis(5)) {
            attempts = 0;
        }

        attempts++;
        lastAttemptTime.put(ip, now);
        loginAttempts.put(ip, attempts);

        // Логирование при подозрении
        if (attempts >= 5) {
            plugin.getStorage().logBruteForce(
                "[BRUTEFORCE] IP=" + ip + " Attempts=" + attempts + " Player=" + p.getName()
            );
            plugin.getLogger().warning("Possible brute-force attack from IP: " + ip);
        }
    }
}
