package com.chatapp.models;

import java.io.Serializable;
import java.util.Date;

public class ChatMessageModel implements Serializable {
    public String senderId;
    public String receiverId;
    public String messageId;
    public String dateTime;
    public Date dateObject;
    public String conversionId, conversionName, conversionImage;
}
