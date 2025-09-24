package com.vinylsystem.common;

import java.util.List;

/**
 * Simple test program to verify Discogs API integration
 */
public class DiscogsApiTest {
    public static void main(String[] args) {
        System.out.println("Testing Discogs API Integration...\n");
        
        try {
            DiscogsApiService api = new DiscogsApiService();
            
            // Test 1: Search for Pink Floyd
            System.out.println("=== Test 1: Searching for 'Pink Floyd Dark Side' ===");
            List<MusicRelease> releases = api.searchReleases("Pink Floyd Dark Side of the Moon", 5);
            
            if (releases.isEmpty()) {
                System.out.println("No results found!");
            } else {
                System.out.println("Found " + releases.size() + " releases:");
                for (MusicRelease release : releases) {
                    System.out.println("  - " + release);
                }
            }
            
            System.out.println("\n=== Test 2: Searching for 'Led Zeppelin' ===");
            releases = api.searchReleases("Led Zeppelin IV", 3);
            
            if (releases.isEmpty()) {
                System.out.println("No results found!");
            } else {
                System.out.println("Found " + releases.size() + " releases:");
                for (MusicRelease release : releases) {
                    System.out.println("  - " + release);
                }
            }
            
            System.out.println("\n=== API Test Complete! ===");
            
        } catch (Exception e) {
            System.err.println("API Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}