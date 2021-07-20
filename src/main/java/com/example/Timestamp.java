package com.example;

import java.util.Collection;
import java.time.Duration;

public class Timestamp {
    private long noteId;
    private long seconds;  // Should convert to day:h:min:sec format (may not need to show all) when displayed
    private String content;  // Actual note content associated with each timestamp
    

    // Getters
    public long getNoteId() {
        return this.noteId;
    }

    public long getSeconds() {
        return this.seconds;
    }

    public String getContent() {
        return this.content;
    }

    // Setters
    public void setNoteId(long newNoteId) {
        this.noteId = newNoteId;
    }

    public void setSeconds(long newSeconds) {
        this.seconds = newSeconds;
    }

    public void setContent(String newContent) {
        this.content = newContent;
    }
}