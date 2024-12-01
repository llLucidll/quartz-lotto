package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Service for sending notifications to users.
 */
public class NotificationService {

    private static final String TAG = "NotificationService";

    /**
     * Sends a notification to a user and saves the notification in Firebase.
     *
     * @param user        A map containing user data, including "userId".
     * @param context     The context from which this method is called.
     * @param title       The title of the notification.
     * @param message     The content text of the notification.
     */
    public static void sendNotification(Map<String, Object> user, Context context, String title, String message) {
        if (user == null || !user.containsKey("userId")) {
            Log.e(TAG, "User data is invalid or missing 'userId'");
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

        // Save the notification in Firebase
        saveNotificationToFirebase(userId, title, message);
    }

    /**
     * Fetches and displays all notifications for the current device ID.
     *
     * @param deviceId The device ID of the current user.
     * @param context  The context to send notifications.
     */
    public static void receiveNotifications(String deviceId, Context context) {
        if (deviceId == null || deviceId.isEmpty()) {
            Log.e(TAG, "Device ID is null or empty");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notifications")
                .whereEqualTo("userId", deviceId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String title = document.getString("title");
                        String message = document.getString("message");

                        if (title != null && message != null) {
                            Map<String, Object> user = new HashMap<>();
                            user.put("userId", deviceId);
                            sendNotificationWithoutSaving(user, context, title, message);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching notifications: ", e));
    }

    /**
     * Sends a notification to a user without saving it in Firebase.
     *
     * @param user    A map containing user data, including "userId".
     * @param context The context from which this method is called.
     * @param title   The title of the notification.
     * @param message The content text of the notification.
     */
    public static void sendNotificationWithoutSaving(Map<String, Object> user, Context context, String title, String message) {
        if (user == null || !user.containsKey("userId")) {
            Log.e(TAG, "User data is invalid or missing 'userId'");
            return;
        }

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


    /**
     * Saves the notification details to Firebase Firestore.
     *
     * @param userId   The ID of the user who will receive the notification.
     * @param title    The title of the notification.
     * @param message  The content text of the notification.
     */
    private static void saveNotificationToFirebase(String userId, String title, String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = formatter.format(currentDate);

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", userId);
        notificationData.put("title", title);
        notificationData.put("message", message);
        notificationData.put("timestamp", formattedDate);

        db.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Notification saved with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving notification", e));
    }
}
