package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
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
    private List<Attendee> attendees;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String eventId= "eventId_1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitinglist);

        recyclerViewAttendees = findViewById(R.id.recyclerViewAttendees);
        recyclerViewAttendees.setLayoutManager(new LinearLayoutManager(this));

        attendeesAdapter = new AttendeeAdapter(attendees, false, true);
        recyclerViewAttendees.setAdapter(attendeesAdapter);

        // Load attendees with status "not chosen"
        loadEventWaitlist(eventId);
        final ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };
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

    public void loadEventWaitlist(String eventId) {
        DocumentReference eventRef = db.collection("Events").document(eventId);

        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, Object>> waitlist = (List<Map<String, Object>>) document.get("waitlist");
                    if (waitlist != null) {
                        List<Object> selectedEntries = new ArrayList<>();
                        for (Map<String, Object> entry : waitlist) {
                            List<Object> arrayField = (List<Object>) entry.get("arrayField");
                            if (arrayField != null && arrayField.size() > 1) {
                                if ("not chosen".equals(arrayField.get(1))) {
                                    selectedEntries.add(arrayField.get(0));
                                }
                            }
                        }
                        System.out.println("Selected entries: " + selectedEntries);
                    } else {
                        System.out.println("No waitlist found for event.");
                    }
                } else {
                    System.out.println("Event does not exist.");
                }
            } else {
                System.err.println("Error getting event: " + task.getException());
            }
        });
    }

}
