package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;
import java.util.regex.Pattern;

public class RestrictedUserProtectionModule implements Module, Listener {
    private final AuthLite plugin;
    private boolean enabled;
    private List<String> entries;

    public RestrictedUserProtectionModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.restricted-user-protection", false);
    }

    @Override
    public void enable() {
        if (!isEnabled()) return;

        enabled = true;
        entries = plugin.getConfig().getStringList("security.restricted-users.entries");

        if (entries.isEmpty()) {
            plugin.getLogger().warning("RestrictedUserProtection enabled but no entries defined!");
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("RestrictedUserProtectionModule enabled.");
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!enabled) return;

        String name = event.getName().toLowerCase();
        String ip = event.getAddress().getHostAddress();

        for (String entry : entries) {
            String[] parts = entry.split(";", 2);
            if (parts.length != 2) continue;

            String restrictedName = parts[0].toLowerCase();
            String allowedPattern = parts[1];

            if (!name.equals(restrictedName)) continue;

            if (isIpAllowed(ip, allowedPattern)) {
                // Допущен
                return;
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    "Unauthorized use of restricted name '" + event.getName() + "'");
                plugin.getLogger().warning("Kicked player '" + event.getName() +
                    "' (IP: " + ip + ") for using restricted name from untrusted IP.");
                return;
            }
        }
    }

    private boolean isIpAllowed(String ip, String pattern) {
        if (pattern.startsWith("regex:")) {
            try {
                String regex = pattern.substring(6);
                return Pattern.compile(regex).matcher(ip).matches();
            } catch (Exception e) {
                plugin.getLogger().severe("Invalid regex in restricted user pattern: " + pattern);
                return false;
            }
        } else if (pattern.contains("*")) {
            // Wildcard: 127.0.0.*
            String regex = "^" + Pattern.quote(pattern).replace("\\*", ".*") + "$";
            return Pattern.compile(regex).matcher(ip).matches();
        } else {
            // Точный IP
            return ip.equals(pattern);
        }
    }
}