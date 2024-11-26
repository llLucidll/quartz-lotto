package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for browsing and deleting events.
 */
public class BrowseEventsActivity extends AppCompatActivity {

    private RecyclerView eventRecyclerView;
    private FirebaseFirestore db;
    private EventAdapterAdmin eventAdapterAdmin;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);

        // Initialize RecyclerView and Firestore
        db = FirebaseFirestore.getInstance();
        eventRecyclerView = findViewById(R.id.event_recycler_view);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        eventAdapterAdmin = new EventAdapterAdmin(this, eventList, this::deleteEvent);
        eventRecyclerView.setAdapter(eventAdapterAdmin);

        fetchEvents();
    }

    /**
     * Fetches events from the 'Events' collection in Firestore.
     */
    private void fetchEvents() {
        db.collection("Events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String eventName = document.getString("eventName");

                        // Use the simplified constructor
                        Event event = new Event(id, eventName);
                        eventList.add(event);
                    }
                    eventAdapterAdmin.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch events.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Deletes an event and its 'Waitlist' subcollection from Firestore.
     *
     * @param event The event to delete.
     */
    private void deleteEvent(Event event) {
        // Get reference to the event document
        DocumentReference eventRef = db.collection("Events").document(event.getEventId());

        // Get reference to the 'Waitlist' subcollection
        CollectionReference waitlistRef = eventRef.collection("Waitlist");

        // Delete all documents in the 'Waitlist' subcollection
        waitlistRef.get().addOnSuccessListener(querySnapshot -> {
            WriteBatch batch = db.batch();
            for (DocumentSnapshot doc : querySnapshot) {
                batch.delete(doc.getReference());
            }
            // Commit the batch deletion
            batch.commit().addOnSuccessListener(aVoid -> {
                // After deleting the subcollection, delete the event document
                eventRef.delete().addOnSuccessListener(aVoid2 -> {
                    eventList.remove(event);
                    eventAdapterAdmin.notifyDataSetChanged();
                    Toast.makeText(this, "Event and waitlist deleted.", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete event.", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to delete waitlist.", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to get waitlist.", Toast.LENGTH_SHORT).show();
        });
    }
}
