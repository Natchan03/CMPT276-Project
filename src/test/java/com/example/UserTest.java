package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    static User u;

    @BeforeAll
    static void setUp(){
        u = new User();
        u.setFname("jack");
        u.setLname("smith");
        u.setEmail("jacksmith@gmail.com");
        u.setPassword("Abc123");
        u.setType("regular");
    }

    @Test
    public void userFname(){
        assertEquals("jack",u.getFname());
        u.setFname("steve");
        assertEquals("steve",u.getFname());
    }

    @Test
    public void userLname(){
        assertEquals("smith",u.getLname());
        u.setLname("alice");
        assertEquals("alice",u.getLname());
    }

    @Test
    public void userEmail(){
        assertEquals("jacksmith@gmail.com",u.getEmail());
        u.setEmail("stevealice@gmail.com");
        assertEquals("stevealice@gmail.com",u.getEmail());
    }

    @Test
    public void userPassword(){
        assertEquals("Abc123",u.getPassword());
        u.setPassword("Xyz456");
        assertEquals("Xyz456",u.getPassword());
    }

    @Test
    public void userType(){
        assertEquals("regular",u.getType());
        u.setType("admin");
        assertEquals("admin",u.getType());
    }

}