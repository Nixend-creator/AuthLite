package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;
import java.net.InetAddress;
import java.net.URI;

public class SsrfProtectionModule implements Module {
    private final AuthLite plugin;

    public SsrfProtectionModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.ssrf-protection", true);
    }

    public boolean isValidWebhookUrl(String url) {
        if (!isEnabled()) return true;
        try {
            URI uri = new URI(url);
            if (!"https".equals(uri.getScheme())) return false;
            InetAddress addr = InetAddress.getByName(uri.getHost());
            return !(addr.isAnyLocalAddress() || addr.isLoopbackAddress() ||
                    addr.isLinkLocalAddress() || addr.isSiteLocalAddress());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void enable() {
        // Используется в ExternalSyncModule
    }
}
