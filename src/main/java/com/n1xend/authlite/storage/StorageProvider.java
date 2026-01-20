package com.n1xend.authlite.storage;

public interface StorageProvider {
    boolean hasAccount(String uuid);
    String getPasswordHash(String uuid);
    void savePasswordHash(String uuid, String hash, String ip);
    int getAccountCountByIp(String ip);
    AutoLoginData getAutoLoginData(String uuid);
    void saveAutoLogin(String uuid, String token, long expiresAt, String ipHash);
    void clearAutoLogin(String uuid);
    void logAuth(String message);
    void logBruteForce(String message);
    String hashIp(String ip);

    class AutoLoginData {
        public final String token;
        public final long expiresAt;
        public final String ipHash;
        public AutoLoginData(String token, long expiresAt, String ipHash) {
            this.token = token;
            this.expiresAt = expiresAt;
            this.ipHash = ipHash;
        }
    }
}
