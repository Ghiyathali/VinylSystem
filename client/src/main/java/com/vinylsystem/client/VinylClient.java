package com.vinylsystem.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.vinylsystem.common.JsonUtils;
import com.vinylsystem.common.LookupMessage;
import com.vinylsystem.common.ProtocolConstants;
import com.vinylsystem.common.ResponseMessage;
import com.vinylsystem.common.StatusCodes;
import com.vinylsystem.common.ValidationUtils;

/**
 * Vinyl Client for directory lookups and server connections
 */
public class VinylClient {
    private final String directoryIP;
    private final int directoryUdpPort;
    
    public VinylClient(String directoryIP, int directoryUdpPort) {
        this.directoryIP = directoryIP;
        this.directoryUdpPort = directoryUdpPort;
    }
    
    public VinylClient() {
        this("localhost", ProtocolConstants.DEFAULT_DIRECTORY_UDP_PORT);
    }
    
    /**
     * Lookup server information by name
     */
    public ServerInfo lookupServer(String serverName) throws IOException {
        // Validate server name
        if (!ValidationUtils.isValidServerName(serverName)) {
            throw new IllegalArgumentException("Invalid server name format. Expected: <string>.group#.pro2[x|y]");
        }
        
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(ProtocolConstants.SOCKET_TIMEOUT);
            
            // Create lookup message
            LookupMessage message = new LookupMessage(ProtocolConstants.MSG_LOOKUP, serverName);
            String jsonMessage = JsonUtils.toJson(message);
            
            System.out.println("Looking up server: " + serverName);
            System.out.println("Sending lookup request: " + jsonMessage);
            
            // Send lookup request
            byte[] requestBytes = jsonMessage.getBytes();
            InetAddress directoryAddress = InetAddress.getByName(directoryIP);
            DatagramPacket requestPacket = new DatagramPacket(
                requestBytes, requestBytes.length, directoryAddress, directoryUdpPort
            );
            
            socket.send(requestPacket);
            
            // Receive response
            byte[] responseBuffer = new byte[ProtocolConstants.MAX_MESSAGE_SIZE];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);
            
            String responseJson = new String(responsePacket.getData(), 0, responsePacket.getLength());
            System.out.println("Directory response: " + responseJson);
            
            // Parse response
            try {
                ResponseMessage response = JsonUtils.parseResponseMessage(responseJson);
                
                if (StatusCodes.SUCCESS.equals(response.getStatusCode())) {
                    if (response.getServerIP() != null && response.getServerPort() != null) {
                        return new ServerInfo(serverName, response.getServerIP(), response.getServerPort());
                    } else {
                        throw new IOException("Invalid server information in response");
                    }
                } else {
                    throw new ServerNotFoundException("Server lookup failed: " + response.getMessage() + 
                                                    " (Status: " + response.getStatusCode() + ")");
                }
                
            } catch (Exception e) {
                throw new IOException("Invalid response format from directory: " + e.getMessage());
            }
        }
    }
    
    /**
     * Connect to a vinyl server
     */
    public Socket connectToServer(String serverName) throws IOException {
        ServerInfo serverInfo = lookupServer(serverName);
        return connectToServer(serverInfo);
    }
    
    /**
     * Connect to a vinyl server using server info
     */
    public Socket connectToServer(ServerInfo serverInfo) throws IOException {
        System.out.println("Connecting to server: " + serverInfo.getName() + 
                          " at " + serverInfo.getIp() + ":" + serverInfo.getPort());
        
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(serverInfo.getIp(), serverInfo.getPort()), 
                      ProtocolConstants.SOCKET_TIMEOUT);
        
        System.out.println("Connected to vinyl server successfully");
        return socket;
    }
    
    /**
     * Send a message to a vinyl server and get response
     */
    public String sendMessage(String serverName, String message) throws IOException {
        try (Socket socket = connectToServer(serverName);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            socket.setSoTimeout(ProtocolConstants.SOCKET_TIMEOUT);
            
            System.out.println("Sending message: " + message);
            writer.println(message);
            
            String response = reader.readLine();
            System.out.println("Server response: " + response);
            
            return response;
        }
    }
    
    /**
     * Interactive client session
     */
    public void startInteractiveSession(String serverName) throws IOException {
        ServerInfo serverInfo = lookupServer(serverName);
        System.out.println("Starting interactive session with: " + serverInfo.getName());
        
        try (Socket socket = connectToServer(serverInfo);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            
            socket.setSoTimeout(0); // No timeout for interactive session
            
            System.out.println("Connected! Type messages (or 'quit' to exit):");
            
            String userInput;
            while ((userInput = consoleReader.readLine()) != null) {
                if ("quit".equalsIgnoreCase(userInput.trim())) {
                    break;
                }
                
                writer.println(userInput);
                String response = reader.readLine();
                if (response != null) {
                    System.out.println("Server: " + response);
                } else {
                    System.out.println("Server disconnected");
                    break;
                }
            }
            
            System.out.println("Session ended");
        }
    }
    
    /**
     * Server information container
     */
    public static class ServerInfo {
        private final String name;
        private final String ip;
        private final int port;
        
        public ServerInfo(String name, String ip, int port) {
            this.name = name;
            this.ip = ip;
            this.port = port;
        }
        
        public String getName() { return name; }
        public String getIp() { return ip; }
        public int getPort() { return port; }
        
        @Override
        public String toString() {
            return name + " at " + ip + ":" + port;
        }
    }
    
    /**
     * Exception for server not found
     */
    public static class ServerNotFoundException extends IOException {
        public ServerNotFoundException(String message) {
            super(message);
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java VinylClient <server-name> [directory-ip] [directory-port] [mode]");
            System.err.println("Modes: lookup, connect, message <text>, interactive");
            System.err.println("Example: java VinylClient myserver.group1.pro2x localhost 8081 interactive");
            System.exit(1);
        }
        
        String serverName = args[0];
        String directoryIP = args.length > 1 ? args[1] : "localhost";
        int directoryPort = args.length > 2 ? Integer.parseInt(args[2]) : ProtocolConstants.DEFAULT_DIRECTORY_UDP_PORT;
        String mode = args.length > 3 ? args[3] : "lookup";
        
        VinylClient client = new VinylClient(directoryIP, directoryPort);
        
        try {
            switch (mode.toLowerCase()) {
                case "lookup":
                    ServerInfo info = client.lookupServer(serverName);
                    System.out.println("Server found: " + info);
                    break;
                    
                case "connect":
                    try (Socket socket = client.connectToServer(serverName)) {
                        System.out.println("Successfully connected to: " + serverName);
                        System.out.println("Local address: " + socket.getLocalSocketAddress());
                        System.out.println("Remote address: " + socket.getRemoteSocketAddress());
                    }
                    break;
                    
                case "message":
                    String message = args.length > 4 ? args[4] : "Hello from client!";
                    String response = client.sendMessage(serverName, message);
                    System.out.println("Response: " + response);
                    break;
                    
                case "interactive":
                    client.startInteractiveSession(serverName);
                    break;
                    
                default:
                    System.err.println("Unknown mode: " + mode);
                    System.exit(1);
            }
            
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}