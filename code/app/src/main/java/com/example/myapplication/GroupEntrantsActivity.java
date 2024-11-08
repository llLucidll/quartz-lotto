package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Activity for displaying and managing group entrants based on their status.
 * Allows viewing of entrants for different groups and sending notifications to eligible users.
 */
public class GroupEntrantsActivity extends AppCompatActivity {

    private static final String TAG = "GroupEntrantsActivity";
    private TextView textViewEntrants;
    private Button buttonSendNotification;
    private FirebaseFirestore db;
    private String groupType;

    /**
     * Initializes the activity, sets up UI elements, Firebase, and loads the list of entrants.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_entrants);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();
        textViewEntrants = findViewById(R.id.textViewEntrants);
        buttonSendNotification = findViewById(R.id.buttonSendNotification);

        // Retrieve group type from the Intent to determine the group to notify
        groupType = getIntent().getStringExtra("groupType");

        // Ensure the notification channel is created before sending notifications
        NotificationUtils.createNotificationChannel(this);

        // Load entrants into the TextView
        loadEntrantsList();

        // Set up click listener for sending notifications
        buttonSendNotification.setOnClickListener(v -> sendNotificationsToAllEntrants());
    }

    /**
     * Loads the list of entrants from Firestore and displays those matching the group type.
     * Only entrants with matching group status are displayed.
     */
    private void loadEntrantsList() {
        DocumentReference waitlistRef = db.collection("Waitlists").document("Waitlist_1");

        waitlistRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> data = documentSnapshot.getData();
                if (data != null && !data.isEmpty()) {
                    StringBuilder entrantsDisplay = new StringBuilder();
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        Object entrantObject = entry.getValue();

                        if (entrantObject instanceof List) {
                            List<?> entrant = (List<?>) entrantObject;
                            if (entrant.size() >= 2) {
                                String userId = (String) entrant.get(0);
                                String status = (String) entrant.get(1);

                                if (userId != null && !userId.isEmpty()) {
                                    // Filter entrants based on groupType
                                    if (status != null && status.equalsIgnoreCase(groupType)) {
                                        // Fetch user profile using userId as document ID
                                        DocumentReference userProfileRef = db.collection("users").document(userId);

                                        userProfileRef.get().addOnSuccessListener(userProfileDoc -> {
                                            if (userProfileDoc.exists()) {
                                                String name = userProfileDoc.getString("name");
                                                // Append the name and status to the entrants display if available
                                                if (name != null) {
                                                    entrantsDisplay.append("Entrant: ").append(name)
                                                            .append("\nStatus: ").append(status).append("\n\n");
                                                    // Update UI on the main thread
                                                    runOnUiThread(() -> textViewEntrants.setText(entrantsDisplay.toString()));
                                                }
                                            } else {
                                                Log.d(TAG, "User profile not found for ID: " + userId);
                                            }
                                        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching user profile for ID: " + userId, e));
                                    } else {
                                        Log.d(TAG, "Entrant " + userId + " does not match groupType " + groupType);
                                    }
                                } else {
                                    Log.e(TAG, "Invalid user ID in entrants list.");
                                    Toast.makeText(this, "Invalid user ID in waitlist.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e(TAG, "Incomplete entrant data.");
                            }
                        } else {
                            Log.e(TAG, "Entrant data is not in the expected format.");
                        }
                    }
                    // Handle case where no entrants match the groupType
                    if (entrantsDisplay.length() == 0) {
                        runOnUiThread(() -> textViewEntrants.setText("No entrants found for this group."));
                    }
                } else {
                    Log.e(TAG, "No entrants found in waitlist.");
                    Toast.makeText(this, "No entrants found in waitlist.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Waitlist_1 document does not exist or is null.");
                Toast.makeText(this, "Waitlist document not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to fetch Waitlist_1 document", e);
            Toast.makeText(this, "Failed to load waitlist.", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Sends notifications to all entrants in the specified group who meet notification criteria.
     * Only sends notifications to entrants with notifications enabled and a matching status.
     */
    private void sendNotificationsToAllEntrants() {
        DocumentReference waitlistRef = db.collection("Waitlists").document("Waitlist_1");

        waitlistRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<?> entrantData = (List<?>) documentSnapshot.get("user_1");
                if (entrantData != null && entrantData.size() >= 2) {
                    String userId = (String) entrantData.get(0);
                    String status = (String) entrantData.get(1);

                    if (userId != null && !userId.isEmpty()) {
                        // Fetch user profile using userId as document ID
                        DocumentReference userProfileRef = db.collection("users").document(userId);

                        userProfileRef.get().addOnSuccessListener(userProfileDoc -> {
                            if (userProfileDoc.exists()) {
                                Boolean notificationsEnabled = userProfileDoc.getBoolean("notificationsEnabled");
                                String name = userProfileDoc.getString("name");

                                // Prepare user data with null checks
                                Map<String, Object> user = new HashMap<>();
                                user.put("userId", userId);
                                user.put("name", name != null ? name : "Unknown User");
                                user.put("notificationsEnabled", notificationsEnabled != null ? notificationsEnabled : false);

                                // Check if the user's status matches the selected group type and notifications are enabled
                                if (status != null && status.equalsIgnoreCase(groupType)
                                        && Boolean.TRUE.equals(user.get("notificationsEnabled"))) {
                                    Log.d(TAG, "User " + userId + " meets notification criteria. Sending notification.");
                                    sendNotificationToEntrant(user, status);
                                } else {
                                    Log.d(TAG, "User " + userId + " does not meet notification criteria.");
                                }
                            } else {
                                Log.d(TAG, "User profile not found for ID: " + userId);
                            }
                        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching user profile for ID: " + userId, e));
                    } else {
                        Log.e(TAG, "Invalid user ID in entrants list.");
                        Toast.makeText(this, "Invalid user ID in waitlist.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Entrant data is incomplete in Waitlist_1.");
                    Toast.makeText(this, "No entrants found in waitlist.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Waitlist_1 document does not exist or is null.");
                Toast.makeText(this, "Waitlist document not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to fetch Waitlist_1 document", e);
            Toast.makeText(this, "Failed to load waitlist.", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Sends a notification to a specific entrant with a status-based message.
     *
     * @param user   A map containing user details including "userId" and "name".
     * @param status The status of the entrant (e.g., "not chosen", "selected", "cancelled").
     */
    private void sendNotificationToEntrant(Map<String, Object> user, String status) {
        String title = "Notification for " + groupType + " entrants";
        String description;

        switch (status) {
            case "not chosen":
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

        // Call NotificationService to send and save the notification in Firestore
        NotificationService.sendNotification(user, this, title, description);
    }

}