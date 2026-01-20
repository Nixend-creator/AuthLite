package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final AuthLite plugin;
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager(AuthLite plugin) {
        this.plugin = plugin;
        modules.add(new CoreAuthModule(plugin));
        modules.add(new IpAccountLimitModule(plugin));
    }

    public void loadAll() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.enable();
                plugin.getLogger().info("Loaded module: " + module.getClass().getSimpleName());
            }
        }
    }
}
