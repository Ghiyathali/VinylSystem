package com.vinylsystem.server;

import com.vinylsystem.common.*;
import java.net.*;
import java.io.*;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Vinyl Server that registers with directory server and handles client connections
 */
public class VinylServer {
    private final String serverName;
    private final String serverIP;
    private final int serverPort;
    private final String directoryIP;
    private final int directoryTcpPort;
    private final int ttlSeconds;
    
    private final ScheduledExecutorService scheduler;
    private final DiscogsApiService discogsApi;
    
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    
    public VinylServer(String serverName, String serverIP, int serverPort, 
                       String directoryIP, int directoryTcpPort, int ttlSeconds) {
        this.serverName = serverName;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.directoryIP = directoryIP;
        this.directoryTcpPort = directoryTcpPort;
        this.ttlSeconds = ttlSeconds;
        
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.discogsApi = new DiscogsApiService();
    }
    
    public VinylServer(String serverName, String serverIP, int serverPort) {
        this(serverName, serverIP, serverPort, "localhost", 
             ProtocolConstants.DEFAULT_DIRECTORY_TCP_PORT, ProtocolConstants.DEFAULT_TTL_SECONDS);
    }
    
    /**
     * Start the vinyl server
     */
    public void start() throws IOException {
        // Validate server name format
        if (!ValidationUtils.isValidServerName(serverName)) {
            throw new IllegalArgumentException("Invalid server name format. Expected: <string>.group#.pro2[x|y]");
        }
        
        if (!ValidationUtils.isValidIPAddress(serverIP)) {
            throw new IllegalArgumentException("Invalid server IP address");
        }
        
        if (!ValidationUtils.isValidPort(serverPort)) {
            throw new IllegalArgumentException("Invalid server port");
        }
        
        System.out.println("Starting Vinyl Server: " + serverName);
        System.out.println("Server IP: " + serverIP + ":" + serverPort);
        System.out.println("Directory: " + directoryIP + ":" + directoryTcpPort);
        
        // Start server socket
        serverSocket = new ServerSocket(serverPort);
        running = true;
        
        // Register with directory
        if (!registerWithDirectory()) {
            throw new IOException("Failed to register with directory server");
        }
        
        // Schedule TTL refresh
        scheduleTTLRefresh();
        
        // Start accepting client connections
        startClientListener();
        
        System.out.println("Vinyl Server started successfully");
    }
    
    /**
     * Stop the vinyl server
     */
    public void stop() {
        System.out.println("Stopping Vinyl Server: " + serverName);
        running = false;
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
        
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Vinyl Server stopped");
    }
    
    /**
     * Register with directory server
     */
    private boolean registerWithDirectory() {
        return sendRegistrationMessage(ProtocolConstants.MSG_REGISTER);
    }
    
    /**
     * Update registration with directory server
     */
    private boolean updateRegistration() {
        return sendRegistrationMessage(ProtocolConstants.MSG_UPDATE);
    }
    
    /**
     * Send registration or update message to directory
     */
    private boolean sendRegistrationMessage(String messageType) {
        try (Socket socket = new Socket(directoryIP, directoryTcpPort);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            socket.setSoTimeout(ProtocolConstants.SOCKET_TIMEOUT);
            
            // Create registration message
            RegistrationMessage message = new RegistrationMessage(
                messageType, serverName, serverIP, serverPort, ttlSeconds
            );
            
            String jsonMessage = JsonUtils.toJson(message);
            System.out.println("Sending " + messageType + " to directory: " + jsonMessage);
            
            // Send message
            writer.println(jsonMessage);
            
            // Read response
            String response = reader.readLine();
            if (response != null) {
                System.out.println("Directory response: " + response);
                
                try {
                    ResponseMessage responseMsg = JsonUtils.parseResponseMessage(response);
                    boolean success = StatusCodes.SUCCESS.equals(responseMsg.getStatusCode());
                    
                    if (!success) {
                        System.err.println("Directory error: " + responseMsg.getMessage());
                    }
                    
                    return success;
                } catch (Exception e) {
                    System.err.println("Invalid response format from directory");
                    return false;
                }
            }
            
            return false;
            
        } catch (IOException e) {
            System.err.println("Error communicating with directory: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Schedule TTL refresh
     */
    private void scheduleTTLRefresh() {
        // Refresh at 60% of TTL to ensure no expiration
        int refreshInterval = (int) (ttlSeconds * 0.6);
        if (refreshInterval < 10) {
            refreshInterval = 10; // Minimum 10 seconds
        }
        
        scheduler.scheduleAtFixedRate(() -> {
            if (running) {
                System.out.println("Refreshing TTL registration...");
                if (!updateRegistration()) {
                    System.err.println("Failed to refresh TTL, attempting re-registration...");
                    if (!registerWithDirectory()) {
                        System.err.println("Failed to re-register with directory!");
                    }
                }
            }
        }, refreshInterval, refreshInterval, TimeUnit.SECONDS);
        
        System.out.println("TTL refresh scheduled every " + refreshInterval + " seconds");
    }
    
    /**
     * Start listening for client connections
     */
    private void startClientListener() {
        scheduler.submit(() -> {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    scheduler.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        });
    }
    
    /**
     * Handle client connection
     */
    private void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            clientSocket.setSoTimeout(ProtocolConstants.SOCKET_TIMEOUT);
            
            System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
            
            String request;
            while ((request = reader.readLine()) != null && running) {
                System.out.println("Client request: " + request);
                
                String response = handleMusicRequest(request);
                writer.println(response);
                
                System.out.println("Client response: " + response);
            }
            
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected");
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
    
    /**
     * Handle music search requests from clients
     */
    private String handleMusicRequest(String request) {
        try {
            // For simplicity, treat all requests as simple music searches
            return processSimpleSearch(request);
            
        } catch (Exception e) {
            System.err.println("Error processing music request: " + e.getMessage());
            return "ERROR: Could not process music search request";
        }
    }
    
    /**
     * Process simple text search queries
     */
    private String processSimpleSearch(String query) {
        try {
            System.out.println("Processing simple search for: " + query);
            
            // Search using Discogs API
            List<MusicRelease> results = discogsApi.searchReleases(query, 3);
            
            // Create a simple text response for basic clients
            StringBuilder response = new StringBuilder();
            response.append("Music Search Results from ").append(serverName).append(":\n");
            response.append("Query: ").append(query).append("\n");
            response.append("Found ").append(results.size()).append(" vinyl records:\n");
            
            for (int i = 0; i < results.size(); i++) {
                MusicRelease release = results.get(i);
                response.append(String.format("%d. %s - %s (%s) [%s]\n", 
                    i + 1,
                    release.getArtist() != null ? release.getArtist() : "Unknown Artist",
                    release.getTitle() != null ? release.getTitle() : "Unknown Title",
                    release.getYear() != null ? release.getYear() : "Unknown Year",
                    release.getFormat() != null ? release.getFormat() : "Vinyl"
                ));
            }
            
            if (results.isEmpty()) {
                response.append("No vinyl records found for your search.\n");
            }
            
            return response.toString();
            
        } catch (Exception e) {
            return "Error searching for music: " + e.getMessage();
        }
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java VinylServer <server-name> <server-ip> <server-port> [directory-ip] [directory-port] [ttl-seconds]");
            System.err.println("Example: java VinylServer myserver.group1.pro2x 192.168.1.100 9090");
            System.exit(1);
        }
        
        String serverName = args[0];
        String serverIP = args[1];
        int serverPort = Integer.parseInt(args[2]);
        
        String directoryIP = args.length > 3 ? args[3] : "localhost";
        int directoryTcpPort = args.length > 4 ? Integer.parseInt(args[4]) : ProtocolConstants.DEFAULT_DIRECTORY_TCP_PORT;
        int ttlSeconds = args.length > 5 ? Integer.parseInt(args[5]) : ProtocolConstants.DEFAULT_TTL_SECONDS;
        
        VinylServer server = new VinylServer(serverName, serverIP, serverPort, directoryIP, directoryTcpPort, ttlSeconds);
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        
        try {
            server.start();
            
            // Keep the main thread alive
            while (server.running) {
                Thread.sleep(1000);
            }
        } catch (IOException e) {
            System.err.println("Failed to start Vinyl Server: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Vinyl Server interrupted");
            Thread.currentThread().interrupt();
        } finally {
            server.stop();
        }
    }
}