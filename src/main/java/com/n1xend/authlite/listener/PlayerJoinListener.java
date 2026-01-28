package com.n1xend.authlite.listener;

import com.n1xend.authlite.AuthLite;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;

public class PlayerJoinListener implements Listener {
    private final AuthLite plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public PlayerJoinListener(AuthLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        var uuid = player.getUniqueId().toString();
        var ip = player.getAddress().getAddress().getHostAddress();

        // Попытка автологина
        var autoLoginData = plugin.getStorage().getAutoLoginData(uuid);
        if (autoLoginData != null) {
            long now = System.currentTimeMillis();
            if (now < autoLoginData.expiresAt) {
                String currentIpHash = plugin.getStorage().hashIp(ip);
                if (currentIpHash.equals(autoLoginData.ipHash)) {
                    plugin.setLoggedIn(player.getUniqueId(), true);
                    String msg = getLocalizedMessage(player, "auto-login-success");
                    player.sendMessage(mm.deserialize(msg));
                    return;
                }
            }
        }

        // Обычный вход
        int timeout = plugin.getConfig().getInt("login-timeout", 60);
        String msg = getLocalizedMessage(player, "not-logged-in")
            .replace("<time>", String.valueOf(timeout));
        player.sendMessage(mm.deserialize(msg));
    }

    // ← Добавлен метод
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