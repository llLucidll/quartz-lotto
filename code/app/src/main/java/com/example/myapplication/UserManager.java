package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.Views.OrganizerProfileView;
import com.example.myapplication.EditProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String TAG = "UserManager";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public UserManager() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void fetchUserRole(Context context, RoleCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated.", Toast.LENGTH_SHORT).show();
            callback.onRoleFetched("entrant"); // Default role
            return;
        }

        String uid = currentUser.getUid();
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isAdmin = documentSnapshot.getBoolean("isAdmin");
                        Boolean isOrganizer = documentSnapshot.getBoolean("isOrganizer");

                        // Handle nulls by treating them as false
                        isAdmin = isAdmin != null && isAdmin;
                        isOrganizer = isOrganizer != null && isOrganizer;

                        Log.d(TAG, "User roles - isAdmin: " + isAdmin + ", isOrganizer: " + isOrganizer);

                        if (isAdmin) {
                            callback.onRoleFetched("admin");
                        } else if (isOrganizer) {
                            callback.onRoleFetched("organizer");
                        } else {
                            callback.onRoleFetched("entrant");
                        }
                    } else {
                        Log.e(TAG, "User document does not exist. Creating new user document.");

                        // Create the user document with default values
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("isAdmin", false);
                        userData.put("isOrganizer", false);
                        userData.put("name", ""); // Default name, can be empty
                        userData.put("email", currentUser.getEmail() != null ? currentUser.getEmail() : "");
                        userData.put("dob", "");
                        userData.put("country", "");
                        userData.put("phone", "");
                        userData.put("profileImageUrl", "");
                        userData.put("userID", uid);

                        userRef.set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "User document created successfully.");
                                    callback.onRoleFetched("entrant"); // Default role
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error creating user document", e);
                                    Toast.makeText(context, "Failed to create user data.", Toast.LENGTH_SHORT).show();
                                    callback.onRoleFetched("entrant"); // Default role
                                });
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.update("isOrganizer", true)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User promoted to organizer.");
                    Toast.makeText(context, "You are now an organizer!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, OrganizerProfileView.class);
                    context.startActivity(intent);

                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error promoting user to organizer", e);
                    Toast.makeText(context, "Failed to update your role.", Toast.LENGTH_SHORT).show();
                });
    }
}
