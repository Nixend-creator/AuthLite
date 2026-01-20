package com.n1xend.authlite.listener;

import com.n1xend.authlite.AuthLite;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

public class ProtectionListener implements Listener {
    private final AuthLite plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ProtectionListener(AuthLite plugin) {
        this.plugin = plugin;
    }

    private void denyIfNotLoggedIn(Player player, String messageKey) {
        if (!plugin.isLoggedIn(player.getUniqueId())) {
            player.sendMessage(mm.deserialize(getPrefix() + "<red>" + getMessage(messageKey)));
        }
    }

    private String getMessage(String key) {
        return switch (key) {
            case "not-logged-in" -> "Please log in first: /login <password>";
            default -> "You must be logged in to do that.";
        };
    }

    private String getPrefix() {
        return plugin.getConfig().getString("messages.prefix", "");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!plugin.isLoggedIn(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            denyIfNotLoggedIn(e.getPlayer(), "not-logged-in");
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage().toLowerCase();
        if (cmd.startsWith("/login") || cmd.startsWith("/register") || cmd.startsWith("/changepassword")) return;
        if (!plugin.isLoggedIn(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            denyIfNotLoggedIn(e.getPlayer(), "not-logged-in");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!plugin.isLoggedIn(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            denyIfNotLoggedIn(e.getPlayer(), "not-logged-in");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!e.getFrom().getBlock().equals(e.getTo().getBlock())) {
            if (!plugin.isLoggedIn(e.getPlayer().getUniqueId())) {
                e.setTo(e.getFrom());
                denyIfNotLoggedIn(e.getPlayer(), "not-logged-in");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!plugin.isLoggedIn(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            denyIfNotLoggedIn(e.getPlayer(), "not-logged-in");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!plugin.isLoggedIn(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            denyIfNotLoggedIn(e.getPlayer(), "not-logged-in");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.setLoggedIn(e.getPlayer().getUniqueId(), false);
    }
}