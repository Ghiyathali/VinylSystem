package com.vinylsystem.common;

import java.util.List;

/**
 * Represents search results from Discogs API
 */
public class DiscogsSearchResult {
    private List<MusicRelease> results;
    private int totalResults;
    private int page;
    private int pages;
    private int perPage;
    
    // Default constructor for JSON parsing
    public DiscogsSearchResult() {}
    
    public DiscogsSearchResult(List<MusicRelease> results, int totalResults) {
        this.results = results;
        this.totalResults = totalResults;
    }
    
    // Getters and Setters
    public List<MusicRelease> getResults() { return results; }
    public void setResults(List<MusicRelease> results) { this.results = results; }
    
    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }
    
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    
    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }
    
    public int getPerPage() { return perPage; }
    public void setPerPage(int perPage) { this.perPage = perPage; }
    
    @Override
    public String toString() {
        return String.format("DiscogsSearchResult{totalResults=%d, page=%d, pages=%d, results=%d}",
                totalResults, page, pages, results != null ? results.size() : 0);
    }
}