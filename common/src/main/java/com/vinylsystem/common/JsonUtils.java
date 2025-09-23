package com.vinylsystem.common;

/**
 * Simple JSON utility for the vinyl system (avoiding external dependencies)
 */
public class JsonUtils {
    
    /**
     * Create JSON string for RegistrationMessage
     */
    public static String toJson(RegistrationMessage msg) {
        return String.format(
            "{\"messageType\":\"%s\",\"serverName\":\"%s\",\"serverIP\":\"%s\",\"serverPort\":%d,\"ttl\":%d}",
            escapeJson(msg.getMessageType()),
            escapeJson(msg.getServerName()),
            escapeJson(msg.getServerIP()),
            msg.getServerPort(),
            msg.getTtl()
        );
    }
    
    /**
     * Create JSON string for LookupMessage
     */
    public static String toJson(LookupMessage msg) {
        return String.format(
            "{\"messageType\":\"%s\",\"serverName\":\"%s\"}",
            escapeJson(msg.getMessageType()),
            escapeJson(msg.getServerName())
        );
    }
    
    /**
     * Create JSON string for ResponseMessage
     */
    public static String toJson(ResponseMessage msg) {
        if (msg.getServerIP() != null && msg.getServerPort() != null) {
            return String.format(
                "{\"statusCode\":\"%s\",\"message\":\"%s\",\"serverIP\":\"%s\",\"serverPort\":%d}",
                escapeJson(msg.getStatusCode()),
                escapeJson(msg.getMessage()),
                escapeJson(msg.getServerIP()),
                msg.getServerPort()
            );
        } else {
            return String.format(
                "{\"statusCode\":\"%s\",\"message\":\"%s\"}",
                escapeJson(msg.getStatusCode()),
                escapeJson(msg.getMessage())
            );
        }
    }
    
    /**
     * Parse RegistrationMessage from JSON
     */
    public static RegistrationMessage parseRegistrationMessage(String json) {
        RegistrationMessage msg = new RegistrationMessage();
        msg.setMessageType(extractStringValue(json, "messageType"));
        msg.setServerName(extractStringValue(json, "serverName"));
        msg.setServerIP(extractStringValue(json, "serverIP"));
        msg.setServerPort(extractIntValue(json, "serverPort"));
        msg.setTtl(extractIntValue(json, "ttl"));
        return msg;
    }
    
    /**
     * Parse LookupMessage from JSON
     */
    public static LookupMessage parseLookupMessage(String json) {
        LookupMessage msg = new LookupMessage();
        msg.setMessageType(extractStringValue(json, "messageType"));
        msg.setServerName(extractStringValue(json, "serverName"));
        return msg;
    }
    
    /**
     * Parse ResponseMessage from JSON
     */
    public static ResponseMessage parseResponseMessage(String json) {
        ResponseMessage msg = new ResponseMessage();
        msg.setStatusCode(extractStringValue(json, "statusCode"));
        msg.setMessage(extractStringValue(json, "message"));
        String serverIP = extractStringValue(json, "serverIP");
        if (serverIP != null) {
            msg.setServerIP(serverIP);
            msg.setServerPort(extractIntValue(json, "serverPort"));
        }
        return msg;
    }
    
    /**
     * Extract string value from JSON
     */
    private static String extractStringValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) return null;
        
        start += pattern.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        
        return json.substring(start, end);
    }
    
    /**
     * Extract integer value from JSON
     */
    private static int extractIntValue(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) return 0;
        
        start += pattern.length();
        int end = Math.min(
            json.indexOf(",", start) == -1 ? json.length() : json.indexOf(",", start),
            json.indexOf("}", start) == -1 ? json.length() : json.indexOf("}", start)
        );
        
        String value = json.substring(start, end).trim();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Escape JSON string
     */
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}