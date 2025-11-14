package com.gameplatform.network;

/**
 * Network configuration constants
 * Requirements: 1.1, 2.1, 3.2, 4.1, 5.1
 */
public class NetworkConfig {
    
    // Base URL for API endpoints
    public static final String BASE_URL = "https://api.gameplatform.com/";
    
    // Timeout configurations (in seconds)
    public static final int CONNECT_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 30;
    public static final int WRITE_TIMEOUT = 30;
    
    // Request headers
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_USER_AGENT = "User-Agent";
    
    // Content types
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    
    // Response codes
    public static final int SUCCESS_CODE = 200;
    public static final int CREATED_CODE = 201;
    public static final int BAD_REQUEST_CODE = 400;
    public static final int UNAUTHORIZED_CODE = 401;
    public static final int FORBIDDEN_CODE = 403;
    public static final int NOT_FOUND_CODE = 404;
    public static final int SERVER_ERROR_CODE = 500;
    
    // Cache settings
    public static final int CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final int CACHE_MAX_AGE = 60; // 1 minute
    public static final int CACHE_MAX_STALE = 60 * 60 * 24; // 1 day
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int FIRST_PAGE = 1;
}