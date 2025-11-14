package com.gameplatform.dto.request;

/**
 * Request DTO for user registration
 * Requirements: 1.2, 1.4, 2.2, 2.3
 */
public class RegisterRequest {
    private String phoneNumber;
    private String verificationCode;
    private String nickname;
    
    public RegisterRequest() {}
    
    public RegisterRequest(String phoneNumber, String verificationCode, String nickname) {
        this.phoneNumber = phoneNumber;
        this.verificationCode = verificationCode;
        this.nickname = nickname;
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
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", verificationCode='" + verificationCode + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}