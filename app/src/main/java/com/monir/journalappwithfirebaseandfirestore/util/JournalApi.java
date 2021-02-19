package com.monir.journalappwithfirebaseandfirestore.util;

import android.app.Application;

public class JournalApi extends Application {
    private String username;
    private String userId;

    private static JournalApi instance;

    // This is our apps global api
    // No enable this we have to declare it AndroidManifest file...in application section
    public static JournalApi getInstance(){
        if(instance == null)
            instance = new JournalApi();
        return instance;
    }

    public JournalApi (){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
