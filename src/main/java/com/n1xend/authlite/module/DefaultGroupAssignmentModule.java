package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class DefaultGroupAssignmentModule implements Module {
    private final AuthLite plugin;
    private boolean enabled;
    private List<String> commands;

    public DefaultGroupAssignmentModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.default-group-assignment", false);
    }

    @Override
    public void enable() {
        if (!isEnabled()) return;
        enabled = true;
        commands = plugin.getConfig().getStringList("security.default-group.commands");
        plugin.getLogger().info("DefaultGroupAssignmentModule enabled.");
    }

    public void assignDefaultGroup(Player player) {
        if (!enabled || commands.isEmpty()) return;
        for (String cmd : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                cmd.replace("{player}", player.getName()));
        }
    }
}