package com.example.myapplication.Repositories;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.Models.Attendee;
import com.example.myapplication.Models.EntrantList;
import com.example.myapplication.NotificationService;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import android.content.Context;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Used for handling all Firebase interactions related to EntrantList
 */
public class EntrantListRepository {
    private static final String EVENT_COLLECTION_NAME = "Events";
    private static final String WAITLIST_COLLECTION_NAME = "Waitlist";
    private EntrantList entrantList;
    private static FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;


    // Constructor
    public EntrantListRepository() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    // Callback interfaces
    public interface FirestoreCallback {
        void onSuccess(ArrayList<Attendee> data); // Modified to accept data
        void onFailure(Exception e);
    }

    public interface FetchEntrantListCallback {
        void onFetchEntrantListSuccess(ArrayList<Attendee> entrantList);
        void onFetchEntrantListFailure(Exception e);
    }

    public interface Callback <T> {
        void onComplete(T result, Exception e);
    }

    /*
    Method to get the list of users from Firebase for all types of EntrantList (not chosen, waiting, confirmed, cancelled)
    */
    public void getEntrantlist(String eventId, String status, final FirestoreCallback callback) {
        // Each userID is mapped to a userName. userName for display.

        //Log.d("EntrantListRepository", "Fetching waitlist for event: " + eventId);

        db.collection(EVENT_COLLECTION_NAME)
                .document(eventId)
                .collection(WAITLIST_COLLECTION_NAME)
                .whereEqualTo("status", status)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ArrayList<Attendee> userList = new ArrayList<>();
                    //HashMap<String, String> waitlist = new HashMap<>();
                    //HashMap<String, String> waitlist = new HashMap<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Attendee user = new Attendee();
                        user.setUserId(doc.getId());
                        user.setUserName(doc.getString("userName"));
                        user.setUserEmail(doc.getString("userEmail"));
                        user.setStatus(doc.getString("status"));

                        //waitlist.put(doc.getId(), doc.getString("userName"));
                        String userName = doc.getString("userName");
                        String userId = doc.getId();
                        Log.d("EntrantListRepository", "User ID: " + userId + ", User Name: " + userName);
                        if (userName != null) { // Ensure userName is not null
                            userList.add(user);
                            //waitlist.put(userId, userName);
                            Log.d("EntrantListRepository", "User ID: " + userId + ", User Name: " + userName);
                        }
                    }
                    callback.onSuccess(userList); // Pass the data back through the callback
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }

    public void getAttendeeListSize(String eventId, Callback<Long> callback) {

        db.collection(EVENT_COLLECTION_NAME)
                .document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Long maxAttendees = document.getLong("maxAttendees");
                            callback.onComplete(maxAttendees, null);
                        } else {
                            callback.onComplete(null, new Exception("Document does not exist"));
                        }
                    } else {
                        callback.onComplete(null, task.getException());
                    }
                });
    }

    /**
     * Samples attendees from the waitlist for the given event and sends notifications.
     *
     * @param eventId The event ID.
     * @param size    The number of attendees to draw.
     * @param context The context to send notifications.
     */
    public void sampleAttendees(String eventId, int size, Context context) {
        getEntrantlist(eventId, "waiting", new FirestoreCallback() {
            @Override
            public void onSuccess(ArrayList<Attendee> entrants) {
                ArrayList<Attendee> selectedAttendees;
                ArrayList<Attendee> unselectedAttendees = null;
                if (entrants.size() <= size) {
                    selectedAttendees = entrants;
                } else {
                    List<Attendee> shuffledList = new ArrayList<>(entrants);
                    Collections.shuffle(shuffledList);
                    selectedAttendees = new ArrayList<>(shuffledList.subList(0, size));
                    unselectedAttendees = new ArrayList<>(shuffledList.subList(size, entrants.size()));

                }
                updateAttendeeList(eventId, selectedAttendees, context);
                updateWaitList(eventId, unselectedAttendees, context);
                sendNotificationLose(eventId, context);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("EntrantListRepository", "Error sampling attendees: ", e);
            }
        });
    }

    /**
     * Updates the status of selected attendees in Firestore and sends notifications.
     *
     * @param eventId         The event ID.
     * @param attendees       The list of selected attendees.
     * @param context         The context to send notifications.
     */
    private void updateAttendeeList(String eventId, ArrayList<Attendee> attendees, Context context) {
        ArrayList<String> userIds = new ArrayList<>();
        for (Attendee attendee : attendees) {
            userIds.add(attendee.getUserId());
        }

        int size = userIds.size();
        updateAttendeeListCount(eventId, size);

        db.collection(EVENT_COLLECTION_NAME)
                .document(eventId)
                .collection(WAITLIST_COLLECTION_NAME)
                .whereIn(FieldPath.documentId(), userIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        document.getReference().update("status", "selected")
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("EntrantListRepository", "User status updated: " + document.getId());
                                    sendNotificationWin(document.getId(), context);
                                })
                                .addOnFailureListener(e -> Log.w("EntrantListRepository", "Error updating user status: ", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("EntrantListRepository", "Error getting documents: ", e));
    }

    private void updateWaitList(String eventId, ArrayList<Attendee> attendees, Context context) {
        ArrayList<String> userIds = new ArrayList<>();
        for (Attendee attendee : attendees) {
            userIds.add(attendee.getUserId());
        }

        int size = userIds.size();
        updateAttendeeListCount(eventId, size);

        db.collection(EVENT_COLLECTION_NAME)
                .document(eventId)
                .collection(WAITLIST_COLLECTION_NAME)
                .whereIn(FieldPath.documentId(), userIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        sendNotificationLose(document.getId(), context);
                    }
                })
                .addOnFailureListener(e -> Log.e("EntrantListRepository", "Error getting documents: ", e));
    }



    /**
     * Sends a notification to the user with the given ID using `NotificationService`.
     *
     * @param userId  The user ID.
     * @param context The context to send notifications.
     */
    private void sendNotificationWin(String userId, Context context) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean notificationsPerm = documentSnapshot.getBoolean("notificationsPerm");
                        if (notificationsPerm != null && notificationsPerm) {
                            // Construct the user map
                            HashMap<String, Object> user = new HashMap<>();
                            user.put("userId", userId);

                            // Send notification
                            NotificationService.sendNotification(
                                    user,
                                    context,
                                    "Congratulations!",
                                    "You have been selected as an attendee for the event. PLS SIGN UP"
                            );
                            Log.d("EntrantListRepository", "Notification sent to user: " + userId);
                        } else {
                            Log.d("EntrantListRepository", "Notifications disabled for user: " + userId);
                        }
                    } else {
                        Log.e("EntrantListRepository", "User not found in Firestore for userId: " + userId);
                    }
                })
                .addOnFailureListener(e -> Log.e("EntrantListRepository", "Error fetching user: ", e));
    }

    /**
     * Sends a notification to the user with the given ID using `NotificationService`.
     *
     * @param userId  The user ID.
     * @param context The context to send notifications.
     */
    private void sendNotificationLose(String userId, Context context) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean notificationsPerm = documentSnapshot.getBoolean("notificationsPerm");
                        if (notificationsPerm != null && notificationsPerm) {
                            // Construct the user map
                            HashMap<String, Object> user = new HashMap<>();
                            user.put("userId", userId);

                            // Send notification
                            NotificationService.sendNotification(
                                    user,
                                    context,
                                    "Sorry!",
                                    "You have not been selected as an attendee for the event. PLS SIGN UP"
                            );
                            Log.d("EntrantListRepository", "Notification sent to user: " + userId);
                        } else {
                            Log.d("EntrantListRepository", "Notifications disabled for user: " + userId);
                        }
                    } else {
                        Log.e("EntrantListRepository", "User not found in Firestore for userId: " + userId);
                    }
                })
                .addOnFailureListener(e -> Log.e("EntrantListRepository", "Error fetching user: ", e));
    }

    public void updateAttendeeListCount(String eventId, int size) {

        db.collection(EVENT_COLLECTION_NAME)
                .document(eventId)
                .get()
                .addOnSuccessListener(DocumentSnapshot -> {
                    int currentAttendeeCount = DocumentSnapshot.getLong("currentAttendees").intValue();
                    int currentWaitlistCount = DocumentSnapshot.getLong("currentWaitlist").intValue();
                    if (currentAttendeeCount == 0 ){
                        DocumentSnapshot.getReference().update("currentAttendees", size);
                    } else  {
                        DocumentSnapshot.getReference().update("currentAttendees", (currentAttendeeCount + size));
                    }

                    if ((currentWaitlistCount - size) <= 0) {
                        DocumentSnapshot.getReference().update("currentWaitlist", 0);
                    } else {
                        DocumentSnapshot.getReference().update("currentWaitlist", (currentWaitlistCount - size));
                    }
                });

    }

}
