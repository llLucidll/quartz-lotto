package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupEntrantsActivity extends AppCompatActivity {

    private TextView textViewEntrants;
    private Button buttonSendNotification;
    private String groupType;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_entrants);

        // Ensure the notification channel is created
        NotificationUtils.createNotificationChannel(this);

        groupType = getIntent().getStringExtra("groupType");

        textViewEntrants = findViewById(R.id.textViewEntrants);
        buttonSendNotification = findViewById(R.id.buttonSendNotification);

        loadEntrantsList(groupType);

        buttonSendNotification.setOnClickListener(v -> fetchUserProfileAndSendNotification());
    }

    private void loadEntrantsList(String groupType) {
        if (groupType.equals("waiting")) {
            textViewEntrants.setText("Waiting List Entrants:\n- Entrant 1\n- Entrant 2");
        } else if (groupType.equals("selected")) {
            textViewEntrants.setText("Selected Entrants:\n- Entrant 3\n- Entrant 4");
        } else if (groupType.equals("cancelled")) {
            textViewEntrants.setText("Cancelled Entrants:\n- Entrant 5\n- Entrant 6");
        }
    }

    /*
    Retrieving User Profile Maps from the database.
     */
    private void fetchUserProfileAndSendNotification() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userProfileRef = db.collection("users").document(userId);

        userProfileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve user profile data from Firestore
                        Map<String, Object> userProfile = document.getData();

                        if (userProfile != null && Boolean.TRUE.equals(userProfile.get("notificationsEnabled"))) {
                            sendNotificationToGroup(userProfile);
                        } else {
                            Toast.makeText(GroupEntrantsActivity.this, "User has not opted in for notifications", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("GroupEntrantsActivity", "No such document");
                        Toast.makeText(GroupEntrantsActivity.this, "User profile not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("GroupEntrantsActivity", "get failed with ", task.getException());
                    Toast.makeText(GroupEntrantsActivity.this, "Failed to retrieve user profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void sendNotificationToGroup(Map<String, Object>  userProfile) {

        userProfile.put("notificationsEnabled", true); // Assume the user opted to receive notifications

        // Notification message based on groupType
        String title = "Notification for " + groupType + " entrants";
        String description;

        switch (groupType) {
            case "waiting":
                description = "Your status is: Waiting List";
                break;
            case "selected":
                description = "Congratulations! You've been selected.";
                break;
            case "cancelled":
                description = "Unfortunately, your status is: Cancelled";
                break;
            default:
                description = "Status update for your group.";
                break;
        }

        // Send notification using NotificationService
        NotificationService.sendNotification(userProfile, this, title, description);

        // Save notification to Firestore
        saveNotificationToFirestore(userProfile, title, description);  // Pass the user map

        Toast.makeText(this, "Notification sent to " + groupType + " entrants", Toast.LENGTH_SHORT).show();
    }


    private void saveNotificationToFirestore(Map<String, Object> user, String title, String description) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create notification data with the user's name
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userName", user.get("name"));  // Save the user's name
        notificationData.put("title", title);  // Save the title of the notification
        notificationData.put("description", description);  // Save the description/message
        notificationData.put("timestamp", FieldValue.serverTimestamp());  // Timestamp when notification was saved

        // Store in the notifications collection
        db.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(documentReference -> {
                    // Optionally handle success (like showing a toast)
                    Log.d("MyFirebaseMessagingService", "Notification saved successfully.");
                })
                .addOnFailureListener(e -> {
                    // Handle failure in saving to Firestore
                    Log.e("MyFirebaseMessagingService", "Error saving notification", e);
                });
    }


}