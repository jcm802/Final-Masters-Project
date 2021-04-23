package com.learningjavaandroid.homesearch.util;

import android.app.Application;

// ====== SINGLETON ==== //
// This is extending Application so it is hidden for use by FireBase auth
// Carries the username and password of logged in user around in isolation
public class UserAPI extends Application {

    private String username;
    private String userId;

    private static UserAPI instance;
    public static UserAPI getInstance(){
        if(instance == null) instance = new UserAPI();
        return instance;
    }

    public UserAPI(){}

    public UserAPI(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }

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
