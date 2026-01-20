package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import java.util.HashMap;
import java.util.Map;

public class HumanityCheckModule implements Module, Listener {
    private final AuthLite plugin;
    private final Map<String, Long> joinTimes = new HashMap<>();

    public HumanityCheckModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.humanity-check", true);
    }

    @Override
    public void enable() {
        if (isEnabled()) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        joinTimes.put(e.getPlayer().getName(), System.currentTimeMillis());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!e.getMessage().startsWith("/register ")) return;
        Player p = e.getPlayer();
        Long joinTime = joinTimes.get(p.getName());
        if (joinTime != null && System.currentTimeMillis() - joinTime < 3000) {
            e.setCancelled(true);
            p.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(
                "<red>Please wait a moment before registering.</red>"
            ));
        }
    }
}
