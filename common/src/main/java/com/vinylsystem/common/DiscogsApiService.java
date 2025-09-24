package com.vinylsystem.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for interacting with the Discogs API
 */
public class DiscogsApiService {
    private final String baseUrl;
    private final String accessToken;
    private final Map<String, String> defaultHeaders;
    
    public DiscogsApiService() {
        this.baseUrl = ConfigManager.getDiscogsBaseUrl();
        this.accessToken = ConfigManager.getDiscogsAccessToken();
        
        if (accessToken == null) {
            throw new IllegalStateException("Discogs access token is required. " +
                "Set discogs.access.token in config.properties or DISCOGS_ACCESS_TOKEN environment variable");
        }
        
        // Set up default headers for all requests
        this.defaultHeaders = new HashMap<>();
        // Use the Personal Access Token in Authorization header
        this.defaultHeaders.put("Authorization", "Discogs token=" + accessToken);
        this.defaultHeaders.put("User-Agent", "VinylSystem/1.0 +https://github.com/Ghiyathali/VinylSystem");
    }
    
    /**
     * Search for releases (albums/singles) by query
     * @param query Search query (artist, album, etc.)
     * @param limit Maximum number of results (default 10, max 100)
     * @return List of matching music releases
     * @throws IOException If the API request fails
     */
    public List<MusicRelease> searchReleases(String query, int limit) throws IOException {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }
        
        // Build API URL (use Authorization header instead of query parameter)
        String encodedQuery = HttpClient.urlEncode(query.trim());
        String url = String.format("%s/database/search?q=%s&type=release&per_page=%d",
                baseUrl, encodedQuery, Math.min(limit, 100));
        
        System.out.println("Discogs API Request: " + url);
        
        // Make API request
        String response = HttpClient.get(url, defaultHeaders);
        
        // Debug: Print more of response to see structure
        System.out.println("API Response preview: " + 
            (response.length() > 500 ? response.substring(0, 500) + "..." : response));
        
        // Also check if results array exists
        if (response.contains("\"results\":")) {
            System.out.println("✓ 'results' field found in response");
            int resultsIndex = response.indexOf("\"results\":");
            String resultsSection = response.substring(resultsIndex, Math.min(resultsIndex + 300, response.length()));
            System.out.println("Results section: " + resultsSection);
        } else {
            System.out.println("✗ 'results' field NOT found in response");
        }
        
        // Parse the response (simplified JSON parsing)
        return parseSearchResponse(response);
    }
    
    /**
     * Search for releases with default limit of 10
     */
    public List<MusicRelease> searchReleases(String query) throws IOException {
        return searchReleases(query, 10);
    }
    
    /**
     * Get detailed information about a specific release
     * @param releaseId The Discogs release ID
     * @return Detailed release information
     * @throws IOException If the API request fails
     */
    public MusicRelease getRelease(String releaseId) throws IOException {
        if (releaseId == null || releaseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Release ID cannot be empty");
        }
        
                String url = String.format("%s/releases/%s", baseUrl, releaseId.trim());
        
        System.out.println("Discogs API Request: " + url);
        
        String response = HttpClient.get(url, defaultHeaders);
        
        // Parse the detailed release response
        return parseReleaseResponse(response);
    }
    
    /**
     * Search for artists by name
     * @param artistName Artist name to search for
     * @param limit Maximum number of results
     * @return List of matching artists
     * @throws IOException If the API request fails
     */
    public List<MusicArtist> searchArtists(String artistName, int limit) throws IOException {
        if (artistName == null || artistName.trim().isEmpty()) {
            throw new IllegalArgumentException("Artist name cannot be empty");
        }
        
        String encodedName = HttpClient.urlEncode(artistName.trim());
        String url = String.format("%s/database/search?q=%s&type=artist&per_page=%d",
                baseUrl, encodedName, Math.min(limit, 100));
        
        System.out.println("Discogs API Request: " + url);
        
        String response = HttpClient.get(url, defaultHeaders);
        
        return parseArtistSearchResponse(response);
    }
    
    // Simplified JSON parsing methods
    // Note: In a production system, you'd use a proper JSON library like Jackson or Gson
    
    private List<MusicRelease> parseSearchResponse(String json) {
        List<MusicRelease> releases = new ArrayList<>();
        
        try {
            // Find the results array in the JSON (handle with or without space)
            String resultsStart1 = "\"results\":[";
            String resultsStart2 = "\"results\": [";
            int startIndex = json.indexOf(resultsStart1);
            String actualResultsStart = resultsStart1;
            
            if (startIndex == -1) {
                startIndex = json.indexOf(resultsStart2);
                actualResultsStart = resultsStart2;
            }
            
            if (startIndex == -1) {
                System.out.println("Debug: No 'results' array found (tried both with and without space)");
                return releases; // No results found
            }
            
            System.out.println("Debug: Found results array at index " + startIndex + " with pattern: " + actualResultsStart);
            
            // Find the end of the results array
            int resultsArrayStart = startIndex + actualResultsStart.length();
            int bracketCount = 1;
            int endIndex = resultsArrayStart;
            
            while (bracketCount > 0 && endIndex < json.length()) {
                char c = json.charAt(endIndex);
                if (c == '[') bracketCount++;
                else if (c == ']') bracketCount--;
                endIndex++;
            }
            
            String resultsArray = json.substring(resultsArrayStart, endIndex - 1);
            System.out.println("Debug: Results array length: " + resultsArray.length());
            System.out.println("Debug: Results array preview: " + 
                (resultsArray.length() > 300 ? resultsArray.substring(0, 300) + "..." : resultsArray));
            
            // Simple parsing - split by object boundaries 
            String[] objects = resultsArray.split("\\},\\s*\\{");
            System.out.println("Debug: Split into " + objects.length + " objects");
            
            for (int i = 0; i < objects.length; i++) {
                String obj = objects[i];
                System.out.println("Debug: Processing object " + i + " (first 100 chars): " + 
                    (obj.length() > 100 ? obj.substring(0, 100) + "..." : obj));
                
                // Clean up the object string
                if (!obj.startsWith("{")) obj = "{" + obj;
                if (!obj.endsWith("}")) obj = obj + "}";
                
                MusicRelease release = parseReleaseFromJson(obj);
                if (release != null) {
                    System.out.println("Debug: Successfully parsed release: " + release.getTitle());
                    releases.add(release);
                } else {
                    System.out.println("Debug: Failed to parse object");
                }
                
                if (releases.size() >= 10) break; // Limit results
            }
            
            System.out.println("Debug: Final release count: " + releases.size());
            
        } catch (Exception e) {
            System.err.println("Error parsing Discogs response: " + e.getMessage());
            e.printStackTrace();
            // Return partial results if any were parsed
        }
        
        return releases;
    }
    
    private MusicRelease parseReleaseResponse(String json) {
        return parseReleaseFromJson(json);
    }
    
    private MusicRelease parseReleaseFromJson(String json) {
        try {
            MusicRelease release = new MusicRelease();
            
            // Extract ID
            release.setId(extractJsonValue(json, "\"id\":", ","));
            
            // Extract title
            release.setTitle(extractJsonValue(json, "\"title\":", ","));
            
            // Extract year
            release.setYear(extractJsonValue(json, "\"year\":", ","));
            
            // Extract format (look for vinyl, LP, etc.)
            String format = extractJsonValue(json, "\"format\":", "]");
            if (format != null && format.contains("LP")) {
                release.setFormat("LP");
            } else if (format != null && format.contains("Single")) {
                release.setFormat("Single");
            } else {
                release.setFormat("Release");
            }
            
            // Extract genre
            String genre = extractJsonValue(json, "\"genre\":", "]");
            if (genre != null) {
                // Clean up genre string
                genre = genre.replaceAll("[\\[\\]\"]", "").split(",")[0].trim();
                release.setGenre(genre);
            }
            
            // Set a default artist if we can't parse it properly
            if (release.getTitle() != null && release.getTitle().contains(" - ")) {
                String[] parts = release.getTitle().split(" - ", 2);
                release.setArtist(parts[0].trim());
                release.setTitle(parts[1].trim());
            }
            
            return release;
            
        } catch (Exception e) {
            System.err.println("Error parsing individual release: " + e.getMessage());
            return null;
        }
    }
    
    private List<MusicArtist> parseArtistSearchResponse(String json) {
        List<MusicArtist> artists = new ArrayList<>();
        // Simplified implementation - would be enhanced in production
        return artists;
    }
    
    // Simple helper method to extract JSON values
    private String extractJsonValue(String json, String key, String endChar) {
        try {
            int startIndex = json.indexOf(key);
            if (startIndex == -1) return null;
            
            startIndex += key.length();
            
            // Skip whitespace and quotes
            while (startIndex < json.length() && 
                   (json.charAt(startIndex) == ' ' || json.charAt(startIndex) == '"')) {
                startIndex++;
            }
            
            // Find end of value
            int endIndex = json.indexOf(endChar, startIndex);
            if (endIndex == -1) endIndex = json.length();
            
            // Look for quote before endChar
            int quoteIndex = json.lastIndexOf('"', endIndex);
            if (quoteIndex > startIndex && quoteIndex < endIndex) {
                endIndex = quoteIndex;
            }
            
            String value = json.substring(startIndex, endIndex).trim();
            return value.isEmpty() ? null : value;
            
        } catch (Exception e) {
            return null;
        }
    }
}