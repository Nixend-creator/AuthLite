package com.n1xend.authlite.command;

import com.n1xend.authlite.AuthLite;
import at.favre.lib.crypto.bcrypt.BCrypt;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.concurrent.TimeUnit;

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

        if (args.length != 3) {
            String msg = getLocalizedMessage(player, "password-change-usage");
            player.sendMessage(mm.deserialize(msg));
            return true;
        }

        if (!plugin.isLoggedIn(player.getUniqueId())) {
            String msg = getLocalizedMessage(player, "not-logged-in");
            player.sendMessage(mm.deserialize(msg));
            return true;
        }

        String oldPass = args[0];
        String newPass1 = args[1];
        String newPass2 = args[2];

        // Проверка cooldown
        Long lastChange = plugin.getLastPasswordChange(player.getUniqueId());
        if (lastChange != null) {
            long cooldown = plugin.getConfig().getLong("password-change-cooldown", 300);
            if (System.currentTimeMillis() - lastChange < TimeUnit.SECONDS.toMillis(cooldown)) {
                long remaining = cooldown - (System.currentTimeMillis() - lastChange) / 1000;
                String msg = getLocalizedMessage(player, "password-change-cooldown")
                    .replace("<time>", String.valueOf(remaining));
                player.sendMessage(mm.deserialize(msg));
                return true;
            }
        }

        // Проверка нового пароля
        if (newPass1.length() < 4) {
            String msg = getLocalizedMessage(player, "password-change-short");
            player.sendMessage(mm.deserialize(msg));
            return true;
        }

        if (!newPass1.equals(newPass2)) {
            String msg = getLocalizedMessage(player, "password-change-mismatch");
            player.sendMessage(mm.deserialize(msg));
            return true;
        }

        String uuid = player.getUniqueId().toString();
        String storedHash = plugin.getStorage().getPasswordHash(uuid);

        if (storedHash == null) {
            String msg = getLocalizedMessage(player, "not-logged-in");
            player.sendMessage(mm.deserialize(msg));
            return true;
        }

        if (!BCrypt.verifyer().verify(oldPass.toCharArray(), storedHash).verified) {
            String msg = getLocalizedMessage(player, "password-change-wrong-old");
            player.sendMessage(mm.deserialize(msg));
            return true;
        }

        String newHash = BCrypt.withDefaults().hashToString(12, newPass1.toCharArray());
        plugin.getStorage().savePasswordHash(uuid, newHash);
        plugin.setLastPasswordChange(player.getUniqueId(), System.currentTimeMillis());

        String msg = getLocalizedMessage(player, "password-change-success");
        player.sendMessage(mm.deserialize(msg));
        return true;
    }

    private String getLocalizedMessage(Player player, String key) {
        String lang = plugin.getConfig().getString(" language", "en").trim(); // ← исправлено

        if (plugin.getConfig().getBoolean("auto-detect-language", false)) {
            try {
                var locale = player.locale();
                if (locale != null) {
                    String clientLang = locale.toString().toLowerCase();
                    if (clientLang.startsWith("ru")) lang = "ru";
                    else if (clientLang.startsWith("uk")) lang = "uk";
                    else if (clientLang.startsWith("de")) lang = "de";
                    else if (clientLang.startsWith("es")) lang = "es";
                    else if (clientLang.startsWith("fr")) lang = "fr";
                    else if (clientLang.startsWith("pt")) lang = "pt";
                    else if (clientLang.startsWith("it")) lang = "it";
                    else if (clientLang.startsWith("pl")) lang = "pl";
                    else if (clientLang.startsWith("tr")) lang = "tr";
                    else if (clientLang.startsWith("zh")) lang = "zh";
                    else if (clientLang.startsWith("ja")) lang = "ja";
                }
            } catch (Exception ignored) {
            }
        }

        File file = new File(plugin.getDataFolder(), "messages_" + lang + ".yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        String msg = cfg.getString(key, "Missing translation for '" + key + "'");
        String prefix = cfg.getString("prefix", "");

        return msg.replace("%prefix%", prefix);
    }
}