package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.*;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity to display event details to attendees and allow them to sign up or join the waitlist.
 */
public class EventSignupActivity extends BaseActivity {

    private static final String TAG = "EventSignupActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private TextView eventNameTextView, dateTextView, timeTextView, descriptionTextView, maxAttendeesTextView, currentAttendeesTextView;
    private ImageView posterImageView;
    private Button signupButton;
    private ProgressBar progressBar;
    private String eventId;

    private FusedLocationProviderClient fusedLocationClient;
    private double userLatitude;
    private double userLongitude;
    private boolean locationObtained = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setContentView(R.layout.activity_event_signup);

        // Initialize views
        eventNameTextView = findViewById(R.id.signupEventNameTextView);
        dateTextView = findViewById(R.id.signupDateTextView);
        timeTextView = findViewById(R.id.signupTimeTextView);
        descriptionTextView = findViewById(R.id.signupDescriptionTextView);
        maxAttendeesTextView = findViewById(R.id.signupMaxAttendeesTextView);
        currentAttendeesTextView = findViewById(R.id.signupCurrentAttendeesTextView);
        posterImageView = findViewById(R.id.signupPosterImageView);
        signupButton = findViewById(R.id.signupButton);
        progressBar = findViewById(R.id.signupProgressBar);

        // Retrieve eventId from intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Event ID missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch and display event details
        fetchEventDetails();

        // Set up sign-up button listener
        signupButton.setOnClickListener(v -> {
            if (locationObtained) {
                registerForEvent();
            } else {
                Toast.makeText(this, "Obtaining your location. Please wait.", Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            }
        });

        // Check and request location permissions
        checkLocationPermission();
    }

    /**
     * Checks for location permissions and requests them if not granted.
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Handles the result of the permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getCurrentLocation();
            } else {
                // Permission denied
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Required")
                        .setMessage("Location access is needed to sign up for events. Please grant location permission.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Redirect to app settings
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .create()
                        .show();
            }
        }
    }

    /**
     * Obtains the user's current location.
     */
    private void getCurrentLocation() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            userLatitude = location.getLatitude();
                            userLongitude = location.getLongitude();
                            locationObtained = true;
                            Log.d(TAG, "Location obtained: " + userLatitude + ", " + userLongitude);
                        } else {
                            // If last location is null, request a new location
                            requestNewLocationData();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to get location.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "getCurrentLocation: ", e);
                    });
        } catch (SecurityException e) {
            Log.e(TAG, "Location permission not granted.", e);
        }
    }

    /**
     * Requests new location data if last location is unavailable.
     */
    private void requestNewLocationData() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000) // 5 seconds
                .setFastestInterval(2000) // 2 seconds
                .setNumUpdates(1);

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } catch (SecurityException e) {
            Log.e(TAG, "Location permission not granted.", e);
        }
    }

    /**
     * Location callback to receive location updates.
     */
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();
                locationObtained = true;
                Log.d(TAG, "New location obtained: " + userLatitude + ", " + userLongitude);
            } else {
                Toast.makeText(EventSignupActivity.this, "Unable to get current location.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * Fetches event details from Firestore and displays them.
     */
    private void fetchEventDetails() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("Events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String eventName = documentSnapshot.getString("eventName");
                        String drawDate = documentSnapshot.getString("drawDate");
                        String eventDateTime = documentSnapshot.getString("eventDateTime");
                        String description = documentSnapshot.getString("description");
                        String posterUrl = documentSnapshot.getString("posterUrl");
                        Long maxAttendeesLong = documentSnapshot.getLong("maxAttendees");
                        Long currentAttendeesLong = documentSnapshot.getLong("currentAttendees");

                        int maxAttendees = maxAttendeesLong != null ? maxAttendeesLong.intValue() : 0;
                        int currentAttendees = currentAttendeesLong != null ? currentAttendeesLong.intValue() : 0;

                        // Update UI
                        eventNameTextView.setText(eventName);
                        dateTextView.setText("Date: " + drawDate);
                        timeTextView.setText("Time: " + eventDateTime);
                        descriptionTextView.setText(description);
                        maxAttendeesTextView.setText("Max Attendees: " + maxAttendees);
                        currentAttendeesTextView.setText("Current Attendees: " + currentAttendees);

                        if (posterUrl != null && !posterUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(posterUrl)
                                    .placeholder(R.drawable.ic_placeholder_image)
                                    .into(posterImageView);
                        } else {
                            posterImageView.setImageResource(R.drawable.ic_placeholder_image);
                        }

                    } else {
                        Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching event details.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching event", e);
                })
                .addOnCompleteListener(task -> progressBar.setVisibility(View.GONE));
    }

    /**
     * Handles the sign-up process for the event.
     * Adds the user to attendees or waitlist accordingly and stores their location.
     */
    private void registerForEvent() {
        progressBar.setVisibility(View.VISIBLE);
        signupButton.setEnabled(false); // Disable button to prevent multiple clicks

        String deviceId = retrieveDeviceId();
        if (deviceId == null) {
            Toast.makeText(this, "Unable to retrieve device ID.", Toast.LENGTH_SHORT).show();
            resetSignupButton();
            return;
        }

        // Fetch user's profile details before proceeding
        DocumentReference userProfileRef = db.collection("users").document(deviceId);
        userProfileRef.get().addOnSuccessListener(userSnapshot -> {
            if (!userSnapshot.exists() || userSnapshot.getString("name") == null) {
                Toast.makeText(this, "Please complete your profile before signing up.", Toast.LENGTH_SHORT).show();
                resetSignupButton();
                return;
            }

            String userName = userSnapshot.getString("name");
            String userEmail = userSnapshot.getString("email");

            // Proceed with event signup
            performEventSignup(deviceId, userName, userEmail);

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error retrieving profile. Please try again.", Toast.LENGTH_SHORT).show();
            resetSignupButton();
        });
    }

    /**
     * Performs the event sign-up operation, storing user location in Firestore.
     */
    private void performEventSignup(String deviceId, String userName, String userEmail) {
        // Ensure location is available
        if (!locationObtained) {
            Toast.makeText(this, "Location not available. Please try again.", Toast.LENGTH_SHORT).show();
            resetSignupButton();
            return;
        }

        DocumentReference eventRef = db.collection("Events").document(eventId);
        DocumentReference attendeeRef = eventRef.collection("Attendees").document(deviceId);

        db.runTransaction(transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            if (!eventSnapshot.exists()) {
                throw new FirebaseFirestoreException("Event does not exist.",
                        FirebaseFirestoreException.Code.NOT_FOUND);
            }

            // Check event capacity
            long maxAttendees = eventSnapshot.getLong("maxAttendees") != null
                    ? eventSnapshot.getLong("maxAttendees")
                    : 0;
            long currentAttendees = eventSnapshot.getLong("currentAttendees") != null
                    ? eventSnapshot.getLong("currentAttendees")
                    : 0;

            if (currentAttendees < maxAttendees) {
                // Add attendee and store location
                Map<String, Object> attendeeData = new HashMap<>();
                attendeeData.put("userName", userName);
                attendeeData.put("userEmail", userEmail);
                attendeeData.put("status", "Attending");
                attendeeData.put("latitude", userLatitude);
                attendeeData.put("longitude", userLongitude);
                transaction.set(attendeeRef, attendeeData);

                // Update current attendees
                transaction.update(eventRef, "currentAttendees", FieldValue.increment(1));
            } else {
                // Add to waitlist and store location
                DocumentReference waitlistRef = eventRef.collection("Waitlist").document(deviceId);
                Map<String, Object> waitlistData = new HashMap<>();
                waitlistData.put("userName", userName);
                waitlistData.put("userEmail", userEmail);
                waitlistData.put("status", "Waitlisted");
                waitlistData.put("latitude", userLatitude);
                waitlistData.put("longitude", userLongitude);
                transaction.set(waitlistRef, waitlistData);
            }

            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "You have successfully signed up!", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity or update UI as needed
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            resetSignupButton();
        }).addOnCompleteListener(task -> progressBar.setVisibility(View.GONE));
    }

    /**
     * Resets the sign-up button and related UI elements after the sign-up operation.
     */
    private void resetSignupButton() {
        signupButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }
}
