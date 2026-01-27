package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PopularPluginsIntegrationModule implements Module {
    private final AuthLite plugin;

    public PopularPluginsIntegrationModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.popular-plugins-integration", true);
    }

    @Override
    public void enable() {
        if (!isEnabled()) return;

        // Проверка на AuthMe — если есть, отключаем AuthLite
        if (isPluginEnabled("AuthMe")) {
            plugin.getLogger().severe("AuthMe detected! AuthLite will disable itself to avoid conflicts.");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        // EssentialsX: отключаем /login, /register в Essentials
        if (isPluginEnabled("Essentials")) {
            plugin.getLogger().info("Essentials detected. AuthLite will handle authentication.");
            // Совет админу: в essentials/config.yml → set register-enabled: false, login-enabled: false
        }

        // LuckPerms: совместимость с префиксами/суффиксами
        if (isPluginEnabled("LuckPerms")) {
            plugin.getLogger().info("LuckPerms detected. Group-based permissions will work normally.");
        }

        // Vault: экономика (если добавишь регистрационную плату)
        if (isPluginEnabled("Vault")) {
            plugin.getLogger().info("Vault detected. Economy features available.");
        }

        // TAB: совместимость с табом
        if (isPluginEnabled("TAB")) {
            plugin.getLogger().info("TAB detected. Player list will update after login.");
        }

        // WorldGuard: защита регионов до входа
        if (isPluginEnabled("WorldGuard")) {
            plugin.getLogger().info("WorldGuard detected. Regions will block unlogged players.");
        }

        // PlaceholderAPI: активируем поддержку
        if (isPluginEnabled("PlaceholderAPI")) {
            // Загружаем PAPI-модуль только если он есть
            try {
                Class.forName("com.n1xend.authlite.module.PlaceholderApiSupportModule");
                new com.n1xend.authlite.module.PlaceholderApiSupportModule(plugin).enable();
            } catch (Exception e) {
                plugin.getLogger().warning("PAPI module not found, skipping integration.");
            }
        }
    }

    private boolean isPluginEnabled(String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }
}
