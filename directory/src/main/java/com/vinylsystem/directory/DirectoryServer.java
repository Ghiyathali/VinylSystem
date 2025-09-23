package com.vinylsystem.directory;

import com.vinylsystem.common.ProtocolConstants;
import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Core Directory Server with TCP and UDP listeners
 */
public class DirectoryServer {
    private final int tcpPort;
    private final int udpPort;
    private final ServerRegistry registry;
    private final MessageHandler messageHandler;
    private final ExecutorService threadPool;
    
    private ServerSocket tcpServer;
    private DatagramSocket udpServer;
    private volatile boolean running = false;
    
    public DirectoryServer(int tcpPort, int udpPort) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.registry = new ServerRegistry();
        this.messageHandler = new MessageHandler(registry);
        this.threadPool = Executors.newCachedThreadPool();
    }
    
    public DirectoryServer() {
        this(ProtocolConstants.DEFAULT_DIRECTORY_TCP_PORT, ProtocolConstants.DEFAULT_DIRECTORY_UDP_PORT);
    }
    
    /**
     * Start the directory server
     */
    public void start() throws IOException {
        System.out.println("Starting Directory Server...");
        
        // Start TCP server for vinyl server registrations
        tcpServer = new ServerSocket(tcpPort);
        System.out.println("TCP Server listening on port " + tcpPort + " for vinyl server registrations");
        
        // Start UDP server for client lookups
        udpServer = new DatagramSocket(udpPort);
        System.out.println("UDP Server listening on port " + udpPort + " for client lookups");
        
        running = true;
        
        // Start TCP listener in separate thread
        threadPool.submit(this::runTcpServer);
        
        // Start UDP listener in separate thread
        threadPool.submit(this::runUdpServer);
        
        System.out.println("Directory Server started successfully");
        System.out.println("Registered servers: " + registry.getServerCount());
    }
    
    /**
     * Stop the directory server
     */
    public void stop() {
        System.out.println("Stopping Directory Server...");
        running = false;
        
        try {
            if (tcpServer != null && !tcpServer.isClosed()) {
                tcpServer.close();
            }
            if (udpServer != null && !udpServer.isClosed()) {
                udpServer.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing servers: " + e.getMessage());
        }
        
        threadPool.shutdown();
        registry.shutdown();
        System.out.println("Directory Server stopped");
    }
    
    /**
     * TCP server loop for vinyl server registrations
     */
    private void runTcpServer() {
        while (running) {
            try {
                Socket clientSocket = tcpServer.accept();
                threadPool.submit(() -> handleTcpClient(clientSocket));
            } catch (IOException e) {
                if (running) {
                    System.err.println("TCP Server error: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Handle TCP client connection (vinyl server registration)
     */
    private void handleTcpClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            clientSocket.setSoTimeout(ProtocolConstants.SOCKET_TIMEOUT);
            
            String requestMessage = reader.readLine();
            if (requestMessage != null) {
                System.out.println("TCP Request from " + clientSocket.getRemoteSocketAddress() + ": " + requestMessage);
                
                String response = messageHandler.handleRegistrationMessage(requestMessage);
                writer.println(response);
                
                System.out.println("TCP Response: " + response);
            }
            
        } catch (IOException e) {
            System.err.println("Error handling TCP client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing TCP client socket: " + e.getMessage());
            }
        }
    }
    
    /**
     * UDP server loop for client lookups
     */
    private void runUdpServer() {
        byte[] buffer = new byte[ProtocolConstants.MAX_MESSAGE_SIZE];
        
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpServer.receive(packet);
                
                threadPool.submit(() -> handleUdpClient(packet));
                
            } catch (IOException e) {
                if (running) {
                    System.err.println("UDP Server error: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Handle UDP client request (client lookup)
     */
    private void handleUdpClient(DatagramPacket packet) {
        try {
            String requestMessage = new String(packet.getData(), 0, packet.getLength());
            System.out.println("UDP Request from " + packet.getAddress() + ":" + packet.getPort() + ": " + requestMessage);
            
            String response = messageHandler.handleLookupMessage(requestMessage);
            
            byte[] responseBytes = response.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(
                responseBytes,
                responseBytes.length,
                packet.getAddress(),
                packet.getPort()
            );
            
            udpServer.send(responsePacket);
            System.out.println("UDP Response: " + response);
            
        } catch (IOException e) {
            System.err.println("Error handling UDP client: " + e.getMessage());
        }
    }
    
    /**
     * Get server registry for testing
     */
    public ServerRegistry getRegistry() {
        return registry;
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        DirectoryServer server = new DirectoryServer();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        
        try {
            server.start();
            
            // Keep the main thread alive
            while (true) {
                Thread.sleep(1000);
                if (!server.running) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start Directory Server: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Directory Server interrupted");
            Thread.currentThread().interrupt();
        } finally {
            server.stop();
        }
    }
}