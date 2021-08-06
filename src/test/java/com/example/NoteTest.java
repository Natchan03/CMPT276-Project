package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

class NoteTest {
    static Note n;

    @BeforeAll
    static void setUp(){
        n = new Note();
        n.setVideoId("video1");
        n.setTitle("Chapter 1");
        n.setOwnerId(1235556688);
        n.setisShared(true);
        n.setisEditable(false);
    }

    @Test
    public void noteVideoId(){
        assertEquals("video1",n.getVideoId());
        n.setVideoId("2333 5555");
        assertEquals("2333 5555",n.getVideoId());
    }

    @Test
    public void noteTitle(){
        assertEquals("Chapter 1", n.getTitle());
        n.setTitle("2nd Lecture - CMPT256");
        assertEquals("2nd Lecture - CMPT256",n.getTitle());
    }

    @Test
    public void noteOwnerId(){
        assertEquals(1235556688,n.getOwnerId());
        n.setOwnerId(-255555555);
        assertEquals(-255555555,n.getOwnerId());
    }

    @Test
    public void noteisShared(){
         assertEquals(true,n.getIsShared());
        n.setisShared(false);
        assertEquals(false,n.getIsShared());
    }

    @Test
    public void noteisEditable(){
         assertEquals(false,n.getIsEditable());
        n.setisEditable(true);
        assertEquals(true,n.getIsEditable());
    }
}