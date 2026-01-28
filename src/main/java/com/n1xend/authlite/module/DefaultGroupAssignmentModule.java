package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class DefaultGroupAssignmentModule implements Module {
    private final AuthLite plugin;
    private boolean enabled;
    private String targetGroup;
    private List<String> fallbackCommands;

    public DefaultGroupAssignmentModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.default-group-assignment", false);
    }

    @Override
    public void enable() {
        if (!isEnabled()) return;

        enabled = true;
        targetGroup = plugin.getConfig().getString("security.default-group.group", "default");
        fallbackCommands = plugin.getConfig().getStringList("security.default-group.fallback-commands");

        plugin.getLogger().info("DefaultGroupAssignmentModule enabled. Target group: " + targetGroup);
    }

    /**
     * Вызывается после успешной регистрации игрока
     */
    public void assignDefaultGroup(Player player) {
        if (!enabled) return;

        UUID uuid = player.getUniqueId();
        String name = player.getName();

        // 1. Попытка через LuckPerms (наиболее надёжно)
        if (assignViaLuckPerms(uuid, name)) return;

        // 2. Попытка через Vault (если установлен)
        if (assignViaVault(name)) return;

        // 3. Универсальный способ — выполнение команд
        assignViaCommands(name);
    }

    private boolean assignViaLuckPerms(UUID uuid, String name) {
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            net.luckperms.api.LuckPerms api = net.luckperms.api.LuckPermsProvider.get();
            net.luckperms.api.model.user.User user = api.getUserManager().getUser(uuid);
            if (user != null) {
                user.data().add(net.luckperms.api.node.Node.builder("group." + targetGroup).build());
                api.getUserManager().saveUser(user);
                plugin.getLogger().info("Assigned " + name + " to group '" + targetGroup + "' via LuckPerms");
                return true;
            }
        } catch (Exception e) {
            // LuckPerms не установлен или ошибка — пропускаем
        }
        return false;
    }

    private boolean assignViaVault(String name) {
        try {
            Class.forName("net.milkbowl.vault.permission.Permission");
            if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                net.milkbowl.vault.permission.Permission perms = Bukkit.getServer().getServicesManager()
                    .getRegistration(net.milkbowl.vault.permission.Permission.class)
                    .getProvider();
                if (perms != null) {
                    perms.playerAddGroup(null, name, targetGroup);
                    plugin.getLogger().info("Assigned " + name + " to group '" + targetGroup + "' via Vault");
                    return true;
                }
            }
        } catch (Exception e) {
            // Vault не установлен — пропускаем
        }
        return false;
    }

    private void assignViaCommands(String name) {
        if (fallbackCommands.isEmpty()) return;

        for (String cmd : fallbackCommands) {
            String finalCmd = cmd.replace("{player}", name).replace("{group}", targetGroup);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
        }
        plugin.getLogger().info("Assigned " + name + " to group '" + targetGroup + "' via fallback commands");
    }
}