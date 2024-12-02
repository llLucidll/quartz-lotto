// com/example/myapplication/repositories/UserRepository.java

package com.example.myapplication.Repositories;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.myapplication.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Used for handling all Firebase interactions related to User
 */
public class UserRepository {

    private static final String TAG = "UserRepository";
    private static final String USERS_COLLECTION = "users";

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    public UserRepository() {
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

    public interface LoadUserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    // Upload Profile Image to Firebase Storage
    public void uploadProfileImage(Uri imageUri, final UploadImageCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not authenticated"));
            return;
        }

        String imageName = "profile_images/" + userId + ".jpg";
        StorageReference storageRef = storage.getReference(imageName);
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> callback.onSuccess(uri.toString()))
                        .addOnFailureListener(callback::onFailure))
                .addOnFailureListener(callback::onFailure);
    }

    // Save or Update User in Firestore
    public void saveUser(User user, final FirestoreCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not authenticated"));
            return;
        }

        user.setUserID(userId); // Ensure the userID is set

        DocumentReference userRef = db.collection(USERS_COLLECTION).document(userId);
        userRef.set(user, SetOptions.merge())
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // Load User from Firestore
    public void loadUser(final LoadUserCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not authenticated"));
            return;
        }

        DocumentReference userRef = db.collection(USERS_COLLECTION).document(userId);
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure(new Exception("User not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    // Delete Profile Image from Firebase Storage
    public void deleteProfileImage(final FirestoreCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not authenticated"));
            return;
        }

        String imageName = "profile_images/" + userId + ".jpg";
        StorageReference storageRef = storage.getReference(imageName);
        storageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Also remove the image URL from Firestore
                    DocumentReference userRef = db.collection(USERS_COLLECTION).document(userId);
                    userRef.update("profileImageUrl", null)
                            .addOnSuccessListener(aVoid1 -> callback.onSuccess())
                            .addOnFailureListener(callback::onFailure);
                })
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
