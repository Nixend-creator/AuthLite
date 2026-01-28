package com.n1xend.authlite.command;

import com.n1xend.authlite.AuthLite;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class AuthCommand implements CommandExecutor {
    private final AuthLite plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public AuthCommand(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            String msg = getLocalizedMessage(player, "auth-help");
            sender.sendMessage(mm.deserialize(msg));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("authlite.reload")) {
                    if (sender instanceof Player player) {
                        String msg = getLocalizedMessage(player, "no-permission");
                        sender.sendMessage(mm.deserialize(msg));
                    } else {
                        sender.sendMessage("No permission.");
                    }
                    return true;
                }
                plugin.reloadConfig();
                sender.sendMessage(mm.deserialize("<green>AuthLite config reloaded!</green>"));
                plugin.getLogger().info(sender.getName() + " executed /auth reload");
                return true;

            default:
                if (sender instanceof Player player) {
                    String msg = getLocalizedMessage(player, "unknown-command");
                    sender.sendMessage(mm.deserialize(msg));
                } else {
                    sender.sendMessage("Unknown subcommand. Use /auth help");
                }
                return true;
        }
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