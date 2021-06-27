package com.example;

public class User {
    private long id;
    private String email;
    private String password;
    private String type;

    // Getters
    public long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getType() {
        return this.type;
    }

    // Setters
    public void setId(long newId) {
        this.id = newId;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public void setType(String newType) {
        this.type = newType;
    }
}