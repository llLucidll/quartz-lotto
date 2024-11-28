package com.example.myapplication.Repositories;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeRepository {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public HomeRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    // Get current user ID
    private String getUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }

    // Update event status to 'waiting' in Firestore
    public void setEventStatusToWaiting(String eventId) {
        String userId = getUserId();
        if (userId != null) {
            DocumentReference eventRef = db.collection("events").document(eventId);
            eventRef.update("attendees." + userId, "waiting");
        }
    }

    // Update event status to 'confirmed' in Firestore
    public void setEventStatusToConfirmed(String eventId) {
        String userId = getUserId();
        if (userId != null) {
            DocumentReference eventRef = db.collection("events").document(eventId);
            eventRef.update("attendees." + userId, "confirmed");
        }
    }

    // Update event status to 'cancelled' in Firestore
    public void setEventStatusToCancelled(String eventId) {
        String userId = getUserId();
        if (userId != null) {
            DocumentReference eventRef = db.collection("events").document(eventId);
            eventRef.update("attendees." + userId, "cancelled");
        }
    }
}
