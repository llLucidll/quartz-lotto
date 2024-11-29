package com.example.myapplication.Repositories;

import android.util.Log;

import com.example.myapplication.Models.Attendee;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/*
Used for handling all Firebase stuff related to EntrantList
*/
public class EntrantListRepository {
    private static final String EVENT_COLLECTION_NAME = "Events";
    private static final String WAITLIST_COLLECTION_NAME = "Waitlist";

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
     * Used to update the status of the users who were selected in the draw to selected
     * @param eventId
     * @param users
     */
    public void updateAttendeeList(String eventId, ArrayList<Attendee> users) {

        ArrayList<String> userIds = new ArrayList<>();
        // Convert Attendee objects to String user Ids for easy db usage.
        for (Attendee user : users) {
            userIds.add(user.getUserId());
        }

        db.collection(EVENT_COLLECTION_NAME)
                .document(eventId)
                .collection(WAITLIST_COLLECTION_NAME)
                .whereIn("userId", userIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        document.getReference().update("status", "selected")
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("EntrantListRepository", "User status updated successfully" + document.getId());
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("EntrantListRepository", "Error updating user status" + document.getId(), e);
                                });
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("EntrantListRepository", "Error getting documents: ", e);

                });


    }
}