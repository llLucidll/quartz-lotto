package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaitinglistActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAttendees;
    private AttendeeAdapter attendeesAdapter;
    private List<Attendee> attendees = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitinglist);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        eventId = getIntent().getStringExtra("event_id");

        recyclerViewAttendees = findViewById(R.id.recyclerViewAttendees);
        recyclerViewAttendees.setLayoutManager(new LinearLayoutManager(this));

        attendeesAdapter = new AttendeeAdapter(attendees, false, true);
        recyclerViewAttendees.setAdapter(attendeesAdapter);

        if (eventId != null) {
            loadEventWaitlist(eventId);
        }

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        Button tabAttendees = findViewById(R.id.tabAttendees);
        tabAttendees.setOnClickListener(view -> {
            Intent intent = new Intent(this, AttendingActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);
        });
    }

//    private void loadNotChosenAttendees() {
//        CollectionReference usersRef = db.collection("Users");
//
//        Query query = usersRef.whereEqualTo("status", "not chosen");
//
//        query.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                attendees.clear();
//
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    String userID = document.getId();
//                    String name = document.getString("name");
//                    String status = document.getString("status");
//
//                    Attendee attendee = new Attendee(userID, name, status);
//                    attendees.add(attendee);
//                }
//
//                attendeesAdapter.notifyDataSetChanged();
//            }
//        });
//    }
    /**
     * Loads the waitlist for a specific event from Firestore.
     * This method fetches the waitlist data of the event identified by the provided event ID,
     * iterates through the entries, extracts the user IDs and statuses, and populates the attendee list
     * for users with a "not chosen" status. The attendee list is then passed to the `AttendeeAdapter` for display.
     *
     * @param eventId The unique ID of the event whose waitlist is to be loaded.
     */
    public void loadEventWaitlist(String eventId) {
        DocumentReference eventRef = db.collection("Events").document(eventId);

        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Get the waitlist (which is an array of maps)
                    List<Map<String, Object>> waitlist = (List<Map<String, Object>>) document.get("waitlist");

                    if (waitlist != null) {
                        List<Attendee> selectedEntries = new ArrayList<>();

                        // Iterate through each entry in the waitlist
                        for (Map<String, Object> entry : waitlist) {
                            // Assuming "arrayField" contains the user ID at index 0 and status at index 1
                            List<Object> arrayField = (List<Object>) entry.get("arrayField");
                            if (arrayField != null && arrayField.size() > 1) {
                                String userId = (String) arrayField.get(0);
                                String status = (String) arrayField.get(1);

                                // Only add attendees with "not chosen" status for waiting list
                                if ("not chosen".equals(status)) {
                                    Attendee attendee = new Attendee(userId, "", status);
                                    selectedEntries.add(attendee);
                                }
                            }
                        }

                        // Update your adapter's data
                        attendeesAdapter = new AttendeeAdapter(selectedEntries, true, false);
                        recyclerViewAttendees.setAdapter(attendeesAdapter);
                        attendeesAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("WaitinglistActivity", "No waitlist found for event.");
                    }
                } else {
                    Log.d("WaitinglistActivity", "Event does not exist.");
                }
            } else {
                Log.e("WaitinglistActivity", "Error getting event: " + task.getException());
            }
        });
    }

    /**
     * Loads attendees based on the selected entries.
     * @param selectedEntries
     */
    private void loadAttendees(List<Object> selectedEntries) {
        for (Object userId : selectedEntries) {
            // fetch user data based on userId
            fetchUserData(userId.toString());
        }
    }

    /**
     * Fetches user data based on the provided user ID.
     * @param userId
     */
    private void fetchUserData(String userId) {
        db.collection("Users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDoc = task.getResult();
                String name = userDoc.getString("name");
                Attendee attendee = new Attendee(userId, name, "waiting");
                attendees.add(attendee);
                attendeesAdapter.notifyDataSetChanged();
            }
        });
    }
}
