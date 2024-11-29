package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity to display user IDs grouped by status for a specific event's waitlist.
 */
public class EventWaitlistActivity extends BaseActivity {

    private static final String TAG = "EventWaitlistActivity";

    private ExpandableListView expandableListView;
    private WaitlistExpandableListAdapter adapter;
    private Button sendNotificationButton;

    private FirebaseFirestore db;
    private String eventId;
    private String eventName;

    // Data structures for ExpandableListView
    private List<String> listGroupTitles;
    private HashMap<String, List<String>> listData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_waitlist);

        expandableListView = findViewById(R.id.expandableListView);
        sendNotificationButton = findViewById(R.id.button_send_notifications);

        db = FirebaseFirestore.getInstance();

        // Get eventId and eventName from intent
        eventId = getIntent().getStringExtra("eventId");
        eventName = getIntent().getStringExtra("eventName");

        if (eventId == null || eventName == null) {
            Toast.makeText(this, "Event data not available", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Event ID or Event Name is null");
            finish();
            return;
        }

        listGroupTitles = new ArrayList<>();
        listData = new HashMap<>();

        adapter = new WaitlistExpandableListAdapter(this, listGroupTitles, listData);
        expandableListView.setAdapter(adapter);

        fetchWaitlistData();

        sendNotificationButton.setOnClickListener(v -> {
            sendNotificationsToSelectedGroups();
        });
    }

    /**
     * Fetches waitlist data from Firestore and populates the ExpandableListView.
     */
    private void fetchWaitlistData() {
        Log.d(TAG, "Fetching waitlist data for eventId: " + eventId);
        CollectionReference waitlistRef = db.collection("Events").document(eventId).collection("Waitlist");
        waitlistRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "Fetched " + queryDocumentSnapshots.size() + " users from Waitlist");
                    Map<String, List<String>> statusGroups = new HashMap<>();
                    statusGroups.put("cancelled", new ArrayList<>());
                    statusGroups.put("confirmed", new ArrayList<>());
                    statusGroups.put("waiting", new ArrayList<>());

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String userId = doc.getId();
                        String status = doc.getString("status");
                        Log.d(TAG, "UserID: " + userId + ", Status: " + status);
                        if (status != null && statusGroups.containsKey(status)) {
                            statusGroups.get(status).add(userId);
                        } else {
                            Log.w(TAG, "Unknown or missing status for user: " + userId);
                        }
                    }

                    // Prepare data for ExpandableListView
                    listGroupTitles.clear();
                    listData.clear();

                    for (Map.Entry<String, List<String>> entry : statusGroups.entrySet()) {
                        String status = entry.getKey();
                        List<String> userIds = entry.getValue();
                        if (!userIds.isEmpty()) {
                            listGroupTitles.add(status);
                            listData.put(status, userIds);
                            Log.d(TAG, "Added group: " + status + " with " + userIds.size() + " users");
                        }
                    }

                    if (listGroupTitles.isEmpty()) {
                        Toast.makeText(this, "No users found in Waitlist", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "No users found in any status group");
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch waitlist for event " + eventId, e);
                    Toast.makeText(this, "Failed to fetch waitlist", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Sends notifications to the selected status groups.
     */
    private void sendNotificationsToSelectedGroups() {
        List<String> selectedGroups = adapter.getSelectedGroups();

        Log.d(TAG, "Selected Groups for Notification: " + selectedGroups.toString());

        if (selectedGroups.isEmpty()) {
            Toast.makeText(this, "Please select at least one status group", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "No status groups selected");
            return;
        }

        for (String status : selectedGroups) {
            List<String> userIds = listData.get(status);
            if (userIds == null || userIds.isEmpty()) {
                Log.w(TAG, "No users in status group: " + status);
                continue;
            }

            String title;
            String message;

            switch (status) {
                case "cancelled":
                    title = "Event Update: Cancelled";
                    message = "Your participation has been cancelled for " + eventName + ".";
                    break;
                case "confirmed":
                    title = "Event Confirmation";
                    message = "Your participation has been confirmed for " + eventName + "!";
                    break;
                case "waiting":
                    title = "Event Waitlist Update";
                    message = "You are on the waitlist for " + eventName + ".";
                    break;
                default:
                    Log.w(TAG, "Unknown status group: " + status);
                    continue;
            }

            Log.d(TAG, "Sending notifications to status group: " + status + " with " + userIds.size() + " users");

            for (String userId : userIds) {
                sendNotificationToUser(userId, title, message);
            }
        }

        Toast.makeText(this, "Notifications sent", Toast.LENGTH_SHORT).show();
    }

    /**
     * Sends a notification to a single user.
     *
     * @param userId  The ID of the user.
     * @param title   The title of the notification.
     * @param message The message content of the notification.
     */
    private void sendNotificationToUser(String userId, String title, String message) {
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);

        NotificationService.sendNotification(user, this, title, message);
        Log.d(TAG, "Notification sent to userId: " + userId);
    }
}
