package com.beautyparlour.dto.response;

public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private String userType;
    private String name;
    private String email;

    // Constructors
    public LoginResponse() {}

    public LoginResponse(String token, String userType, String name, String email) {
        this.token = token;
        this.userType = userType;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
