package com.example.myapplication.Repositories;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

/*
Used for handling all Firebase stuff related to EntrantList
 */
public class EntrantListRepository {
    private static final String COLLECTION_NAME = "Waitlists";

    private static FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final FirebaseAuth auth;

    //Constructor
    public EntrantListRepository() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    //Callback interfaces
    public interface FirestoreCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public static String createListId() {
        return db.collection(COLLECTION_NAME).document().getId();
    }

}
