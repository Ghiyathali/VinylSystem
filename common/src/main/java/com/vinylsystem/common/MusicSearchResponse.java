package com.vinylsystem.common;

import java.util.List;

/**
 * Represents a music search response to a client
 */
public class MusicSearchResponse {
    private String messageType;
    private String statusCode;
    private String message;
    private List<MusicRelease> results;
    private int totalFound;
    
    public MusicSearchResponse() {}
    
    public MusicSearchResponse(String messageType, String statusCode, String message) {
        this.messageType = messageType;
        this.statusCode = statusCode;
        this.message = message;
    }
    
    public MusicSearchResponse(String messageType, String statusCode, List<MusicRelease> results, int totalFound) {
        this.messageType = messageType;
        this.statusCode = statusCode;
        this.results = results;
        this.totalFound = totalFound;
        this.message = "Search completed successfully";
    }
    
    // Getters and setters
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public List<MusicRelease> getResults() { return results; }
    public void setResults(List<MusicRelease> results) { this.results = results; }
    
    public int getTotalFound() { return totalFound; }
    public void setTotalFound(int totalFound) { this.totalFound = totalFound; }
    
    @Override
    public String toString() {
        return String.format("MusicSearchResponse{status='%s', message='%s', resultsCount=%d, totalFound=%d}",
                statusCode, message, results != null ? results.size() : 0, totalFound);
    }
}