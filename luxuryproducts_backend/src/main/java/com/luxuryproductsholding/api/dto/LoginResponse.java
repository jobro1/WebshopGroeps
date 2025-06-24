package com.luxuryproductsholding.api.dto;

public class LoginResponse {
    public Long userId;
    public String email;
    public String token;
    public String role;

    public LoginResponse(Long userId, String email, String token,  String role) {
        this.userId = userId;
        this.email = email;
        this.token = token;
        this.role = role;
    }
}
