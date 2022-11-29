package com.chatapp.help;

import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;

public class Constant {
    public static final String TAG_MAIN = "main_activity";
    public static final String TAG_CREATE_PROFILE = "create_profile_activity";
    public static final String TAG_CHAT = "chats_activity";
    public static final String TAG_SIGNUP = "signUp_activity";
    public static final String TAG_USERS = "users_activity";
    public static final String TAG_VERIFY_USER = "verifyOTP_activity";
    public static String phoneNumber = "phone";
    public static String empty = "Empty";
    public static String permission_verified = "Permission Granted";
    public static String permission_unverified = "Permission Denied";
    public static String KEY_PROFILE_NAME = "name";
    public static String user_name = "username";
    public static String KEY_ABOUT = "about";
    public static String PROFILE_IMAGE = "image";
    public static String USER_ID = "uid";
    public static String USER_DIR = "users";
    public static String USER = "user";
    public static String USER_TOKEN = "token";
    public static String KEY_SENDER_ID = "senderId";
    public static String KEY_SENDER_NAME = "senderName";
    public static String KEY_SENDER_IMAGE_URL = "senderImage";
    public static String KEY_RECEIVER_ID = "receiverId";
    public static String KEY_RECEIVER_NAME = "receiverName";
    public static String KEY_RECEIVER_IMAGE_URL = "receiverImage";
    public static String KEY_CONVERSIONS = "conversions";
    public static String KEY_AVAILABILITY = "availability";
    public static String KEY_MESSAGE = "message";
    public static String KEY_CHATS_DIR = "chats";
    public static String KEY_TIMESTAMP = "timestamp";
    public static String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static String REMOTE_MSG_DATA = "data";
    public static String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";


    public static HashMap<String, String> remoteMsgHeader = null;

    public static HashMap<String, String> getRemoteMsgHeaders(){
        if (remoteMsgHeader == null){
            remoteMsgHeader = new HashMap<>();
            remoteMsgHeader.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAkuwdz50:APA91bHWbn9f8f1_cKV1VNj9qRBUdu2PURJwEQglS0xzmPsNuFdIMFKrgLBdJjDisOmnqOW-9QkCfbzaG9ruh-nCZpBJyiwLB6BFg6nkRBrDjTQOPqOku99PrJ7QeLJe1-d5Wt5tayrL"
            );
            remoteMsgHeader.put(REMOTE_MSG_CONTENT_TYPE,
                    "application/json");
        }
        return remoteMsgHeader;
    }

    public static void setMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
