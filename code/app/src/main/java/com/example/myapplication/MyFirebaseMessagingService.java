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

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private FirebaseFirestore firestore;

    @Override
    public void onCreate() {
        super.onCreate();
        firestore = FirebaseFirestore.getInstance();
    }

    // This method fetches users based on the groupType and a listener
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




    // Define the listener interface to handle the result
    public interface OnUsersFetchedListener {
        void onUsersFetched(List<UserProfile> users);
        void onFailure(Exception e);
    }


    private void sendNotificationToUser(UserProfile user, String title, String message) {
        if (user.isReceivingNotifications()) {
            // Send the notification using NotificationService
            NotificationService.sendNotification(user, this, title, message);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        // Handle token refresh (send to your server if needed)
    }

}