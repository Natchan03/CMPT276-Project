package com.example;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

public class User implements UserDetails {
    private long id;
    private String fname;
    private String lname;
    private String email;
    private String password;
    private String type;
    private String resetPassword;

    // Getters
    public long getId() {
        return this.id;
    }

    public String getFname() {
        return this.fname;
    }

    public String getLname() {
        return this.lname;
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

    public String resetPasswordToken() {
        return this.resetPassword;
    }

    // Setters
    public void setId(long newId) {
        this.id = newId;
    }

    public void setFname(String newFname) {
        this.fname = newFname;
    }

    public void setLname(String newLname) {
        this.lname = newLname;
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

    //
    // Below methods are overrides for implementing UserDetails
    //

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        java.util.List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList("ROLE_USER");

        if (this.type.equals("admin")) {
            roles.addAll(AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        }

        return roles;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}