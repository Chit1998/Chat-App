package com.chatapp.help;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferences {

    private SharedPreferences preferences;
    private Context context;
    private SharedPreferences.Editor editor;

    public static final String KEY_PROFILE_NAME = "countryCode";
    public static final String KEY_ABOUT = "about";
    public static final String PROFILE_IMAGE = "image";
    public static final String USER_ID = "uid";
    public static final String USER_TOKEN = "token";

    public Preferences(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }


    public void setData(String profile_name, String about, String profileImage,String uid){
        editor.putString(KEY_PROFILE_NAME,profile_name);
        editor.putString(KEY_ABOUT,about);
        editor.putString(PROFILE_IMAGE,profileImage);
        editor.putString(USER_ID,uid);
        editor.commit();
    }

    public void setToken(String token){
        editor.putString(USER_TOKEN,token);
        editor.commit();
    }

    public HashMap<String, String> getLogInDetails(){
        HashMap<String, String> userData = new HashMap<String, String>();
        userData.put(KEY_PROFILE_NAME,preferences.getString(KEY_PROFILE_NAME,null));
        userData.put(KEY_ABOUT,preferences.getString(KEY_ABOUT,null));
        userData.put(PROFILE_IMAGE,preferences.getString(PROFILE_IMAGE,null));
        userData.put(USER_ID,preferences.getString(USER_ID,null));
        userData.put(USER_TOKEN,preferences.getString(USER_TOKEN,null));
        return userData;
    }

    public void dataClear(){
        editor.clear();
        editor.commit();
    }


}
