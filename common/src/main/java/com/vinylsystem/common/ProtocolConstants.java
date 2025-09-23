package com.vinylsystem.common;

/**
 * Protocol constants and configuration
 */
public class ProtocolConstants {
    // Default ports
    public static final int DEFAULT_DIRECTORY_TCP_PORT = 8080;
    public static final int DEFAULT_DIRECTORY_UDP_PORT = 8081;
    public static final int DEFAULT_VINYL_SERVER_PORT = 9090;
    
    // TTL settings
    public static final int DEFAULT_TTL_SECONDS = 300; // 5 minutes
    public static final int CLEANUP_INTERVAL_SECONDS = 30; // Run cleanup every 30 seconds
    
    // Message types
    public static final String MSG_REGISTER = "REGISTER";
    public static final String MSG_UPDATE = "UPDATE";
    public static final String MSG_LOOKUP = "LOOKUP";
    public static final String MSG_RESPONSE = "RESPONSE";
    
    // Network settings
    public static final int MAX_MESSAGE_SIZE = 1024;
    public static final int SOCKET_TIMEOUT = 5000; // 5 seconds
    
    // Name validation regex: <string>.group#.pro2[x|y]
    public static final String NAME_PATTERN = "^.{1,30}\\.group\\d+\\.pro2[xy]?$";
    
    // IP validation regex
    public static final String IP_PATTERN = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
}