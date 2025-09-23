package com.vinylsystem.common;

/**
 * Response message for both TCP and UDP protocols
 */
public class ResponseMessage {
    private String statusCode;
    private String message;
    private String serverIP;
    private Integer serverPort;
    
    public ResponseMessage() {}
    
    public ResponseMessage(String statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
    
    public ResponseMessage(String statusCode, String message, String serverIP, Integer serverPort) {
        this.statusCode = statusCode;
        this.message = message;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }
    
    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getServerIP() { return serverIP; }
    public void setServerIP(String serverIP) { this.serverIP = serverIP; }
    
    public Integer getServerPort() { return serverPort; }
    public void setServerPort(Integer serverPort) { this.serverPort = serverPort; }
}