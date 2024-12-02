package com.example.myapplication.Repositories;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.example.myapplication.Models.Event;
import com.example.myapplication.Views.HomeView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used for handling all Firebase interactions related to Home
 */
public class HomeRepository {
    private final FirebaseFirestore db;
    private final String deviceId;

    public HomeRepository(Context context) {
        this.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Returns all events where your status is waiting in
     * @param homeView
     */
    public void fetchWaitlistEvents(HomeView homeView) {
        String targetDeviceId = this.deviceId; //stores deviceid
        List<Event> deviceWaitlistEvents = new ArrayList<>(); //initializes empty list to hold events where user is on waitlist with "waiting" status
        Map<String, String> fetchedUserStatuses = new HashMap<>();

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
     * Returns all events where your device id shows up in and your status is selected
     * @param homeView
     */
    public void fetchSelectedEvents(HomeView homeView) {
        String targetDeviceId = this.deviceId;
        List<Event> deviceSelectedEvents = new ArrayList<>();
        Map<String, String> fetchedUserStatuses = new HashMap<>();

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
                                                    fetchedUserStatuses.put(event.getEventId(), status);
                                                }
                                            }
                                        } else {
                                            Log.e("FirestoreError", "Error fetching waitlist doc", waitlistDocTask.getException());
                                        }

                                        if (eventsProcessed[0] == totalEvents) {
                                            homeView.updateSelectedEvents(deviceSelectedEvents, fetchedUserStatuses);
                                        }
                                    });
                        }
                    } else {
                        Log.e("FirestoreError", "Error fetching events", task.getException());
                    }
                });
    }

    /**
     * Removes the user from the waitlist when they hit leave waitlist
     * @param eventId
     **/
    public void removeFromWaitlist(String eventId) {
        String deviceID = this.deviceId;


        DocumentReference eventDocRef = db.collection("Events").document(eventId);
        DocumentReference waitlistDocRef = eventDocRef.collection("Waitlist").document(deviceID);

        db.runTransaction(transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventDocRef);
            if (!eventSnapshot.exists()) {
                try {
                    throw new Exception("Event does not exist!");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            DocumentSnapshot waitlistSnapshot = transaction.get(waitlistDocRef);
            if (!waitlistSnapshot.exists()) {
                try {
                    throw new Exception("User is not on the waitlist!");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            String status = waitlistSnapshot.getString("status");
            Long currentWaitlist = eventSnapshot.getLong("currentWaitlist");
            Long currentAttendees = eventSnapshot.getLong("currentAttendees");

            if ("waiting".equals(status)) {
                // User is on waitlist, decrement currentWaitlist
                if (currentWaitlist != null && currentWaitlist > 0) {
                    transaction.update(eventDocRef, "currentWaitlist", currentWaitlist - 1);
                }
            } else if ("selected".equals(status) || "confirmed".equals(status)) {
                // User is in selected or confirmed, decrement currentAttendees
                if (currentAttendees != null && currentAttendees > 0) {
                    transaction.update(eventDocRef, "currentAttendees", currentAttendees - 1);
                }
            }
            // Remove user from waitlist
            transaction.delete(waitlistDocRef);

            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("Firebase", "Device ID successfully removed from waitlist.");
        }).addOnFailureListener(e -> {
            Log.w("Firebase", "Error removing device ID from waitlist", e);
        });

    }

    /**
     * Updates the event Status when the user confirms/declines the invitation.
     * @param eventId
     * @param newStatus
     */
    public void updateEventStatus(String eventId, String newStatus) {
        String deviceID = this.deviceId;

        DocumentReference eventDocRef = db.collection("Events").document(eventId);
        DocumentReference waitlistDocRef = eventDocRef.collection("Waitlist").document(deviceID);

        db.runTransaction(transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventDocRef);
            if (!eventSnapshot.exists()) {
                try {
                    throw new Exception("Event does not exist!");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            DocumentSnapshot waitlistSnapshot = transaction.get(waitlistDocRef);
            if (!waitlistSnapshot.exists()) {
                try {
                    throw new Exception("User is not on the waitlist!");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            String currentStatus = waitlistSnapshot.getString("status");
            Long currentWaitlist = eventSnapshot.getLong("currentWaitlist");
            Long currentAttendees = eventSnapshot.getLong("currentAttendees");

            if ("selected".equals(currentStatus) && "confirmed".equals(newStatus)) {
                // User confirms participation
                if (currentWaitlist != null && currentWaitlist > 0) {
                    transaction.update(eventDocRef, "currentWaitlist", currentWaitlist - 1);
                }
                if (currentAttendees != null) {
                    transaction.update(eventDocRef, "currentAttendees", currentAttendees + 1);
                } else {
                    transaction.update(eventDocRef, "currentAttendees", 1L);
                }
                // Update user status
                transaction.update(waitlistDocRef, "status", newStatus);
            } else {
                try {
                    throw new Exception("Invalid status transition from " + currentStatus + " to " + newStatus);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("Firebase", "Status updated to " + newStatus);
        }).addOnFailureListener(e -> {
            Log.w("Firebase", "Error updating status", e);
        });
    }

}
