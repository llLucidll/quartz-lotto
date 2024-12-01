package com.example.myapplication.Repositories;


import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.BaseActivity;
import com.example.myapplication.Models.Event;
import com.example.myapplication.Views.HomeView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeRepository extends BaseActivity {
    private final FirebaseFirestore db;
    private final String deviceId;


    public HomeRepository() {
        this.deviceId = "TA9jSDvfKDX6wP3ueVO5nTwrf4D3";
        db = FirebaseFirestore.getInstance();
    }

    // Method to fetch events where the device is on the waitlist with "waiting" status
    public void fetchWaitingDevices(HomeView homeView) {
        String targetDeviceId = "TA9jSDvfKDX6wP3ueVO5nTwrf4D3"; // Sample device ID
        List<Event> deviceWaitlistEvents = new ArrayList<>(); // List to store Event objects for display

        // Query all events from the "events" collection
        db.collection("Events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Loop through each event in the collection
                        for (QueryDocumentSnapshot eventDoc : task.getResult()) {
                            Event event = eventDoc.toObject(Event.class);
                            event.setEventId(eventDoc.getId()); // Set event ID

                            // Query the "waitlist" subcollection for the current event
                            db.collection("Events")
                                    .document(event.getEventId())
                                    .collection("Waitlist")
                                    .get()
                                    .addOnCompleteListener(waitlistTask -> {
                                        if (waitlistTask.isSuccessful()) {
                                            // Loop through each device document in the waitlist
                                            for (QueryDocumentSnapshot waitlistDoc : waitlistTask.getResult()) {
                                                Log.d("FirestoreDebug", "Fetched waitlist documents: " + waitlistTask.getResult().size());
                                                // Check if document ID matches the target device ID
                                                if (targetDeviceId.equals(waitlistDoc.getId())) {
                                                    String status = waitlistDoc.getString("status"); // Get status field

                                                    // Check if status equals "waiting"
                                                    if ("waiting".equals(status)) {
                                                        // Set event details and add to waitlist
                                                        event.setEventName(eventDoc.getString("eventName"));
                                                        event.setEventDateTime(eventDoc.getString("eventDateTime"));
                                                        deviceWaitlistEvents.add(event);
                                                        break; // Only add once for this device
                                                    }
                                                }
                                            }

                                            // Update the waitlist in the HomeView
                                            homeView.updateWaitlistEvents(deviceWaitlistEvents);
                                        } else {
                                            Log.e("FirestoreError", "Error fetching waitlist", waitlistTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.e("FirestoreError", "Error fetching events", task.getException());
                    }
                });
    }


    public static void removeFromWaitlist(String eventName) {
        String deviceID = "TA9jSDvfKDX6wP3ueVO5nTwrf4D3"; // The device ID to be removed
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query all events to find the event with the matching name
        db.collection("Events")
                .whereEqualTo("eventName", eventName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Loop through the events to check for a match
                        for (DocumentSnapshot document : task.getResult()) {
                            // Once we find the event document, remove the device ID from the waitlist
                            db.collection("Events")
                                    .document(document.getId()) // Document ID for the event
                                    .collection("Waitlist")
                                    .document(deviceID) // Device ID to remove
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firebase", "Device ID successfully removed from waitlist.");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firebase", "Error removing device ID from waitlist", e);
                                    });
                        }
                    } else {
                        Log.w("Firebase", "No event found with the name: " + eventName);
                    }
                });
    }
}
