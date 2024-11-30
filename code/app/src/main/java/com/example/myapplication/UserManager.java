package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.Views.OrganizerProfileView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserManager {
    private static final String TAG = "UserManager";
    private FirebaseFirestore db;
    private String deviceId;

    // Constructor to initialize Firestore and retrieve the deviceId
    public UserManager(Context context) {
        db = FirebaseFirestore.getInstance();
        deviceId = retrieveDeviceId(context);
        if (deviceId == null) {
            Log.e(TAG, "Failed to retrieve device ID.");
        }
    }

    // Method to retrieve the device ID
    private String retrieveDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // Fetch user role based on deviceId
    public void fetchUserRole(Context context, RoleCallback callback) {
        if (deviceId == null || deviceId.isEmpty()) {
            Toast.makeText(context, "Device ID not found.", Toast.LENGTH_SHORT).show();
            callback.onRoleFetched("entrant"); // Default role
            return;
        }

        Log.d(TAG, "Fetching document for Device ID: " + deviceId);

        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Document data: " + documentSnapshot.getData());
                        Boolean isAdmin = documentSnapshot.getBoolean("isAdmin");
                        Boolean isOrganizer = documentSnapshot.getBoolean("isOrganizer");

                        // Handle nulls by treating them as false
                        isAdmin = isAdmin != null && isAdmin;
                        isOrganizer = isOrganizer != null && isOrganizer;

                        if (isAdmin) {
                            callback.onRoleFetched("admin");
                        } else if (isOrganizer) {
                            callback.onRoleFetched("organizer");
                        } else {
                            callback.onRoleFetched("entrant");
                        }
                    } else {
                        Log.e(TAG, "User document does not exist for Device ID: " + deviceId);
                        Toast.makeText(context, "User data not found.", Toast.LENGTH_SHORT).show();
                        callback.onRoleFetched("entrant"); // Default role
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user role", e);
                    Toast.makeText(context, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
                    callback.onRoleFetched("entrant"); // Default role
                });
    }

    public interface RoleCallback {
        void onRoleFetched(String role);
    }

    public void navigateToProfile(Context context, String role) {
        Intent intent;
        switch (role.toLowerCase()) {
            case "admin":
                intent = new Intent(context, AdminProfileActivity.class);
                break;
            case "organizer":
                intent = new Intent(context, OrganizerProfileView.class);
                break;
            case "entrant":
            default:
                intent = new Intent(context, EditProfileActivity.class);
                break;
        }
        context.startActivity(intent);
    }

    public void promoteToOrganizer(Context context) {
        if (deviceId == null || deviceId.isEmpty()) {
            Toast.makeText(context, "Device ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.update("isOrganizer", true)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User promoted to organizer.");
                    Toast.makeText(context, "You are now an organizer!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error promoting user to organizer", e);
                });
    }
}