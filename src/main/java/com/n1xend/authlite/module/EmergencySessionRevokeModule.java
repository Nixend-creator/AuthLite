package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EmergencySessionRevokeModule implements Module {
    private final AuthLite plugin;

    public EmergencySessionRevokeModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.emergency-session-revoke", true);
    }

    @Override
    public void enable() {
        if (isEnabled()) {
            plugin.getCommand("auth").setTabCompleter((sender, cmd, label, args) -> {
                if (args.length == 1 && sender.hasPermission("authlite.admin")) {
                    return java.util.Arrays.asList("revoke");
                }
                return java.util.Collections.emptyList();
            });
            // Обработка /auth revoke в AuthCommand
        }
    }
}
