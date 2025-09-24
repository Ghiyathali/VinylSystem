package com.vinylsystem.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Simple demo client to test music search functionality
 */
public class MusicSearchDemo {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java MusicSearchDemo <search-query>");
            System.out.println("Example: java MusicSearchDemo \"Pink Floyd\"");
            System.exit(1);
        }
        
        String query = args[0];
        String serverHost = "127.0.0.1";
        int serverPort = 9001;
        
        System.out.println("=== VinylSystem Music Search Demo ===");
        System.out.println("Connecting to vinyl server at " + serverHost + ":" + serverPort);
        System.out.println("Searching for: " + query);
        System.out.println();
        
        try (Socket socket = new Socket(serverHost, serverPort);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            socket.setSoTimeout(30000); // 30 seconds timeout for music search
            
            // Send search query
            writer.println(query);
            
            // Read response
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            
        } catch (IOException e) {
            System.err.println("Error connecting to vinyl server: " + e.getMessage());
            System.err.println("Make sure the vinyl server is running:");
            System.err.println("java -cp \"build\" com.vinylsystem.server.VinylServer \"rock.group1.pro2x\" \"127.0.0.1\" 9001");
        }
    }
}