package com.quanhm.ecommerce.be.response;

public class AuthResponse {
    private String jwt;
    private String message;
    private String role;

    public AuthResponse() {
    }

    public AuthResponse(String jwt, String message) {
        super();
        this.jwt = jwt;
        this.message = message;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getMessage() {
        return message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
