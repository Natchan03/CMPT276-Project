package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

class TimestampTest {
    static Timestamp t;

    @BeforeAll
    static void setUp(){
        t = new Timestamp();
        t.setNoteId(1235556688);
        t.setContent("This is my first note");
    }

    @Test
    public void TimestampNoteId(){
        assertEquals(1235556688, t.getNoteId());
        t.setNoteId(00555333);
        assertEquals(00555333,t.getNoteId());
    }

    @Test
    public void TimestampSetContent(){
        assertEquals("This is my first note", t.getContent());
        t.setContent("Chapter Part 1: example");
        assertEquals("Chapter Part 1: example",t.getContent());
    }

}
