package com.cs.domain.auth;

public class AuthWebRequest {

    private String username;
    private String password;

    public AuthWebRequest() {
    }

    public AuthWebRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
