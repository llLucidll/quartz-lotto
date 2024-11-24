// com/example/myapplication/repositories/FacilityRepository.java

package com.example.myapplication.Repositories;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.Models.Facility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FacilityRepository {

    private static final String TAG = "FacilityRepository";
    private static final String FACILITY_COLLECTION = "Facilities";

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;


    //Constructor
    public FacilityRepository() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    // Callback Interfaces
    public interface UploadImageCallback {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
    }

    public interface FirestoreCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface LoadFacilityCallback {
        void onSuccess(Facility facility);
        void onFailure(Exception e);
    }


    // Upload Image to Firebase Storage
    public void uploadImage(Uri imageUri, final UploadImageCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not authenticated"));
            return;
        }

        String imageName = UUID.randomUUID().toString() + ".jpg";
        StorageReference storageRef = storage.getReference("facility_images/" + userId + "/" + imageName);
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> callback.onSuccess(uri.toString()))
                        .addOnFailureListener(callback::onFailure))
                .addOnFailureListener(callback::onFailure);
    }

    // Save or Update Facility in Firestore
    public void saveFacility(Facility facility, final FirestoreCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not authenticated"));
            return;
        }

        facility.setId(userId); // Use userId as facility ID to ensure one facility per user

        db.collection(FACILITY_COLLECTION).document(userId)
                .set(facility, SetOptions.merge())
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // Load Facility for a Specific User
    public void loadFacility(final LoadFacilityCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not authenticated"));
            return;
        }

        db.collection(FACILITY_COLLECTION).document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Facility facility = documentSnapshot.toObject(Facility.class);
                        callback.onSuccess(facility);
                    } else {
                        callback.onFailure(new Exception("Facility not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    // Delete Facility from Firestore
    public void deleteFacility(final FirestoreCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not authenticated"));
            return;
        }

        db.collection(FACILITY_COLLECTION).document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // Get Current User ID
    public String getCurrentUserId() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
        return null;
    }

    // Get Storage Reference from Image URL
    public StorageReference getStorageReference(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return null;
        return FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
    }
}
