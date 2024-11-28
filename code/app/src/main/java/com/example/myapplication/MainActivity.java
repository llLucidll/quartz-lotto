package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import org.osmdroid.config.Configuration;


/**
 * MainActivity initializes anonymous authentication and handles navigation.
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Osmdroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Sign in anonymously
        signInAnonymously();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_camera:
                    selectedFragment = new QRScannerFragment(); // Navigate to QRScannerFragment
                    break;
                case R.id.nav_profile:
                    // Start AdminProfileActivity instead of navigating to a Fragment
                    Intent intent = new Intent(this, OrganizerProfileActivity.class);
                    startActivity(intent);
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
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInAnonymously:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        // You can now use user.getUid() as the unique identifier
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInAnonymously:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Retrieves the current user's UID.
     *
     * @return The UID of the current user, or null if not signed in.
     */
    public String getCurrentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }
}
