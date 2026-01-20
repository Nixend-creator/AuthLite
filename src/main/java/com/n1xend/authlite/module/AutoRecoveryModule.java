package com.n1xend.authlite.module;

import com.n1xend.authlite.AuthLite;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AutoRecoveryModule implements Module {
    private final AuthLite plugin;

    public AutoRecoveryModule(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("modules.auto-recovery", true);
    }

    @Override
    public void enable() {
        if (isEnabled()) {
            File playersFile = new File(plugin.getDataFolder(), "players.yml");
            if (playersFile.exists()) {
                try {
                    // Простая проверка целостности (можно расширить)
                    String content = Files.readString(playersFile.toPath());
                    if (!content.contains("password")) {
                        plugin.getLogger().severe("players.yml appears corrupted!");
                    }
                } catch (IOException e) {
                    plugin.getLogger().warning("Failed to check players.yml integrity");
                }
            }
        }
    }
}
