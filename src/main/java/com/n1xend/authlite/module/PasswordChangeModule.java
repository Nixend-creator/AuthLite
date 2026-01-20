package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;

public class PasswordChangeModule implements Module {
    private final AuthLite plugin;

    public PasswordChangeModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.password-change", true);
    }

    @Override
    public void enable() {
        // Команда уже зарегистрирована в CoreAuthModule
        // Здесь можно добавить доп. логику (например, уведомления)
    }
}
