package com.luxuryproductsholding.api.dto;

public class LoginDTO {
    public String email;
    public String password;

    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

