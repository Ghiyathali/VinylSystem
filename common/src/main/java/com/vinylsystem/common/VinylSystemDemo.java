package com.vinylsystem.common;

import java.util.List;

/**
 * Comprehensive demo of VinylSystem with different music searches
 */
public class VinylSystemDemo {
    public static void main(String[] args) {
        try {
            System.out.println("🎵 VINYLSYSTEM COMPREHENSIVE DEMO 🎵\n");
            
            DiscogsApiService api = new DiscogsApiService();
            
            // Test different artists and genres
            String[] searches = {
                "The Beatles Abbey Road",
                "Bob Dylan",
                "Miles Davis",
                "Nirvana Nevermind",
                "Queen Bohemian Rhapsody"
            };
            
            for (String search : searches) {
                System.out.println("🔍 Searching for: " + search);
                System.out.println("━".repeat(50));
                
                List<MusicRelease> releases = api.searchReleases(search, 3);
                
                if (releases.isEmpty()) {
                    System.out.println("No vinyl records found.\n");
                    continue;
                }
                
                System.out.println("Found " + releases.size() + " vinyl records:");
                for (MusicRelease release : releases) {
                    System.out.printf("  🎼 %s by %s (%s) - %s vinyl [ID: %s]%n",
                        release.getTitle(),
                        release.getArtist(),
                        release.getYear(),
                        release.getFormat(),
                        release.getId()
                    );
                }
                System.out.println();
            }
            
            System.out.println("🎉 DEMO COMPLETE!");
            System.out.println("This demonstrates the VinylSystem's ability to search");
            System.out.println("real vinyl records from Discogs' database of 14+ million releases!");
            
        } catch (Exception e) {
            System.err.println("Demo error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}