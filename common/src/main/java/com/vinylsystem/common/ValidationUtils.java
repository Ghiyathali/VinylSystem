package com.vinylsystem.common;

import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtils {
    private static final Pattern NAME_PATTERN = Pattern.compile(ProtocolConstants.NAME_PATTERN);
    private static final Pattern IP_PATTERN = Pattern.compile(ProtocolConstants.IP_PATTERN);
    
    /**
     * Validates server name format: <string>.group#.pro2[x|y]
     */
    public static boolean isValidServerName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name).matches();
    }
    
    /**
     * Validates IP address format
     */
    public static boolean isValidIPAddress(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }
        return IP_PATTERN.matcher(ip).matches();
    }
    
    /**
     * Validates port number range
     */
    public static boolean isValidPort(int port) {
        return port > 0 && port <= 65535;
    }
    
    /**
     * Validates TTL value
     */
    public static boolean isValidTTL(int ttl) {
        return ttl > 0 && ttl <= 3600; // Max 1 hour
    }
}