package com.example.myapplication.Repositories;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.example.myapplication.Models.Event;
import com.example.myapplication.Views.HomeView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeRepository {
    private final FirebaseFirestore db;
    private final String deviceId;

    public HomeRepository(Context context) {
        this.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Fetches events where the user is on the waitlist with "waiting" status.
     */
    public void fetchWaitlistEvents(HomeView homeView) {
        String targetDeviceId = this.deviceId; //stores deviceid
        List<Event> deviceWaitlistEvents = new ArrayList<>(); //initializes empty list to hold events where user is on waitlist with "waiting" status

        db.collection("Events")
                .get()//gets all events in event collection
                .addOnCompleteListener(task -> { //listener to handle result of fetch
                    if (task.isSuccessful()) {
                        int totalEvents = task.getResult().size(); //will use this for iterations later
                        int[] eventsProcessed = {0}; //none processed at first

                        for (QueryDocumentSnapshot eventDoc : task.getResult()) {
                            Event event = eventDoc.toObject(Event.class);//converts each firestore document to event object
                            event.setEventId(eventDoc.getId()); //sets events id from firestore

                            db.collection("Events")
                                    .document(event.getEventId())
                                    .collection("Waitlist")//goes to waitlist
                                    .document(targetDeviceId)//tries to get document corresponding with users device id
                                    .get()
                                    .addOnCompleteListener(waitlistTask -> {
                                        eventsProcessed[0]++;
                                        if (waitlistTask.isSuccessful()) {
                                            DocumentSnapshot waitlistDoc = waitlistTask.getResult();
                                            if (waitlistDoc.exists()) {
                                                String status = waitlistDoc.getString("status");
                                                if ("waiting".equals(status)) {
                                                    deviceWaitlistEvents.add(event);
                                                }
                                            }
                                        } else {
                                            Log.e("FirestoreError", "Error fetching waitlist doc", waitlistTask.getException());
                                        }

                                        if (eventsProcessed[0] == totalEvents) {
                                            homeView.updateWaitlistEvents(deviceWaitlistEvents);
                                        }
                                    });
                        }
                    } else {
                        Log.e("FirestoreError", "Error fetching events", task.getException());
                    }
                });
    }

    /**
     * Fetches events where the user is "selected" or "confirmed".
     */
    public void fetchSelectedEvents(HomeView homeView) {
        String targetDeviceId = this.deviceId;
        List<Event> deviceSelectedEvents = new ArrayList<>();

        db.collection("Events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalEvents = task.getResult().size();
                        int[] eventsProcessed = {0};

                        for (QueryDocumentSnapshot eventDoc : task.getResult()) {
                            Event event = eventDoc.toObject(Event.class);
                            event.setEventId(eventDoc.getId());

                            db.collection("Events")
                                    .document(event.getEventId())
                                    .collection("Waitlist")
                                    .document(targetDeviceId)
                                    .get()
                                    .addOnCompleteListener(waitlistDocTask -> {
                                        eventsProcessed[0]++;
                                        if (waitlistDocTask.isSuccessful()) {
                                            DocumentSnapshot waitlistDoc = waitlistDocTask.getResult();
                                            if (waitlistDoc.exists()) {
                                                String status = waitlistDoc.getString("status");
                                                if ("selected".equals(status) || "confirmed".equals(status)) {
                                                    deviceSelectedEvents.add(event);
                                                }
                                            }
                                        } else {
                                            Log.e("FirestoreError", "Error fetching waitlist doc", waitlistDocTask.getException());
                                        }

                                        if (eventsProcessed[0] == totalEvents) {
                                            homeView.updateSelectedEvents(deviceSelectedEvents);
                                        }
                                    });
                        }
                    } else {
                        Log.e("FirestoreError", "Error fetching events", task.getException());
                    }
                });
    }

    /**
     * Removes the user from the waitlist of the specified event.
     */
    public void removeFromWaitlist(String eventName) {
        String deviceID = this.deviceId;

        db.collection("Events")
                .whereEqualTo("eventName", eventName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("Events")
                                    .document(document.getId())
                                    .collection("Waitlist")
                                    .document(deviceID)
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

    /**
     * Updates the user's status for a specific event.
     */
    public void updateEventStatus(String eventName, String newStatus) {
        String deviceID = this.deviceId;

        db.collection("Events")
                .whereEqualTo("eventName", eventName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("Events")
                                    .document(document.getId())
                                    .collection("Waitlist")
                                    .document(deviceID)
                                    .update("status", newStatus)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firebase", "Status updated to " + newStatus);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firebase", "Error updating status", e);
                                    });
                        }
                    } else {
                        Log.w("Firebase", "No event found with the name: " + eventName);
                    }
                });
    }
}
