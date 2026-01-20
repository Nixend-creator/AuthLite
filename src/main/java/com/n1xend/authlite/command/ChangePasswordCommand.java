package com.n1xend.authlite.command;

import com.n1xend.authlite.AuthLite;
import at.favre.lib.crypto.bcrypt.BCrypt;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChangePasswordCommand implements CommandExecutor {
    private final AuthLite plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ChangePasswordCommand(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can change password.");
            return true;
        }

        if (!plugin.isLoggedIn(player.getUniqueId())) {
            player.sendMessage(mm.deserialize(getPrefix() + "<red>Please log in first!"));
            return true;
        }

        if (args.length != 3) {
            player.sendMessage(mm.deserialize(getPrefix() + "<red>Usage: /changepassword <old> <new> <repeat>"));
            return true;
        }

        String oldPass = args[0];
        String newPass1 = args[1];
        String newPass2 = args[2];

        if (!newPass1.equals(newPass2)) {
            player.sendMessage(mm.deserialize(getPrefix() + "<red>New passwords do not match!"));
            return true;
        }

        if (newPass1.length() < 4) {
            player.sendMessage(mm.deserialize(getPrefix() + "<red>New password too short (min 4 characters)!"));
            return true;
        }

        String uuid = player.getUniqueId().toString();
        String storedHash = plugin.getStorage().getPasswordHash(uuid);

        if (storedHash == null || !BCrypt.verifyer().verify(oldPass.toCharArray(), storedHash).verified) {
            player.sendMessage(mm.deserialize(getPrefix() + "<red>Incorrect old password!"));
            return true;
        }

        String newHash = BCrypt.withDefaults().hashToString(12, newPass1.toCharArray());
        String ip = player.getAddress().getAddress().getHostAddress();
        plugin.getStorage().savePasswordHash(uuid, newHash, ip);
        plugin.getStorage().clearAutoLogin(uuid); // revoke auto-login for security

        plugin.getSessionLogger().log("PASSWORD_CHANGED", 
            java.util.Map.of("PLAYER", player.getName(), "UUID", uuid));
        player.sendMessage(mm.deserialize(getPrefix() + "<green>Password changed successfully!"));
        return true;
    }

    private String getPrefix() {
        return plugin.getConfig().getString("messages.prefix", "");
    }
}
