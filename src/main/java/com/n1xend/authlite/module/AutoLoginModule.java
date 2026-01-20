package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;

public class AutoLoginModule implements Module {
    private final AuthLite plugin;

    public AutoLoginModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.auto-login", true);
    }

    @Override
    public void enable() {
        // Логика уже встроена в PlayerJoinListener и Storage
        // Этот модуль управляет только включением/отключением
    }
}
