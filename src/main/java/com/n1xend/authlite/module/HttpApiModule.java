package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpApiModule implements Module {
    private final AuthLite plugin;
    private HttpServer server;

    public HttpApiModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.http-api", false);
    }

    @Override
    public void enable() {
        if (!isEnabled()) return;
        try {
            int port = plugin.getConfig().getInt("http-api.port", 8080);
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/api/v1/status", exchange -> {
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
            });
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to start HTTP API: " + e.getMessage());
        }
    }

    @Override
    public void disable() {
        if (server != null) server.stop(0);
    }
}
