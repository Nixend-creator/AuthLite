package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class IpAccountLimitModule implements Module, Listener {
    private final AuthLite plugin;

    public IpAccountLimitModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.ip-account-limit", false);
    }

    @Override
    public void enable() {
        if (!isEnabled()) return;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String cmd = event.getMessage().toLowerCase();

        if (!cmd.startsWith("/register ")) return;
        if (player.isOp()) return;

        String ip = player.getAddress().getAddress().getHostAddress();
        int maxAccounts = plugin.getConfig().getInt("security.max-accounts-per-ip", 5);
        if (maxAccounts <= 0) return;

        int currentCount = plugin.getStorage().getAccountCountByIp(ip);
        if (currentCount >= maxAccounts) {
            event.setCancelled(true);
            player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(
                "<red>Too many accounts from your IP. Max: " + maxAccounts + "</red>"
            ));
            plugin.getLogger().info("Blocked registration from IP " + ip + " (limit: " + maxAccounts + ")");
        }
    }
}
