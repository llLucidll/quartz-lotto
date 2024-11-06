package com.example.myapplication;

//for managing notification permissions.

import android.app.Activity;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class NotificationService {

    /*
    Example Usage:
    String title = "Opted In";
    String description = "You have opted in to receive notifications.";
    notificationService.sendNotification(user, context, title, description);
     */



    private static final int BASE_NOTIFICATION_ID = 100;

    //Send Notification method
    public static void sendNotification(UserProfile user, Context context, String title, String description) {
        if (!user.isReceivingNotifications()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
                return; // handle the notification after the user grants permission
            }
        }

        String channelId = NotificationUtils.getChannelId();

        Intent intent = new Intent(context, MainActivity.class); //TODO Change MainActivity to Homepage if exists
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //creating a new activity when notif is clicked

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_background) // TODO Replace with our app icon?
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Send the notification
        int notificationID = (int) System.currentTimeMillis(); //Assures unique notifcation id.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationID, builder.build());

    }

    //Notification Permissions
    public static void optOutOfNotifications(UserProfile user) {
        user.setReceiveNotifications(false);
    }

    public static void optInToNotifications(Context context, UserProfile user) {
        user.setReceiveNotifications(true);

        if (user.isChosenFromWaitingList()) {
            sendNotification(user, context, "Success", "You have opted into notifications");
        }
    }

    //For notification preference when chosen from waiting list
    public void updateChosenFromWaitingListPreference(UserProfile user, boolean preference) {
        user.setNotifyChosenFromWaitingList(preference);
    }

    //For notification preference when not chosen from waiting list
    public void updateNotChosenFromWaitingListPreference(UserProfile user, boolean preference) {
        user.setNotifyNotChosenFromWaitingList(preference);
    }

}

