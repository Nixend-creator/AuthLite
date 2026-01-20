package com.n1xend.authlite.command;

import com.n1xend.authlite.AuthLite;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AuthCommand implements CommandExecutor {
    private final AuthLite plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public AuthCommand(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(mm.deserialize("<red>Usage: /auth [reload|info|stats|sessions]"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("authlite.reload")) {
                    sender.sendMessage(mm.deserialize("<red>No permission."));
                    return true;
                }
                plugin.reloadConfig();
                sender.sendMessage(mm.deserialize("<green>AuthLite config reloaded!"));
                plugin.getLogger().info(sender.getName() + " executed /auth reload");
                return true;

            default:
                sender.sendMessage(mm.deserialize("<red>Unknown subcommand. Use: reload"));
                return true;
        }
    }
}
