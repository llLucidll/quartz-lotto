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
    private void loadEventWaitlist(String eventId) {
        db.collection("Waitlist")
                .whereEqualTo("event_id", eventId) // Filter by event_id
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the waitlist data
                            Map<String, Object> waitlistData = document.getData();

                            // Check each user in the waitlist
                            for (Map.Entry<String, Object> entry : waitlistData.entrySet()) {
                                // Skip non-user fields (like event_id)
                                if (entry.getKey().startsWith("user_")) {
                                    List<String> userInfo = (List<String>) entry.getValue();
                                    String status = userInfo.get(1); // User's status (not chosen, waiting, etc.)

                                    // If status is "not chosen", add the user to the list
                                    if ("not chosen".equals(status)) {
                                        String userId = userInfo.get(0); // User profile ID
                                        // Fetch the user's name from the "Users" collection
                                        fetchUserName(userId);
                                    }
                                }
                            }
                        }
                    }
                });
    }



    /**
     * Fetches user name based on the provided user ID.
     * @param userId
     */
    private void fetchUserName(String userId) {
        db.collection("Users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        String userName = document.getString("name");

                        if (userName != null) {
                            // Add the user to the attendees list
                            attendees.add(new Attendee(userName, "not chosen"));
                            attendeesAdapter.notifyDataSetChanged(); // Update RecyclerView
                        }
                    }
                });
    }

}
