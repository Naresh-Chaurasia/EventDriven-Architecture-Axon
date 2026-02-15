package com.appsdeveloperblog.photoapp.api.users.model;

public class JwtRequest {
    private String username;
    private String userId;

    public JwtRequest() {}

    public JwtRequest(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
