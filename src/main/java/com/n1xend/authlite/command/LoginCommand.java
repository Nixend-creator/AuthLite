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
            String msg = getLocalizedMessage(player, "login-usage");
            player.sendMessage(mm.deserialize(msg));
            return true;
        }

        if (plugin.isLoggedIn(player.getUniqueId())) {
            String msg = getLocalizedMessage(player, "login-already");
            player.sendMessage(mm.deserialize(msg));
            return true;
        }

        String inputPass = args[0];
        String uuid = player.getUniqueId().toString();
        String storedHash = plugin.getStorage().getPasswordHash(uuid);

        if (storedHash == null) {
            String msg = getLocalizedMessage(player, "not-logged-in");
            player.sendMessage(mm.deserialize(msg));
            return true;
        }

        if (BCrypt.verifyer().verify(inputPass.toCharArray(), storedHash).verified) {
            plugin.setLoggedIn(player.getUniqueId(), true);

            // === Автологин ===
            if (plugin.getConfig().getBoolean("auto-login.enabled", true)) {
                long durationHours = plugin.getConfig().getLong("auto-login.duration-hours", 24);
                long expiresAt = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(durationHours);
                String ip = player.getAddress().getAddress().getHostAddress();
                String ipHash = plugin.getStorage().hashIp(ip);
                String token = java.util.UUID.randomUUID().toString();
                plugin.getStorage().saveAutoLogin(uuid, token, expiresAt, ipHash);
            }

            plugin.getSessionLogger().log("LOGIN_SUCCESS",
                java.util.Map.of("PLAYER", player.getName(), "UUID", uuid));

            String msg = getLocalizedMessage(player, "login-success");
            player.sendMessage(mm.deserialize(msg));
        } else {
            plugin.getSessionLogger().log("LOGIN_FAIL",
                java.util.Map.of("PLAYER", player.getName(), "IP", player.getAddress().getAddress().getHostAddress()));
            String msg = getLocalizedMessage(player, "login-fail");
            player.sendMessage(mm.deserialize(msg));
        }

        return true;
    }

    private String getLocalizedMessage(Player player, String key) {
        String lang = plugin.getConfig().getString("language", "en");

        // Автоопределение языка (Paper 1.19+)
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
                // Используем язык по умолчанию из конфига
            }
        }

        File file = new File(plugin.getDataFolder(), "messages_" + lang + ".yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        String msg = cfg.getString(key, "Missing translation for '" + key + "'");
        String prefix = cfg.getString("prefix", "");

        return msg.replace("%prefix%", prefix);
    }
}