package com.chatapp.help.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.chatapp.R;
import com.chatapp.activities.ChatsActivity;
import com.chatapp.activities.UsersActivity;
import com.chatapp.help.Constant;
import com.chatapp.models.UserDataModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MessageService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        UserDataModel dataModel = new UserDataModel();
        dataModel.uid = message.getData().get(Constant.USER_ID);
        dataModel.name = message.getData().get(Constant.KEY_PROFILE_NAME);
        dataModel.token = message.getData().get(Constant.USER_TOKEN);

        int notificationId = new Random().nextInt();

        String channelId = "chat-message";
        Intent intent = new Intent(this, ChatsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constant.USER, dataModel);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.ic_notifications);
        builder.setContentTitle(dataModel.name);
        builder.setContentText(message.getData().get("message"));
        builder.setStyle(
                new NotificationCompat.BigTextStyle().bigText(
                        message.getData().get("message")
                ));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence channelName = "Chat message";
            String channelDescription = "Notification channel is used for chat message notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName,importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId, builder.build());
    }
}
