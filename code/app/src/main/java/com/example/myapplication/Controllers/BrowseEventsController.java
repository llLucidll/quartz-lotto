package com.example.myapplication.Controllers;

import com.example.myapplication.Models.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BrowseEventsController {

    private final FirebaseFirestore db;

    public BrowseEventsController() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Fetches events from the 'Events' collection in Firestore.
     *
     * @param onSuccess Callback when events are successfully fetched.
     * @param onFailure Callback when fetching fails.
     */
    public void fetchEvents(Consumer<List<Event>> onSuccess, Consumer<String> onFailure) {
        db.collection("Events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> events = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) { // Explicit type used here
                        String id = document.getId();
                        String eventName = document.getString("eventName");
                        events.add(new Event(id, eventName));
                    }
                    onSuccess.accept(events);
                })
                .addOnFailureListener(e -> onFailure.accept("Failed to fetch events: " + e.getMessage()));
    }

    /**
     * Deletes an event and its 'Waitlist' subcollection from Firestore.
     *
     * @param event     The event to delete.
     * @param onSuccess Callback when deletion is successful.
     * @param onFailure Callback when deletion fails.
     */
    public void deleteEvent(Event event, Runnable onSuccess, Consumer<String> onFailure) {
        DocumentReference eventRef = db.collection("Events").document(event.getEventId());
        CollectionReference waitlistRef = eventRef.collection("Waitlist");

        waitlistRef.get().addOnSuccessListener(querySnapshot -> {
            WriteBatch batch = db.batch();
            for (QueryDocumentSnapshot doc : querySnapshot) { // Explicit type used here
                batch.delete(doc.getReference());
            }
            batch.commit().addOnSuccessListener(aVoid -> {
                eventRef.delete().addOnSuccessListener(aVoid2 -> onSuccess.run())
                        .addOnFailureListener(e -> onFailure.accept("Failed to delete event: " + e.getMessage()));
            }).addOnFailureListener(e -> onFailure.accept("Failed to delete waitlist: " + e.getMessage()));
        }).addOnFailureListener(e -> onFailure.accept("Failed to get waitlist: " + e.getMessage()));
    }
}
