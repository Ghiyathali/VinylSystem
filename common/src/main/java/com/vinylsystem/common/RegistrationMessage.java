package com.vinylsystem.common;

/**
 * Registration message for TCP protocol (vinyl server to directory)
 */
public class RegistrationMessage {
    private String messageType;
    private String serverName;
    private String serverIP;
    private int serverPort;
    private int ttl;
    
    public RegistrationMessage() {}
    
    public RegistrationMessage(String messageType, String serverName, String serverIP, int serverPort, int ttl) {
        this.messageType = messageType;
        this.serverName = serverName;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.ttl = ttl;
    }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public String getServerName() { return serverName; }
    public void setServerName(String serverName) { this.serverName = serverName; }
    
    public String getServerIP() { return serverIP; }
    public void setServerIP(String serverIP) { this.serverIP = serverIP; }
    
    public int getServerPort() { return serverPort; }
    public void setServerPort(int serverPort) { this.serverPort = serverPort; }
    
    public int getTtl() { return ttl; }
    public void setTtl(int ttl) { this.ttl = ttl; }
}