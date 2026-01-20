package com.n1xend.authlite.command;

import com.n1xend.authlite.AuthLite;
import at.favre.lib.crypto.bcrypt.BCrypt;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {
    private final AuthLite plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public RegisterCommand(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can register.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(mm.deserialize(getPrefix() + "<red>Usage: /register <password> <repeat>"));
            return true;
        }

        if (plugin.getStorage().hasAccount(player.getUniqueId().toString())) {
            player.sendMessage(mm.deserialize(getPrefix() + "<red>You are already registered!"));
            return true;
        }

        String pass1 = args[0];
        String pass2 = args[1];

        if (!pass1.equals(pass2)) {
            player.sendMessage(mm.deserialize(getPrefix() + "<red>Passwords do not match!"));
            return true;
        }

        if (pass1.length() < 4) {
            player.sendMessage(mm.deserialize(getPrefix() + "<red>Password too short (min 4 characters)!"));
            return true;
        }

        String hash = BCrypt.withDefaults().hashToString(12, pass1.toCharArray());
        String ip = player.getAddress().getAddress().getHostAddress();
        plugin.getStorage().savePasswordHash(player.getUniqueId().toString(), hash, ip);
        plugin.getSessionLogger().log("REGISTER_SUCCESS", 
            java.util.Map.of("PLAYER", player.getName(), "UUID", player.getUniqueId().toString()));

        player.sendMessage(mm.deserialize(getPrefix() + "<green>You have been successfully registered! Please log in: /login <password>"));
        return true;
    }

    private String getPrefix() {
        return plugin.getConfig().getString("messages.prefix", "");
    }
}
