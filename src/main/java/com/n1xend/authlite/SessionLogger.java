package com.n1xend.authlite;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Map;

public class SessionLogger {
    private final Path logPath;

    public SessionLogger(AuthLite plugin) {
        Path dir = plugin.getDataFolder().toPath().resolve("logs");
        try { Files.createDirectories(dir); } catch (IOException ignored) {}
        this.logPath = dir.resolve("sessions.log");
    }

    public void log(String event, Map<String, String> fields) {
        String line = "[" + Instant.now() + "] EVENT=" + event;
        for (var entry : fields.entrySet()) {
            line += " " + entry.getKey() + "=" + entry.getValue();
        }
        try {
            Files.write(logPath, (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {}
    }

    public Path getCurrentLogPath() { return logPath; }
}
