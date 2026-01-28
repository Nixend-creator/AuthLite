package com.n1xend.authlite.command;

import com.n1xend.authlite.AuthLite;
import com.n1xend.authlite.module.DefaultGroupAssignmentModule;
import at.favre.lib.crypto.bcrypt.BCrypt;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

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
            String msg = getLocalizedMessage(player, "register-usage");
            player.sendMessage(mm.deserialize(msg));
            return true;
        }

        if (plugin.isLoggedIn(player.getUniqueId())) {
            String msg = getLocalizedMessage(player, "register-already");
            player.sendMessage(mm.deserialize(msg));
            return true;
        }

        String pass1 = args[0];
        String pass2 = args[1];

        if (!pass1.equals(pass2)) {
            String msg = getLocalizedMessage(player, "register-mismatch");
            player.sendMessage(mm.deserialize(msg));
            return true;
        }

        if (pass1.length() < 4) {
            String msg = getLocalizedMessage(player, "register-short");
            player.sendMessage(mm.deserialize(msg));
            return true;
        }

        String uuid = player.getUniqueId().toString();
        if (plugin.getStorage().getPasswordHash(uuid) != null) {
            String msg = getLocalizedMessage(player, "register-already");
            player.sendMessage(mm.deserialize(msg));
            return true;
        }

        String hashed = BCrypt.withDefaults().hashToString(12, pass1.toCharArray());
        plugin.getStorage().savePasswordHash(uuid, hashed);
        plugin.setLoggedIn(player.getUniqueId(), true);

        // === Назначение группы после регистрации ===
        DefaultGroupAssignmentModule groupModule = plugin.getModuleManager().getModule(DefaultGroupAssignmentModule.class);
        if (groupModule != null) {
            groupModule.assignDefaultGroup(player);
        }

        plugin.getSessionLogger().log("REGISTER_SUCCESS",
            java.util.Map.of("PLAYER", player.getName(), "UUID", uuid));

        String msg = getLocalizedMessage(player, "register-success");
        player.sendMessage(mm.deserialize(msg));
        return true;
    }

    private String getLocalizedMessage(Player player, String key) {
        String lang = plugin.getConfig().getString("language", "en");

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