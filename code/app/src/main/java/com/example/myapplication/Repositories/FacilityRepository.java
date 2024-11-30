package com.example.myapplication.Repositories;

import android.net.Uri;
import android.util.Log;

import com.example.myapplication.Models.Facility;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class FacilityRepository {

    private static final String TAG = "FacilityRepository";
    private static final String FACILITY_COLLECTION = "Facilities";

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    // Constructor
    public FacilityRepository() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
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
    public void uploadImage(Uri imageUri, String deviceId, final UploadImageCallback callback) {
        if (deviceId == null || deviceId.isEmpty()) {
            callback.onFailure(new Exception("Device ID is not available"));
            return;
        }

        String imageName = UUID.randomUUID().toString() + ".jpg";
        StorageReference storageRef = storage.getReference("facility_images/" + deviceId + "/" + imageName);
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> callback.onSuccess(uri.toString()))
                        .addOnFailureListener(callback::onFailure))
                .addOnFailureListener(callback::onFailure);
    }

    // Save or Update Facility in Firestore
    public void saveFacility(Facility facility, String deviceId, final FirestoreCallback callback) {
        if (deviceId == null || deviceId.isEmpty()) {
            callback.onFailure(new Exception("Device ID is not available"));
            return;
        }

        db.collection(FACILITY_COLLECTION).document(deviceId)
                .set(facility, SetOptions.merge())
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // Load Facility for a Specific Device ID
    public void loadFacility(String deviceId, final LoadFacilityCallback callback) {
        if (deviceId == null || deviceId.isEmpty()) {
            callback.onFailure(new Exception("Device ID is not available"));
            return;
        }

        db.collection(FACILITY_COLLECTION).document(deviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Facility facility = documentSnapshot.toObject(Facility.class);
                        callback.onSuccess(facility);
                    } else {
                        callback.onFailure(new Exception("Facility not found for device ID: " + deviceId));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    // Delete Facility from Firestore
    public void deleteFacility(String deviceId, final FirestoreCallback callback) {
        if (deviceId == null || deviceId.isEmpty()) {
            callback.onFailure(new Exception("Device ID is not available"));
            return;
        }

        db.collection(FACILITY_COLLECTION).document(deviceId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // Get Storage Reference from Image URL
    public StorageReference getStorageReference(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        return FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
    }
}
