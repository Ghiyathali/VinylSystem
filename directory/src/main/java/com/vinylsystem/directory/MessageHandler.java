package com.vinylsystem.directory;

import com.vinylsystem.common.*;

/**
 * Handles JSON message parsing and response generation
 */
public class MessageHandler {
    private final ServerRegistry registry;
    
    public MessageHandler(ServerRegistry registry) {
        this.registry = registry;
    }
    
    /**
     * Handle TCP registration message from vinyl server
     */
    public String handleRegistrationMessage(String jsonMessage) {
        try {
            RegistrationMessage message = JsonUtils.parseRegistrationMessage(jsonMessage);
            
            // Validate message
            if (message == null) {
                return createErrorResponse(StatusCodes.INVALID_REQUEST, "Invalid JSON message");
            }
            
            if (message.getMessageType() == null || 
                (!ProtocolConstants.MSG_REGISTER.equals(message.getMessageType()) && 
                 !ProtocolConstants.MSG_UPDATE.equals(message.getMessageType()))) {
                return createErrorResponse(StatusCodes.INVALID_REQUEST, "Invalid message type");
            }
            
            // Validate server name format
            if (!ValidationUtils.isValidServerName(message.getServerName())) {
                return createErrorResponse(StatusCodes.INVALID_NAME_FORMAT, 
                    "Invalid server name format. Expected: <string>.group#.pro2[x|y]");
            }
            
            // Validate IP address
            if (!ValidationUtils.isValidIPAddress(message.getServerIP())) {
                return createErrorResponse(StatusCodes.INVALID_IP_FORMAT, "Invalid IP address format");
            }
            
            // Validate port
            if (!ValidationUtils.isValidPort(message.getServerPort())) {
                return createErrorResponse(StatusCodes.INVALID_REQUEST, "Invalid port number");
            }
            
            // Validate TTL
            if (!ValidationUtils.isValidTTL(message.getTtl())) {
                return createErrorResponse(StatusCodes.INVALID_REQUEST, "Invalid TTL value");
            }
            
            // Register the server
            registry.registerServer(
                message.getServerName(),
                message.getServerIP(),
                message.getServerPort(),
                message.getTtl()
            );
            
            return createSuccessResponse(message.getMessageType() + " successful");
            
        } catch (Exception e) {
            return createErrorResponse(StatusCodes.INVALID_REQUEST, "Invalid JSON format: " + e.getMessage());
        }
    }
    
    /**
     * Handle UDP lookup message from client
     */
    public String handleLookupMessage(String jsonMessage) {
        try {
            LookupMessage message = JsonUtils.parseLookupMessage(jsonMessage);
            
            // Validate message
            if (message == null) {
                return createErrorResponse(StatusCodes.INVALID_REQUEST, "Invalid JSON message");
            }
            
            if (!ProtocolConstants.MSG_LOOKUP.equals(message.getMessageType())) {
                return createErrorResponse(StatusCodes.INVALID_REQUEST, "Invalid message type");
            }
            
            // Validate server name format
            if (!ValidationUtils.isValidServerName(message.getServerName())) {
                return createErrorResponse(StatusCodes.INVALID_NAME_FORMAT, 
                    "Invalid server name format. Expected: <string>.group#.pro2[x|y]");
            }
            
            // Look up the server
            ServerRegistry.ServerRecord record = registry.lookupServer(message.getServerName());
            
            if (record == null) {
                return createErrorResponse(StatusCodes.NOT_FOUND, "Server not found or expired");
            }
            
            return createLookupResponse(record);
            
        } catch (Exception e) {
            return createErrorResponse(StatusCodes.INVALID_REQUEST, "Invalid JSON format: " + e.getMessage());
        }
    }
    
    /**
     * Create success response
     */
    private String createSuccessResponse(String message) {
        ResponseMessage response = new ResponseMessage(StatusCodes.SUCCESS, message);
        return JsonUtils.toJson(response);
    }
    
    /**
     * Create error response
     */
    private String createErrorResponse(String statusCode, String message) {
        ResponseMessage response = new ResponseMessage(statusCode, message);
        return JsonUtils.toJson(response);
    }
    
    /**
     * Create lookup response with server information
     */
    private String createLookupResponse(ServerRegistry.ServerRecord record) {
        ResponseMessage response = new ResponseMessage(
            StatusCodes.SUCCESS,
            "Server found",
            record.getIp(),
            record.getPort()
        );
        return JsonUtils.toJson(response);
    }
}