package com.example.myapplication;

import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * BaseActivity allows a method to get the current user's UID
 * and other activities should extend this class to access user identification
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Retrieves the unique device ID.
     *
     * @return Device ID as a String.
     */
    protected String retrieveDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Retrieves the user's name from their profile asynchronously.
     *
     * @param callback A callback to handle the retrieved user name.
     */
    public void getUserName(UserNameCallback callback) {
        String deviceId = retrieveDeviceId();
        if (deviceId == null) {
            callback.onCallback("Unknown");
            return;
        }

        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userName = documentSnapshot.getString("userName");
                callback.onCallback(userName != null ? userName : "Unknown");
            } else {
                callback.onCallback("Unknown");
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            callback.onCallback("Unknown");
        });
    }

    /**
     * Callback interface for retrieving user name.
     */
    public interface UserNameCallback {
        void onCallback(String userName);
    }

    /**
     * Retrieves the user's type (admin or entrant) from their profile asynchronously.
     *
     * @param callback A callback to handle the retrieved user type.
     */
    public void getUserType(UserTypeCallback callback) {
        String deviceId = retrieveDeviceId();
        if (deviceId == null) {
            callback.onCallback("entrant"); // Default type
            return;
        }

        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userType = documentSnapshot.getString("userType");
                callback.onCallback(userType != null ? userType : "entrant");
            } else {
                callback.onCallback("entrant");
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            callback.onCallback("entrant");
        });
    }

    /**
     * Callback interface for retrieving user type.
     */
    public interface UserTypeCallback {
        void onCallback(String userType);
    }
}
