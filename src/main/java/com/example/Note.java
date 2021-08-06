package com.example;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.time.Duration;

// public class Note implements NoteDetails {
public class Note {
    private long id;
    private String videoId;
    private String title;
    private String content;
    private LocalDate dateCreated;
    private long ownerId;
    private long[] sharedIds;
    private boolean isShared;

    // Getters
    public long getId() {
        return this.id;
    }

    public String getVideoId() {
        return this.videoId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public LocalDate getDateCreated() {
        return this.dateCreated;
    }

    public long getOwnerId() {
        return this.ownerId;
    }

    public long[] getSharedIds() {
        return this.sharedIds;
    }

    public boolean getIsShared() {
        return this.isShared;
    }

    // Setters
    public void setId(long newId) {
        this.id = newId;
    }

    public void setVideoId(String newVideoId) {
        this.videoId = newVideoId;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public void setContent(String newContent) {
        this.content = newContent;
    }

    public void setDateCreated(LocalDate newDateCreated) {
        this.dateCreated = newDateCreated;
    }

    public void setOwnerId(long newOwnerId) {
        this.ownerId = newOwnerId;
    }

    public void setSharedId(long[] newSharedIds) {
        this.sharedIds = newSharedIds;
    }

    public void setisShared(boolean newIsShared) {
        this.isShared = newIsShared;
    }

    // Iteration 3 TO-DO: Methods to insert (share) or remove (unshare) a single or
    // multiple shared id.
}