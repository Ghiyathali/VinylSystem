package com.vinylsystem.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration manager for API keys and settings
 */
public class ConfigManager {
    private static final String CONFIG_FILE = "/config.properties";
    private static Properties properties = null;
    
    static {
        loadConfig();
    }
    
    /**
     * Load configuration from environment variables and config file
     */
    private static void loadConfig() {
        properties = new Properties();
        
        // Try to load from config file first
        try (InputStream input = ConfigManager.class.getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
                System.out.println("Loaded configuration from " + CONFIG_FILE);
            }
        } catch (IOException e) {
            System.out.println("No config file found, using environment variables only");
        }
    }
    
    /**
     * Get a configuration value, checking environment variables first, then config file
     * @param key The configuration key
     * @param defaultValue Default value if not found
     * @return The configuration value
     */
    public static String getConfig(String key, String defaultValue) {
        // Check environment variable first (higher priority)
        String envValue = System.getenv(key.toUpperCase().replace(".", "_"));
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }
        
        // Check properties file
        String propValue = properties.getProperty(key);
        if (propValue != null && !propValue.trim().isEmpty()) {
            return propValue.trim();
        }
        
        return defaultValue;
    }
    
    /**
     * Get a required configuration value
     * @param key The configuration key
     * @return The configuration value
     * @throws IllegalStateException If the configuration value is not found
     */
    public static String getRequiredConfig(String key) {
        String value = getConfig(key, null);
        if (value == null) {
            throw new IllegalStateException("Required configuration missing: " + key + 
                ". Set environment variable " + key.toUpperCase().replace(".", "_") + 
                " or add " + key + " to config.properties");
        }
        return value;
    }
    
    // Discogs API specific getters
    public static String getDiscogsAccessToken() {
        return getConfig("discogs.access.token", null);
    }
    
    public static String getDiscogsConsumerKey() {
        return getConfig("discogs.consumer.key", null);
    }
    
    public static String getDiscogsConsumerSecret() {
        return getConfig("discogs.consumer.secret", null);
    }
    
    public static String getDiscogsBaseUrl() {
        return getConfig("discogs.base.url", "https://api.discogs.com");
    }
}