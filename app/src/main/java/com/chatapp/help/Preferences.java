package com.chatapp.help;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferences {

    private SharedPreferences preferences;
    private Context context;
    private SharedPreferences.Editor editor;


    public Preferences(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }


    public void setData(String profile_name, String about, String profileImage,String uid){
        editor.putString(Constant.KEY_PROFILE_NAME,profile_name);
        editor.putString(Constant.KEY_ABOUT,about);
        editor.putString(Constant.PROFILE_IMAGE,profileImage);
        editor.putString(Constant.USER_ID,uid);
        editor.commit();
    }

    public void setToken(String token){
        editor.putString(Constant.USER_TOKEN,token);
        editor.commit();
    }

    public HashMap<String, String> getLogInDetails(){
        HashMap<String, String> userData = new HashMap<String, String>();
        userData.put(Constant.KEY_PROFILE_NAME,preferences.getString(Constant.KEY_PROFILE_NAME,null));
        userData.put(Constant.KEY_ABOUT,preferences.getString(Constant.KEY_ABOUT,null));
        userData.put(Constant.PROFILE_IMAGE,preferences.getString(Constant.PROFILE_IMAGE,null));
        userData.put(Constant.USER_ID,preferences.getString(Constant.USER_ID,null));
        userData.put(Constant.USER_TOKEN,preferences.getString(Constant.USER_TOKEN,null));
        return userData;
    }

    public void dataClear(){
        editor.clear();
        editor.commit();
    }


}
