package com.n1xend.authlite.storage;

import java.util.Map;
import java.util.Optional;

public interface StorageProvider {

    // --- Пароли ---
    void savePasswordHash(String username, String passwordHash, String ipHash);
    Optional<String> getPasswordHash(String username);

    // --- Автовход ---
    record AutoLoginData(String token, long expiresAt) {}
    void saveAutoLogin(String username, String token, long expiresAt, String ipHash);
    Optional<AutoLoginData> getAutoLogin(String username);
    boolean hasAutoLogin(String username, String ipHash);
    void removeAutoLogin(String username);

    // --- IP-аккаунты ---
    int getAccountCountByIp(String ipHash);
    void incrementAccountCount(String ipHash);
    void decrementAccountCount(String ipHash);

    // --- Брутфорс ---
    void logBruteForce(String ipHash);

    // --- Утилиты ---
    String hashIp(String ip);
}