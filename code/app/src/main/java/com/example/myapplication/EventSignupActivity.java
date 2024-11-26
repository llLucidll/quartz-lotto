package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity to display event details to attendees and allow them to sign up or join the waitlist.
 */
public class EventSignupActivity extends BaseActivity { // Now extends BaseActivity

    private static final String TAG = "EventSignupActivity";

    private TextView eventNameTextView, dateTextView, timeTextView, descriptionTextView, maxAttendeesTextView, currentAttendeesTextView;
    private ImageView posterImageView;
    private Button signupButton;
    private ProgressBar progressBar;
    private String eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        signupButton.setOnClickListener(v -> registerForEvent());
    }

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
                                    .placeholder(R.drawable.ic_placeholder_image) // Ensure you have a placeholder image
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
     * Prevents duplicate sign-ups and adds the user to attendees or waitlist accordingly.
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
        DocumentReference userProfileRef = db.collection("Users").document(deviceId);
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

    // Method to handle the actual signup logic after profile validation
    private void performEventSignup(String deviceId, String userName, String userEmail) {
        DocumentReference eventRef = db.collection("Events").document(eventId);
        DocumentReference attendeeRef = eventRef.collection("Attendees").document(deviceId);

        db.runTransaction(transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            if (!eventSnapshot.exists()) {
                try {
                    throw new Exception("Event does not exist.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            // Check event capacity
            long maxAttendees = eventSnapshot.getLong("maxAttendees") != null
                    ? eventSnapshot.getLong("maxAttendees")
                    : 0;
            long currentAttendees = eventSnapshot.getLong("currentAttendees") != null
                    ? eventSnapshot.getLong("currentAttendees")
                    : 0;

            if (currentAttendees < maxAttendees) {
                // Add attendee
                Map<String, Object> attendeeData = new HashMap<>();
                attendeeData.put("userName", userName);
                attendeeData.put("userEmail", userEmail);
                attendeeData.put("status", "Attending");
                transaction.set(attendeeRef, attendeeData);

                transaction.update(eventRef, "currentAttendees", FieldValue.increment(1));
            } else {
                // Add to waitlist
                DocumentReference waitlistRef = eventRef.collection("Waitlist").document(deviceId);
                Map<String, Object> waitlistData = new HashMap<>();
                waitlistData.put("userName", userName);
                waitlistData.put("userEmail", userEmail);
                waitlistData.put("status", "Waitlisted");
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
     * Proceeds with registration after retrieving user name.
     *
     * @param deviceId The device ID of the user.
     * @param userName The user name.
     */
    private void proceedWithRegistration(String deviceId, String userName) {
        // Define document references
        DocumentReference eventRef = db.collection("Events").document(eventId);
        DocumentReference attendeeRef = eventRef.collection("Attendees").document(deviceId);
        DocumentReference waitlistRef = eventRef.collection("Waitlist").document(deviceId);

        db.runTransaction(transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            if (!eventSnapshot.exists()) {
                try {
                    throw new Exception("Event does not exist.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            Long maxAttendeesLong = eventSnapshot.getLong("maxAttendees");
            Long currentAttendeesLong = eventSnapshot.getLong("currentAttendees");

            int maxAttendees = maxAttendeesLong != null ? maxAttendeesLong.intValue() : 0;
            int currentAttendees = currentAttendeesLong != null ? currentAttendeesLong.intValue() : 0;

            // Check if deviceId is already an attendee
            DocumentSnapshot attendeeSnapshot = transaction.get(attendeeRef);
            if (attendeeSnapshot.exists()) {
                try {
                    throw new Exception("Already signed up as an attendee.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            // Check if deviceId is already in waitlist
            DocumentSnapshot waitlistSnapshot = transaction.get(waitlistRef);
            if (waitlistSnapshot.exists()) {
                try {
                    throw new Exception("Already in waitlist.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (currentAttendees < maxAttendees) {
                // Add to Attendees subcollection
                Map<String, Object> attendeeData = new HashMap<>();
                attendeeData.put("deviceId", deviceId);
                attendeeData.put("userName", userName); // Store user name

                transaction.set(attendeeRef, attendeeData);

                // Increment currentAttendees
                transaction.update(eventRef, "currentAttendees", FieldValue.increment(1));
            } else {
                // Add to Waitlist subcollection
                Map<String, Object> waitlistData = new HashMap<>();
                waitlistData.put("deviceId", deviceId);
                waitlistData.put("userName", userName); // Store user name

                transaction.set(waitlistRef, waitlistData);
            }

            return null;
        }).addOnSuccessListener(aVoid -> {
            // After transaction success, update UI
            db.collection("Events").document(eventId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Long currentAttendeesLong = documentSnapshot.getLong("currentAttendees");
                            int currentAttendees = currentAttendeesLong != null ? currentAttendeesLong.intValue() : 0;
                            Long maxAttendeesLong = documentSnapshot.getLong("maxAttendees");
                            int maxAttendees = maxAttendeesLong != null ? maxAttendeesLong.intValue() : 0;

                            if (currentAttendees <= maxAttendees) {
                                Toast.makeText(this, "Successfully registered for the event!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Event full. You have been added to the waitlist.", Toast.LENGTH_SHORT).show();
                            }

                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error fetching event details.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error fetching event after transaction", e);
                    });

        }).addOnFailureListener(e -> {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("Already signed up as an attendee.")) {
                Toast.makeText(this, "You have already signed up for this event.", Toast.LENGTH_SHORT).show();
            } else if (errorMessage.contains("Already in waitlist.")) {
                Toast.makeText(this, "You are already in the waitlist for this event.", Toast.LENGTH_SHORT).show();
            } else if (errorMessage.contains("Event does not exist.")) {
                Toast.makeText(this, "Event does not exist.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error registering for event.", Toast.LENGTH_SHORT).show();
            }
            Log.e(TAG, "Transaction failure: " + e.getMessage());
            resetSignupButton();
        }).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
        });
    }

    /**
     * Resets the sign-up button and related UI elements after the sign-up operation.
     */
    private void resetSignupButton() {
        signupButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }
}
