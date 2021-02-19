package com.monir.journalappwithfirebaseandfirestore.model;

import com.google.firebase.Timestamp;

public class Journal {
    private String title;
    private String though;
    private String imageUrl;
    private String userId;
    protected String userName;
    private Timestamp timeAdded;

    public Journal() {
        // this is must need for firebase
    }

    public Journal(String title, String though, String imageUrl, String userId, String userName, Timestamp timeAdded) {
        this.title = title;
        this.though = though;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.userName = userName;
        this.timeAdded = timeAdded;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThough() {
        return though;
    }

    public void setThough(String though) {
        this.though = though;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }
}
