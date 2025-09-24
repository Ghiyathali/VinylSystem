package com.vinylsystem.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Simple HTTP client for making GET requests to REST APIs
 */
public class HttpClient {
    private static final int DEFAULT_TIMEOUT = 10000; // 10 seconds
    private static final String USER_AGENT = "VinylSystem/1.0";
    
    /**
     * Make a GET request to the specified URL
     * @param url The URL to request
     * @return The response body as a string
     * @throws IOException If the request fails
     */
    public static String get(String url) throws IOException {
        return get(url, null);
    }
    
    /**
     * Make a GET request to the specified URL with headers
     * @param url The URL to request
     * @param headers Map of headers to include in the request
     * @return The response body as a string
     * @throws IOException If the request fails
     */
    public static String get(String url, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            
            // Set request properties
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(DEFAULT_TIMEOUT);
            connection.setReadTimeout(DEFAULT_TIMEOUT);
            connection.setRequestProperty("User-Agent", USER_AGENT);
            
            // Add custom headers
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    connection.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            
            // Check response code
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP request failed with response code: " + responseCode + 
                                    " for URL: " + url);
            }
            
            // Read response
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append('\n');
                }
            }
            
            return response.toString().trim();
            
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * URL encode a query parameter
     * @param value The value to encode
     * @return The URL-encoded value
     */
    public static String urlEncode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            // Fallback to simple replacement
            return value.replace(" ", "%20")
                       .replace("&", "%26")
                       .replace("=", "%3D");
        }
    }
}