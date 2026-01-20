package com.n1xend.authlite.command;

import com.n1xend.authlite.AuthLite;
import at.favre.lib.crypto.bcrypt.BCrypt;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {
    private final AuthLite plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public LoginCommand(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can log in.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(mm.deserialize(getPrefix() + "<red>Usage: /login <password>"));
            return true;
        }

        if (plugin.isLoggedIn(player.getUniqueId())) {
            player.sendMessage(mm.deserialize(getPrefix() + "<red>You are already logged in!"));
            return true;
        }

        String inputPass = args[0];
        String storedHash = plugin.getStorage().getPasswordHash(player.getUniqueId().toString());

        if (storedHash == null) {
            player.sendMessage(mm.deserialize(getPrefix() + "<red>Please register first!"));
            return true;
        }

        if (BCrypt.verifyer().verify(inputPass.toCharArray(), storedHash).verified) {
            plugin.setLoggedIn(player.getUniqueId(), true);
            plugin.getSessionLogger().log("LOGIN_SUCCESS", 
                java.util.Map.of("PLAYER", player.getName(), "UUID", player.getUniqueId().toString()));
            player.sendMessage(mm.deserialize(getPrefix() + "<green>Welcome! You are now logged in."));
        } else {
            plugin.getSessionLogger().log("LOGIN_FAIL", 
                java.util.Map.of("PLAYER", player.getName(), "IP", player.getAddress().getAddress().getHostAddress()));
            player.sendMessage(mm.deserialize(getPrefix() + "<red>Incorrect password!"));
        }

        return true;
    }

    private String getPrefix() {
        return plugin.getConfig().getString("messages.prefix", "");
    }
}
