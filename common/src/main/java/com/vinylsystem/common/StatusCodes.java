package com.vinylsystem.common;

/**
 * Status codes for the vinyl system protocol
 */
public class StatusCodes {
    public static final String SUCCESS = "000001";
    public static final String ERROR = "000002";
    public static final String INVALID_REQUEST = "000003";
    public static final String SERVER_ERROR = "000004";
    public static final String NOT_FOUND = "000100";
    
    // Additional status codes for various scenarios
    public static final String INVALID_NAME_FORMAT = "000004";
    public static final String INVALID_IP_FORMAT = "000005";
    public static final String TTL_EXPIRED = "000006";
    public static final String DUPLICATE_REGISTRATION = "000007";
}