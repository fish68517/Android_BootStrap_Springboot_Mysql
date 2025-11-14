package com.gameplatform.dto.request;

/**
 * Request DTO for user login
 * Requirements: 1.2, 2.2, 2.3
 */
public class LoginRequest {
    private String phoneNumber;
    private String verificationCode;
    
    public LoginRequest() {}
    
    public LoginRequest(String phoneNumber, String verificationCode) {
        this.phoneNumber = phoneNumber;
        this.verificationCode = verificationCode;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getVerificationCode() {
        return verificationCode;
    }
    
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
    
    @Override
    public String toString() {
        return "LoginRequest{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", verificationCode='" + verificationCode + '\'' +
                '}';
    }
}