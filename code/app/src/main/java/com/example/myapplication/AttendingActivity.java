package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttendingActivity extends AppCompatActivity {

    private RecyclerView recyclerViewWaitlist;
    private RecyclerView recyclerViewCancelled;
    private RecyclerView recyclerViewConfirmed;
    private AttendeeAdapter waitlistAdapter;
    private AttendeeAdapter cancelledAdapter;
    private AttendeeAdapter confirmedAdapter;
    private List<Attendee> waitlist;
    private List<Attendee> cancelledList;
    private List<Attendee> confirmedList;

    private FirebaseFirestore db;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attending);

        db = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("event_id");

        recyclerViewWaitlist = findViewById(R.id.recyclerViewWaitlist);
        recyclerViewCancelled = findViewById(R.id.recyclerViewCancelled);
        recyclerViewConfirmed = findViewById(R.id.recyclerViewConfirmed);

        recyclerViewWaitlist.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCancelled.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewConfirmed.setLayoutManager(new LinearLayoutManager(this));

        waitlist = new ArrayList<>();
        cancelledList = new ArrayList<>();
        confirmedList = new ArrayList<>();

        waitlistAdapter = new AttendeeAdapter(waitlist, true, false);
        cancelledAdapter = new AttendeeAdapter(cancelledList, false, false);
        confirmedAdapter = new AttendeeAdapter(confirmedList, false, false);

        recyclerViewWaitlist.setAdapter(waitlistAdapter);
        recyclerViewCancelled.setAdapter(cancelledAdapter);
        recyclerViewConfirmed.setAdapter(confirmedAdapter);

        loadAttendinglist(eventId);

        final ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        Button tabWaitlist = findViewById(R.id.tabWaitlist);
        tabWaitlist.setOnClickListener(view -> {
            Intent intent = new Intent(this, WaitinglistActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);
        });
    }

    /**
     * Loads the attending list for a specific event from Firestore.
     * @param eventId
     */
    private void loadAttendinglist(String eventId) {
        DocumentReference eventRef = db.collection("Events").document(eventId);

        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Map<String, Object>> waitlistArray = (List<Map<String, Object>>) task.getResult().get("waitlist");

                if (waitlistArray != null) {
                    for (Map<String, Object> entry : waitlistArray) {
                        List<Object> arrayField = (List<Object>) entry.get("arrayField");
                        if (arrayField != null && arrayField.size() > 1) {
                            String userId = (String) arrayField.get(0);
                            String status = (String) arrayField.get(1);

                            if (!"not chosen".equals(status)) {
                                fetchUserNameAndSort(userId, status);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Fetches user data based on the provided user ID and updates the corresponding list.
     * @param userId
     * @param status
     */
    private void fetchUserNameAndSort(String userId, String status) {
        db.collection("Users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String name = task.getResult().getString("name");

                Attendee attendee = new Attendee(userId, name, status);

                switch (status) {
                    case "waiting":
                        waitlist.add(attendee);
                        break;
                    case "cancelled":
                        cancelledList.add(attendee);
                        break;
                    case "confirmed":
                        confirmedList.add(attendee);
                        break;
                }

                // Notify adapters of data change
                waitlistAdapter.notifyDataSetChanged();
                cancelledAdapter.notifyDataSetChanged();
                confirmedAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Moves an attendee to the cancelled list.
     * @param position
     */
    public void moveToCancelled(int position) {
        Attendee attendee = waitlist.get(position);
        waitlist.remove(position);
        attendee.setStatus("cancelled");
        cancelledList.add(attendee);
        updateStatusInFirestore(attendee, "cancelled");

        waitlistAdapter.notifyDataSetChanged();
        cancelledAdapter.notifyDataSetChanged();
    }

    /**
     * Moves an attendee to the confirmed list.
     * @param position
     */
    public void moveToConfirmed(int position) {
        Attendee attendee = waitlist.get(position);
        waitlist.remove(position);
        attendee.setStatus("confirmed");
        confirmedList.add(attendee);
        updateStatusInFirestore(attendee, "confirmed");

        waitlistAdapter.notifyDataSetChanged();
        confirmedAdapter.notifyDataSetChanged();
    }

    /**
     * Updates the status of an attendee in Firestore.
     * @param attendee
     * @param newStatus
     */
    private void updateStatusInFirestore(Attendee attendee, String newStatus) {
        db.collection("Users").document(attendee.getUserID())
                .update("status", newStatus);
    }
}
