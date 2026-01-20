package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;

public class AuditLoggingModule implements Module {
    private final AuthLite plugin;

    public AuditLoggingModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.audit-logging", true);
    }

    @Override
    public void enable() {
        // Логирование уже реализовано в SessionLogger и Storage
    }
}
