package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.myapplication.Views.HomeView;
import com.example.myapplication.Views.OrganizerProfileView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.osmdroid.config.Configuration;

/**
 * MainActivity initializes anonymous authentication and handles navigation.
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Osmdroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Retrieve the device ID
        String deviceId = retrieveDeviceId();

        // Fetch and display notifications for the current device
        NotificationService.receiveNotifications(deviceId, this);


        // Initialize UserManager with the current context
        userManager = new UserManager(this);

        // Sign in anonymously
        signInAnonymously();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeView()) // changed
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeView(); // changed
                    break;
                case R.id.nav_camera:
                    selectedFragment = new QRScannerFragment(); // Navigate to QRScannerFragment
                    break;
                case R.id.nav_profile:
                    // Handle profile navigation through UserManager
                    userManager.fetchUserRole(this, role -> {
                        userManager.navigateToProfile(this, role);
                    });
                    return true;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .addToBackStack(null)
                        .commit();
            }
            return true;
        });
    }

    /**
     * Signs in the user anonymously and handles the result.
     */
    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d(TAG, "signInAnonymously:success");
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInAnonymously:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
