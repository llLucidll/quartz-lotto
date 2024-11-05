package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class WaitinglistActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAttendees;
    private AttendeeAdapter attendeesAdapter;
    private List<Attendee> attendees;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitinglist);

        recyclerViewAttendees = findViewById(R.id.recyclerViewAttendees);
        recyclerViewAttendees.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
        attendees = new ArrayList<>();
        attendeesAdapter = new AttendeeAdapter(attendees, false, true);
        recyclerViewAttendees.setAdapter(attendeesAdapter);

        // Load attendees with status "not chosen"
        loadNotChosenAttendees();
    }

    private void loadNotChosenAttendees() {
        CollectionReference usersRef = db.collection("Users");

        Query query = usersRef.whereEqualTo("status", "not chosen");

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                attendees.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String userID = document.getId();
                    String name = document.getString("name");
                    String status = document.getString("status");

                    Attendee attendee = new Attendee(userID, name, status);
                    attendees.add(attendee);
                }

                attendeesAdapter.notifyDataSetChanged();
            }
        });
    }
}
