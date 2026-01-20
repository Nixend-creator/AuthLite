package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;

public class SandboxModeModule implements Module {
    private final AuthLite plugin;

    public SandboxModeModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.sandbox-mode", false);
    }

    public boolean isInSandboxMode() {
        return isEnabled();
    }

    @Override
    public void enable() {
        if (isEnabled()) {
            plugin.getLogger().warning("Sandbox mode enabled! All passwords are 'test'.");
        }
    }
}
