package com.chatapp.models;

import java.io.Serializable;

public class UserDataModel implements Serializable {
    public String name;
    public String about;
    public String url;
    public String uid;
    public String token;

    public UserDataModel() {
    }

    public UserDataModel(String name, String about, String url, String uid) {
        this.name = name;
        this.about = about;
        this.url = url;
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
