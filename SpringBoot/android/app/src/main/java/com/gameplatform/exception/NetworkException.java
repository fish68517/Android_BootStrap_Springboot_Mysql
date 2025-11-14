package com.gameplatform.exception;

/**
 * Exception for network-related errors
 * Requirements: 1.3, 2.2, 3.2, 4.1, 5.1
 */
public class NetworkException extends Exception {
    
    private int errorCode;
    private String errorMessage;
    
    public NetworkException(String message) {
        super(message);
        this.errorMessage = message;
    }
    
    public NetworkException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }
    
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
        this.errorMessage = message;
    }
    
    public NetworkException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Check if this is a connection error
     * @return true if connection error, false otherwise
     */
    public boolean isConnectionError() {
        return getCause() instanceof java.net.ConnectException ||
               getCause() instanceof java.net.SocketTimeoutException ||
               getCause() instanceof java.net.UnknownHostException;
    }
    
    /**
     * Check if this is a timeout error
     * @return true if timeout error, false otherwise
     */
    public boolean isTimeoutError() {
        return getCause() instanceof java.net.SocketTimeoutException;
    }
    
    /**
     * Check if this is a server error (5xx)
     * @return true if server error, false otherwise
     */
    public boolean isServerError() {
        return errorCode >= 500 && errorCode < 600;
    }
    
    /**
     * Check if this is a client error (4xx)
     * @return true if client error, false otherwise
     */
    public boolean isClientError() {
        return errorCode >= 400 && errorCode < 500;
    }
    
    @Override
    public String toString() {
        return "NetworkException{" +
                "errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", cause=" + getCause() +
                '}';
    }
}