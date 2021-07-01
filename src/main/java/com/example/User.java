package com.example;

public class User {
    private long id;
    private String fname;
    private String lname;
    //private String username;
    private String email;
    private String password;
    private String type;

    // Getters
    public long getId() {
        return this.id;
    }
    public String getFname()
    {
        return this.fname;
    }
    public String getLname()
    {
        return this.lname;
    }
    //username getter
    // public String getUsername()
    // {
    //     return this.username;
    // }
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
    public void setFname(String newFname){
        this.fname=newFname;
    }
    public void setLname(String newLname){
        this.lname=newLname;
    }
    //username setter
    // public void setUsername(String newUsername){
    //     this.username=newUsername;
    // }
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