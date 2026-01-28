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
        // В ModuleManager.java
modules.add(new CoreAuthModule(plugin));
modules.add(new AutoLoginModule(plugin));
modules.add(new PasswordChangeModule(plugin));
modules.add(new BruteForceProtectionModule(plugin));
modules.add(new IpAccountLimitModule(plugin));
modules.add(new HumanityCheckModule(plugin));
modules.add(new SsrfProtectionModule(plugin));
modules.add(new EmergencySessionRevokeModule(plugin));
modules.add(new AuditLoggingModule(plugin));
modules.add(new GeoIpBlockingModule(plugin));
modules.add(new HttpApiModule(plugin));
modules.add(new PlaceholderApiSupportModule(plugin));
modules.add(new SandboxModeModule(plugin));
modules.add(new AutoRecoveryModule(plugin));
modules.add(new PopularPluginsIntegrationModule(plugin));
modules.add(new RestrictedUserProtectionModule(plugin));
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
