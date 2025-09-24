package com.vinylsystem.common;

/**
 * Represents a music search request from a client
 */
public class MusicSearchRequest {
    private String messageType;
    private String query;
    private String searchType; // "release", "artist", "album"
    private int limit;
    
    public MusicSearchRequest() {}
    
    public MusicSearchRequest(String messageType, String query, String searchType, int limit) {
        this.messageType = messageType;
        this.query = query;
        this.searchType = searchType;
        this.limit = limit;
    }
    
    // Getters and setters
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    
    public String getSearchType() { return searchType; }
    public void setSearchType(String searchType) { this.searchType = searchType; }
    
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
    
    @Override
    public String toString() {
        return String.format("MusicSearchRequest{type='%s', query='%s', searchType='%s', limit=%d}",
                messageType, query, searchType, limit);
    }
}