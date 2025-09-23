package com.vinylsystem.directory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.Iterator;

/**
 * Thread-safe registry for vinyl server registrations with TTL management
 */
public class ServerRegistry {
    private final ConcurrentHashMap<String, ServerRecord> servers;
    private final ScheduledExecutorService cleanupExecutor;
    
    public ServerRegistry() {
        this.servers = new ConcurrentHashMap<>();
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        startCleanupTask();
    }
    
    /**
     * Register or update a server
     */
    public void registerServer(String name, String ip, int port, int ttlSeconds) {
        long expirationTime = System.currentTimeMillis() + (ttlSeconds * 1000L);
        ServerRecord record = new ServerRecord(name, ip, port, expirationTime);
        servers.put(name, record);
        System.out.println("Registered server: " + name + " at " + ip + ":" + port + " (TTL: " + ttlSeconds + "s)");
    }
    
    /**
     * Lookup a server by name
     */
    public ServerRecord lookupServer(String name) {
        ServerRecord record = servers.get(name);
        if (record != null && record.isExpired()) {
            servers.remove(name);
            System.out.println("Removed expired server: " + name);
            return null;
        }
        return record;
    }
    
    /**
     * Remove expired servers
     */
    public void cleanupExpiredServers() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, ServerRecord>> iterator = servers.entrySet().iterator();
        int removedCount = 0;
        
        while (iterator.hasNext()) {
            Map.Entry<String, ServerRecord> entry = iterator.next();
            if (entry.getValue().getExpirationTime() <= currentTime) {
                iterator.remove();
                removedCount++;
                System.out.println("Cleanup: Removed expired server: " + entry.getKey());
            }
        }
        
        if (removedCount > 0) {
            System.out.println("Cleanup completed: Removed " + removedCount + " expired servers");
        }
    }
    
    /**
     * Get current server count
     */
    public int getServerCount() {
        return servers.size();
    }
    
    /**
     * Start the cleanup task
     */
    private void startCleanupTask() {
        cleanupExecutor.scheduleAtFixedRate(
            this::cleanupExpiredServers,
            30, // Initial delay
            30, // Period
            TimeUnit.SECONDS
        );
        System.out.println("Started cleanup task with 30-second interval");
    }
    
    /**
     * Shutdown the registry
     */
    public void shutdown() {
        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Server record with expiration time
     */
    public static class ServerRecord {
        private final String name;
        private final String ip;
        private final int port;
        private final long expirationTime;
        
        public ServerRecord(String name, String ip, int port, long expirationTime) {
            this.name = name;
            this.ip = ip;
            this.port = port;
            this.expirationTime = expirationTime;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
        
        public String getName() { return name; }
        public String getIp() { return ip; }
        public int getPort() { return port; }
        public long getExpirationTime() { return expirationTime; }
    }
}