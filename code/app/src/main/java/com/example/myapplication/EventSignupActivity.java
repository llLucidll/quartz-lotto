// File: app/src/main/java/com/example/myapplication/EventSignupActivity.java
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
import com.example.myapplication.Models.Attendee;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnSuccessListener;
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
            Log.e(TAG, "Event ID is missing from the intent.");
            finish();
            return;
        }

        Log.d(TAG, "Event ID retrieved: " + eventId);

        // Fetch and display event details
        fetchEventDetails();

        // Set up sign-up button listener
        signupButton.setOnClickListener(v -> {
            if (locationObtained) {
                registerForEvent();
            } else {
                Toast.makeText(this, "Obtaining your location. Please wait.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Location not yet obtained. Attempting to get location.");
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
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permissions already granted.");
            getCurrentLocation();
        } else {
            Log.d(TAG, "Requesting location permissions.");
            // Request both ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Handles the result of the permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean fineLocationGranted = false;
            boolean coarseLocationGranted = false;

            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                        fineLocationGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                    } else if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permissions[i])) {
                        coarseLocationGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                    }
                }
            }

            if (fineLocationGranted || coarseLocationGranted) {
                Log.d(TAG, "Location permissions granted.");
                getCurrentLocation();
            } else {
                // Permission denied
                Log.d(TAG, "Location permissions denied.");
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
                            Log.d(TAG, "Last location is null, requesting new location.");
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
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000) // 5 seconds
                .setFastestInterval(2000) // 2 seconds
                .setNumUpdates(1);

        try {
            Log.d(TAG, "Requesting new location data.");
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
                Log.w(TAG, "Location callback received null location.");
            }
        }
    };

    /**
     * Fetches event details from Firestore and displays them.
     */
    private void fetchEventDetails() {
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Fetching event details from Firestore.");

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

                        Log.d(TAG, "Event Details - Name: " + eventName + ", Draw Date: " + drawDate +
                                ", Event Time: " + eventDateTime + ", Max Attendees: " + maxAttendees +
                                ", Current Attendees: " + currentAttendees);

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
                        Log.e(TAG, "Event document does not exist.");
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching event details.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching event details: ", e);
                })
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Finished fetching event details.");
                });
    }

    /**
     * Handles the sign-up process for the event.
     * Adds the user to attendees or waitlist accordingly and stores their location.
     */
    private void registerForEvent() {
        progressBar.setVisibility(View.VISIBLE);
        signupButton.setEnabled(false); // Disable button to prevent multiple clicks
        Log.d(TAG, "Initiating sign-up process.");

        String deviceId = retrieveDeviceId();
        if (deviceId == null) {
            Toast.makeText(this, "Unable to retrieve device ID.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Device ID is null.");
            resetSignupButton();
            return;
        }

        Log.d(TAG, "Device ID retrieved: " + deviceId);

        // Fetch user's profile details before proceeding
        DocumentReference userProfileRef = db.collection("users").document(deviceId);
        userProfileRef.get().addOnSuccessListener(userSnapshot -> {
            if (!userSnapshot.exists() || userSnapshot.getString("name") == null) {
                Toast.makeText(this, "Please complete your profile before signing up.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "User profile does not exist or is incomplete.");
                resetSignupButton();
                return;
            }

            String userName = userSnapshot.getString("name");
            String userEmail = userSnapshot.getString("email"); // Ensure your Firestore has an 'email' field

            Log.d(TAG, "User Profile - Name: " + userName + ", Email: " + userEmail);

            // Proceed with event signup
            performEventSignup(deviceId, userName, userEmail);

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error retrieving profile. Please try again.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error retrieving user profile: ", e);
            resetSignupButton();
        });
    }

    /**
     * Performs the event sign-up operation, storing user location in Firestore.
     */
    private void performEventSignup(String userId, String userName, String userEmail) {
        // Ensure location is available
        if (!locationObtained) {
            Toast.makeText(this, "Location not available. Please try again.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Location not obtained before sign-up.");
            resetSignupButton();
            return;
        }

        Log.d(TAG, "Attempting to sign up user with ID: " + userId + " at location: " + userLatitude + ", " + userLongitude);

        DocumentReference eventRef = db.collection("Events").document(eventId);
        DocumentReference attendeeRef = eventRef.collection("Attendees").document(userId);
        DocumentReference waitlistRef = eventRef.collection("Waitlist").document(userId);

        db.runTransaction(transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            if (!eventSnapshot.exists()) {
                Log.e(TAG, "Event does not exist.");
                throw new FirebaseFirestoreException("Event does not exist.",
                        FirebaseFirestoreException.Code.NOT_FOUND);
            }

            // Check if user is already in Attendees
            DocumentSnapshot attendeeSnapshot = transaction.get(attendeeRef);
            if (attendeeSnapshot.exists()) {
                Log.e(TAG, "User already signed up in Attendees.");
                throw new FirebaseFirestoreException("User already signed up for the event.",
                        FirebaseFirestoreException.Code.ALREADY_EXISTS);
            }

            // Check if user is already in Waitlist
            DocumentSnapshot waitlistSnapshot = transaction.get(waitlistRef);
            if (waitlistSnapshot.exists()) {
                Log.e(TAG, "User already on the Waitlist.");
                throw new FirebaseFirestoreException("User already on the waitlist.",
                        FirebaseFirestoreException.Code.ALREADY_EXISTS);
            }

            // Check event capacity
            Long maxAttendeesLong = eventSnapshot.getLong("maxAttendees");
            Long currentAttendeesLong = eventSnapshot.getLong("currentAttendees");

            int maxAttendees = maxAttendeesLong != null ? maxAttendeesLong.intValue() : 0;
            int currentAttendees = currentAttendeesLong != null ? currentAttendeesLong.intValue() : 0;

            Log.d(TAG, "Event Capacity - Current Attendees: " + currentAttendees + ", Max Attendees: " + maxAttendees);

            if (currentAttendees < maxAttendees) {
                // Add attendee and store location
                Map<String, Object> attendeeData = new HashMap<>();
                attendeeData.put("userName", userName);
                attendeeData.put("userEmail", userEmail);
                attendeeData.put("status", "Attending");
                attendeeData.put("latitude", userLatitude);      // Dynamic latitude
                attendeeData.put("longitude", userLongitude);    // Dynamic longitude
                transaction.set(attendeeRef, attendeeData);

                // Update current attendees
                transaction.update(eventRef, "currentAttendees", FieldValue.increment(1));

                Log.d(TAG, "User added to Attendees.");

            } else {
                // Add to waitlist and store location
                Map<String, Object> waitlistData = new HashMap<>();
                waitlistData.put("userName", userName);
                waitlistData.put("userEmail", userEmail);
                waitlistData.put("status", "Waitlisted");
                waitlistData.put("latitude", userLatitude);      // Dynamic latitude
                waitlistData.put("longitude", userLongitude);    // Dynamic longitude
                transaction.set(waitlistRef, waitlistData);

                Log.d(TAG, "User added to Waitlist.");
            }

            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "You have successfully signed up!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Sign-up transaction successful.");
            finish(); // Close the activity or update UI as needed
        }).addOnFailureListener(e -> {
            if (e instanceof FirebaseFirestoreException) {
                FirebaseFirestoreException ffe = (FirebaseFirestoreException) e;
                if (ffe.getCode() == FirebaseFirestoreException.Code.ALREADY_EXISTS) {
                    Toast.makeText(this, ffe.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Sign-up failed: " + ffe.getMessage());
                } else {
                    Toast.makeText(this, "Signup failed: " + ffe.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Sign-up failed with Firestore error: ", e);
                }
            } else {
                Toast.makeText(this, "Signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Sign-up failed with unknown error: ", e);
            }
            resetSignupButton();
        }).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            Log.d(TAG, "Sign-up transaction completed.");
        });
    }

    /**
     * Resets the sign-up button and related UI elements after the sign-up operation.
     */
    private void resetSignupButton() {
        signupButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        Log.d(TAG, "Sign-up button reset.");
    }
}
