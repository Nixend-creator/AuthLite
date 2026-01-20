package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PlaceholderApiSupportModule implements Module {
    private final AuthLite plugin;

    public PlaceholderApiSupportModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.placeholderapi-support", true);
    }

    @Override
    public void enable() {
        if (isEnabled() && plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new AuthLitePlaceholder().register();
        }
    }

    private class AuthLitePlaceholder extends PlaceholderExpansion {
        @Override
        public String getIdentifier() { return "authlite"; }
        @Override
        public String getAuthor() { return "N1xend"; }
        @Override
        public String getVersion() { return "1.0.0"; }

        @Override
        public String onPlaceholderRequest(OfflinePlayer player, String params) {
            if (player == null) return "";
            if (params.equals("logged_in")) {
                return String.valueOf(plugin.isLoggedIn(player.getUniqueId()));
            }
            return "";
        }
    }
}
