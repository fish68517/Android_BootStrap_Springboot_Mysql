package com.gameplatform.dto.request;

/**
 * Request DTO for sending verification code
 * Requirements: 1.1, 1.3, 2.1, 2.2
 */
public class SendCodeRequest {
    private String phoneNumber;
    private String type; // "login" or "register"
    
    public SendCodeRequest() {}
    
    public SendCodeRequest(String phoneNumber, String type) {
        this.phoneNumber = phoneNumber;
        this.type = type;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}