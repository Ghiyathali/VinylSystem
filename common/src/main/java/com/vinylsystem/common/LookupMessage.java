package com.vinylsystem.common;

/**
 * Lookup message for UDP protocol (client to directory)
 */
public class LookupMessage {
    private String messageType;
    private String serverName;
    
    public LookupMessage() {}
    
    public LookupMessage(String messageType, String serverName) {
        this.messageType = messageType;
        this.serverName = serverName;
    }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public String getServerName() { return serverName; }
    public void setServerName(String serverName) { this.serverName = serverName; }
}