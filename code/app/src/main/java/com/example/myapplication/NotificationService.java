package com.example.myapplication;

import android.app.Activity;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code NotificationService} class handles sending notifications to users and saving
 * notification data to Firebase Firestore.
 */
public class NotificationService {

    private static final String TAG = "NotificationService";

    /**
     * Sends a notification to the specified user and saves the notification data to Firestore.
     *
     * @param user        A map containing user data, including "userId", "name", and "notificationsEnabled".
     * @param context     The context from which this method is called.
     * @param title       The title of the notification.
     * @param description The content text of the notification.
     */
    public static void sendNotification(Map<String, Object> user, Context context, String title, String description) {
        if (user == null || !Boolean.TRUE.equals(user.get("notificationsEnabled"))) {
            Log.d(TAG, "Notifications are disabled for this user.");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (context instanceof Activity) {
                    ActivityCompat.requestPermissions(
                            (Activity) context,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            1001
                    );
                }
                Log.d(TAG, "Notification permission not granted. Requesting permission.");
                return;
            }
        }

        String channelId = NotificationUtils.getChannelId();
        Intent intent = new Intent(context, MainActivity.class); // Adjust MainActivity if needed
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        //Build the structure of the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your app icon
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        //Generate a unique notification ID
        int notificationID = (int) System.currentTimeMillis();
        //Send the actual Notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationID, builder.build());

        // Save the notification to Firestore
        saveNotificationToFirestore(user, title, description, context);
    }

    /**
     * Saves the notification data to Firebase Firestore under the "notifications" collection.
     *
     * @param user        A map containing user data, including "userId" and "name".
     * @param title       The title of the notification.
     * @param description The content text of the notification.
     * @param context     The context from which this method is called.
     */
    private static void saveNotificationToFirestore(Map<String, Object> user, String title, String description, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = (String) user.get("userId");
        String userName = (String) user.get("name");

        Log.d(TAG, "User data received: " + user);

        // Prepare data to save in Firestore
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("description", description);
        notificationData.put("timestamp", FieldValue.serverTimestamp());
        notificationData.put("userName", userName != null ? userName : "Unknown User");

        Log.d(TAG, "Saving notification to Firestore: " + notificationData);

        // Save the notification under the top-level 'notifications' collection
        db.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(docRef -> {
                    Log.d(TAG, "Notification saved successfully with ID: " + docRef.getId());
                    // Show Toast on success on the main thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "Notification saved successfully for " + userName, Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving notification for user: " + userId, e);
                    // Show Toast on failure on the main thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "Error saving notification for " + userName, Toast.LENGTH_SHORT).show();
                    });
                });
    }
}
