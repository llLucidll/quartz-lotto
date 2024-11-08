package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

/**
 * Utility class to handle creation and management of notification channels.
 * This is required for notifications on Android 8.0 (Oreo) and higher.
 */
public class NotificationUtils {
    private static final String CHANNEL_ID = "default_channel_id";

    /**
     * Retrieves the default notification channel ID.
     *
     * @return The channel ID for the default notification channel.
     */
    public static String getChannelId() {
        return CHANNEL_ID;
    }

    /**
     * Creates a notification channel for the application.
     * This is required for displaying notifications on Android 8.0 (Oreo) and higher.
     *
     * @param context The context used to create the notification channel.
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Your Channel Name";
            String description = "Your Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
