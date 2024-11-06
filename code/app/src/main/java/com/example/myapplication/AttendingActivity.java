package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attending);

        db = FirebaseFirestore.getInstance();

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

        loadAttendees();
    }

    private void loadAttendees() {
        CollectionReference usersRef = db.collection("Users");

        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Clear the lists to avoid duplicates if loadAttendees() is called again
                waitlist.clear();
                cancelledList.clear();
                confirmedList.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String userID = document.getId(); // Get the unique document ID
                    String name = document.getString("name");
                    String status = document.getString("status");

                    // Create a new Attendee with the userID, name, and status
                    Attendee attendee = new Attendee(userID, name, status);

                    // Sort attendee based on status
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
                }

                // Notify adapters of data change
                waitlistAdapter.notifyDataSetChanged();
                cancelledAdapter.notifyDataSetChanged();
                confirmedAdapter.notifyDataSetChanged();
            }
        });
    }

    public void moveToCancelled(int position) {
        Attendee attendee = waitlist.get(position);
        waitlist.remove(position);
        attendee.setStatus("cancelled");
        cancelledList.add(attendee);
        updateStatusInFirestore(attendee, "cancelled");

        waitlistAdapter.notifyDataSetChanged();
        cancelledAdapter.notifyDataSetChanged();
    }

    public void moveToConfirmed(int position) {
        Attendee attendee = waitlist.get(position);
        waitlist.remove(position);
        attendee.setStatus("confirmed");
        confirmedList.add(attendee);
        updateStatusInFirestore(attendee, "confirmed");

        waitlistAdapter.notifyDataSetChanged();
        confirmedAdapter.notifyDataSetChanged();
    }

    private void updateStatusInFirestore(Attendee attendee, String newStatus) {
        db.collection("Users").document(attendee.getUserID())
                .update("status", newStatus);
    }
}
