package org.example.payments.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenVersionStore {

    private final Map<String, Integer> versions = new ConcurrentHashMap<>();

    public int getVersion(String username) {
        return versions.getOrDefault(username, 1);
    }

    public void increment(String username) {
        versions.put(username, getVersion(username) + 1);
    }

    public boolean isCurrent(String username, int versionInToken) {
        return versionInToken == getVersion(username);
    }
}
