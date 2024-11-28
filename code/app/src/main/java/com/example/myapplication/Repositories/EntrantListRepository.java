package com.example.myapplication.Repositories;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
        void onSuccess(HashMap<String, String> data); // Modified to accept data
        void onFailure(Exception e);
    }
    public interface FetchEntrantListCallback {
        void onFetchEntrantListSuccess(ArrayList<String> entrantList);
        void onFetchEntrantListFailure(Exception e);
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
                    HashMap<String, String> waitlist = new HashMap<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String userName = doc.getString("userName");
                        String userId = doc.getId();
                        if (userName != null) { // Ensure userName is not null
                            waitlist.put(userId, userName);
                            Log.d("EntrantListRepository", "User ID: " + userId + ", User Name: " + userName);
                            Log.d("EntrantListRepository", "Waitlist: " + waitlist);
                        }
                    }
                    Log.d("EntrantListRepository", "Waitlist sent to controller: " + waitlist);
                    callback.onSuccess(waitlist); // Pass the data back through the callback
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }
}
