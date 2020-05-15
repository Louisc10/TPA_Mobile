package com.example.readup.model;

public class Member {
    private String email;
    private String password;
    private String name;
    private String googleId;

    public Member(String email, String password, String name, String googleId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.googleId = googleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
}
