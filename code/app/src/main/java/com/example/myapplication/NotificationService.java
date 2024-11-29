package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Map;

/**
 * Service for sending notifications to users.
 */
public class NotificationService {

    private static final String TAG = "NotificationService";

    /**
     * Sends a notification to a user.
     *
     * @param user        A map containing user data, including "userId".
     * @param context     The context from which this method is called.
     * @param title       The title of the notification.
     * @param message     The content text of the notification.
     */
    public static void sendNotification(Map<String, Object> user, Context context, String title, String message) {
        if (user == null || !user.containsKey("userId")) {
            return;
        }

        String userId = (String) user.get("userId");

        // Intent to open the app when notification is clicked
        Intent intent = new Intent(context, MainActivity.class); // Replace with your desired activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationUtils.getChannelId())
                .setSmallIcon(R.drawable.ic_notif) // Replace with your app's notification icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Unique notification ID
        int notificationId = (int) System.currentTimeMillis();

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }
}
