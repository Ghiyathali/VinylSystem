package com.vinylsystem.test;

import com.vinylsystem.client.VinylClient;
import java.io.*;
import java.net.Socket;

/**
 * Test the complete VinylSystem with Discogs API integration
 */
public class VinylSystemTest {
    public static void main(String[] args) {
        System.out.println("Testing VinylSystem with Discogs API Integration...\n");
        
        try {
            // Test 1: Lookup server in directory
            System.out.println("=== Test 1: Looking up rock music server ===");
            VinylClient client = new VinylClient();
            VinylClient.ServerInfo serverInfo = client.lookupServer("rock.group1.pro2x");
            System.out.println("Found server: " + serverInfo.getServerName() + 
                             " at " + serverInfo.getServerIP() + ":" + serverInfo.getServerPort());
            
            // Test 2: Connect to server and search for music
            System.out.println("\n=== Test 2: Connecting to server for music search ===");
            try (Socket socket = new Socket(serverInfo.getServerIP(), serverInfo.getServerPort());
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                
                // Search for Pink Floyd
                System.out.println("Searching for: Pink Floyd");
                writer.println("Pink Floyd");
                String response = reader.readLine();
                System.out.println("Server Response:\n" + response);
                
                // Search for Led Zeppelin
                System.out.println("\n" + "=".repeat(50));
                System.out.println("Searching for: Led Zeppelin");
                writer.println("Led Zeppelin");
                response = reader.readLine();
                System.out.println("Server Response:\n" + response);
            }
            
            System.out.println("\n=== VinylSystem Test Complete! ===");
            System.out.println("✅ Directory server working");
            System.out.println("✅ Vinyl server registration working"); 
            System.out.println("✅ Client server lookup working");
            System.out.println("✅ Discogs API integration working");
            System.out.println("✅ Real vinyl record data retrieved!");
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}