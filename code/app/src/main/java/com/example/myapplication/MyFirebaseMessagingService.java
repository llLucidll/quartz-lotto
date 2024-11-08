package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MyFirebaseMessagingService is a Firebase Messaging Service that handles incoming messages,
 * manages Firebase Firestore operations, and sends notifications to users.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private FirebaseFirestore firestore;

    /**
     * Initializes the Firestore instance when the service is created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Fetches users from Firestore based on the specified group type and whether they have
     * notifications enabled.
     *
     * @param groupType The group type to filter users by.
     * @param listener An instance of OnUsersFetchedListener to handle the result of the fetch.
     */
    public void fetchUsersWithNotificationsEnabled(String groupType, OnUsersFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch the users who match the criteria
        db.collection("users")
                .whereEqualTo("groupType", groupType)  // Filter by groupType
                .whereEqualTo("receiveNotifications", true)  // Ensure they want notifications
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserProfile> users = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        UserProfile user = documentSnapshot.toObject(UserProfile.class);
                        if (user != null) {
                            users.add(user);
                        }
                    }
                    listener.onUsersFetched(users);  // Call the listener with the fetched users
                })
                .addOnFailureListener(listener::onFailure);  // Pass the failure to the listener
    }

    /**
     * Interface to handle the result of fetching users with notifications enabled.
     */
    public interface OnUsersFetchedListener {
        /**
         * Called when the users are successfully fetched from Firestore.
         *
         * @param users List of users who match the specified criteria.
         */
        void onUsersFetched(List<UserProfile> users);

        /**
         * Called if an error occurs during the user fetch.
         *
         * @param e Exception detailing the error.
         */
        void onFailure(Exception e);
    }

    /**
     * Sends a notification to a specified user using the NotificationService.
     *
     * @param user The user to whom the notification should be sent.
     * @param title The title of the notification.
     * @param message The message content of the notification.
     */
    private void sendNotificationToUser(Map<String, Object> user, String title, String message) {
        if (user != null || Boolean.TRUE.equals(user.get("notificationsEnabled"))) {
            // Send the notification using NotificationService
            NotificationService.sendNotification(user, this, title, message);
        }
    }

    /**
     * Called when a new token for the device is generated. This can be used to send
     * the token to your server for user identification and push notifications.
     *
     * @param token The new FCM token for the device.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        // Handle token refresh (send to your server if needed)
    }

}
