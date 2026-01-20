package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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
        public @NotNull String getIdentifier() {
            return "authlite";
        }

        @Override
        public @NotNull String getAuthor() {
            return "N1xend";
        }

        @Override
        public @NotNull String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onPlaceholderRequest(OfflinePlayer player, @NotNull String params) {
            if (player == null) {
                return "";
            }

            if (params.equalsIgnoreCase("logged_in")) {
                return String.valueOf(plugin.isLoggedIn(player.getUniqueId()));
            }

            // Важно: возвращай null для неизвестных параметров
            return null;
        }
    }
}