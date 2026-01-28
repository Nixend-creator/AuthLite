package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final AuthLite plugin;
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager(AuthLite plugin) {
        this.plugin = plugin;
    }

    public void loadModules() {
        modules.add(new CoreAuthModule(plugin));
        modules.add(new AutoLoginModule(plugin));
        modules.add(new PasswordChangeModule(plugin));
        modules.add(new BruteForceProtectionModule(plugin));
        modules.add(new IpAccountLimitModule(plugin));
        modules.add(new HumanityCheckModule(plugin));
        modules.add(new SsrfProtectionModule(plugin));
        modules.add(new EmergencySessionRevokeModule(plugin));
        modules.add(new AuditLoggingModule(plugin));
        modules.add(new PlaceholderApiSupportModule(plugin));
        modules.add(new AutoRecoveryModule(plugin));
        modules.add(new PopularPluginsIntegrationModule(plugin));
        modules.add(new RestrictedUserProtectionModule(plugin));
        modules.add(new DefaultGroupAssignmentModule(plugin));
    }

    public void enableModules() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.enable();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module module : modules) {
            if (clazz.isInstance(module)) {
                return (T) module;
            }
        }
        return null;
    }
}