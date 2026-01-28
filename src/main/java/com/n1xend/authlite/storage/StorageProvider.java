package com.n1xend.authlite.storage;

import java.util.UUID;

public interface StorageProvider {
    
    boolean isRegistered(String username);
    
    String getPasswordHash(String username);
    
    String getSalt(String username);
    
    void savePasswordHash(String username, String passwordHash, String salt);
    
    void logAuth(String username);
    
    long getLastPasswordChange(UUID uuid);
    
    void setLastPasswordChange(UUID uuid, long timestamp);
    
    record AutoLoginData(String ipAddress, long expiresAt) {}
    
    AutoLoginData getAutoLoginData(String username);
    
    void saveAutoLoginData(String username, String ipAddress, long expiresAt);
    
    boolean hasAutoLogin(String username, String ipAddress);
}