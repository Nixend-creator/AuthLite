package com.n1xend.authlite.command;

import com.n1xend.authlite.AuthLite;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class AuthSessionsCommand implements CommandExecutor {
    private final AuthLite plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public AuthSessionsCommand(AuthLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("authlite.sessions")) {
            sender.sendMessage(mm.deserialize("<red>No permission."));
            return true;
        }

        int lines = 10;
        if (args.length > 0) {
            try {
                lines = Math.min(50, Math.max(1, Integer.parseInt(args[0])));
            } catch (NumberFormatException ignored) {}
        }

        Path logFile = plugin.getSessionLogger().getCurrentLogPath();
        if (!Files.exists(logFile)) {
            sender.sendMessage(mm.deserialize("<yellow>Session log is empty.</yellow>"));
            return true;
        }

        try {
            List<String> allLines = Files.readAllLines(logFile);
            int start = Math.max(0, allLines.size() - lines);
            List<String> tail = allLines.subList(start, allLines.size());

            sender.sendMessage(mm.deserialize("<green>Last " + lines + " session log entries:</green>"));
            for (String line : tail) {
                sender.sendMessage(mm.deserialize("<gray>" + line + "</gray>"));
            }
        } catch (IOException e) {
            sender.sendMessage(mm.deserialize("<red>Error reading log: " + e.getMessage() + "</red>"));
        }

        return true;
    }
}
