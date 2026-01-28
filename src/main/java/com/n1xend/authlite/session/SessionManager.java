package com.n1xend.authlite.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final Map<UUID, Long> sessions = new ConcurrentHashMap<>();

    public void addSession(UUID playerId, long durationMillis) {
        sessions.put(playerId, System.currentTimeMillis() + durationMillis);
    }

    public boolean hasSession(UUID playerId) {
        Long expireTime = sessions.get(playerId);
        if (expireTime == null) return false;
        if (System.currentTimeMillis() > expireTime) {
            sessions.remove(playerId);
            return false;
        }
        return true;
    }

    public void removeSession(UUID playerId) {
        sessions.remove(playerId);
    }
}