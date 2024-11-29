package com.example.myapplication;

import static android.app.PendingIntent.getActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import com.example.myapplication.Models.Event;
import com.example.myapplication.Models.Facility;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity allowing organizers to create events and generate QR codes.
 */
public class CreateEventActivity extends BaseActivity { // Extends BaseActivity

    private EditText eventNameEditText, dateEditText, timeEditText, descriptionEditText, maxAttendeesEditText, maxWaitlistEditText;
    private CheckBox geolocationCheckBox;
    private ImageView qrCodeImageView;
    private Button uploadPosterButton, saveButton, generateQRButton;
    private Uri posterUri;
    private ActivityResultLauncher<Intent> posterPickerLauncher;
    private String qrCodeLink; // Class-level variable to hold the QR code link
    private ProgressDialog progressDialog;
    private boolean isSaving = false; // Flag to track if save is in progress

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        // Initialize views
        eventNameEditText = findViewById(R.id.eventNameEditText);
        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        maxAttendeesEditText = findViewById(R.id.maxAttendeesEditText);
        maxWaitlistEditText = findViewById(R.id.maxWaitlistEditText);
        geolocationCheckBox = findViewById(R.id.geolocationCheckBox);
        saveButton = findViewById(R.id.saveButton);
        generateQRButton = findViewById(R.id.generateQRButton);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        uploadPosterButton = findViewById(R.id.uploadPosterButton);

        dateEditText.setOnClickListener(v -> showDatePicker());
        timeEditText.setOnClickListener(v -> showTimePicker());

        // Initialize the poster picker launcher
        posterPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        posterUri = result.getData().getData();
                        Toast.makeText(this, "Poster selected", Toast.LENGTH_SHORT).show();
                        // Optionally, display the selected poster
                        qrCodeImageView.setImageURI(posterUri);
                        uploadPosterButton.setText("Update Poster");
                    }
                }
        );

        // Allow updating the poster
        uploadPosterButton.setOnClickListener(v -> {
            if (posterUri != null) {
                new AlertDialog.Builder(this)
                        .setTitle("Update Poster")
                        .setMessage("A poster is already selected. Do you want to replace it?")
                        .setPositiveButton("Yes", (dialog, which) -> openPosterPicker())
                        .setNegativeButton("No", null)
                        .show();
            } else {
                openPosterPicker();
            }
        });

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving event...");
        progressDialog.setCancelable(false);

        // Set up listeners
        saveButton.setOnClickListener(view -> {
            if (!isSaving) { // Prevent multiple saves
                saveEvent();
            }
        });

        generateQRButton.setOnClickListener(view -> {
            if (qrCodeLink != null && !qrCodeLink.isEmpty()) {
                generateQRCode(qrCodeLink);
            } else {
                Toast.makeText(this, "Please save the event first", Toast.LENGTH_SHORT).show();
            }
        });

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish(); // Close the activity and go back
        });

        // Optionally, set up action bar back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back button in the action bar
        if (item.getItemId() == android.R.id.home) {
            if (!isSaving) { // Prevent closing during save
                finish(); // Close the activity and go back
            } else {
                Toast.makeText(this, "Please wait until the event is saved.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens the poster picker to allow the organizer to select an image.
     */
    private void openPosterPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        posterPickerLauncher.launch(intent);
    }

    /**
     * Shows a DatePickerDialog for selecting the event date.
     */
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    String formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                    dateEditText.setText(formattedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }

    /**
     * Shows a TimePickerDialog for selecting the event time.
     */
    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minuteOfHour) -> {
                    String formattedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    timeEditText.setText(formattedTime);
                },
                hour, minute, true);
        timePickerDialog.show();
    }

    /**
     * Generates a QR code for the event and displays it in the ImageView.
     *
     * @param qrCodeLink The data to encode in the QR code.
     */
    private void generateQRCode(String qrCodeLink) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrCodeLink, BarcodeFormat.QR_CODE, 300, 300);
            qrCodeImageView.setImageBitmap(bitmap);
            qrCodeImageView.setVisibility(View.VISIBLE); // Make the QR code visible
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Saves the event details to Firestore.
     */
    private void saveEvent() {
        isSaving = true; // Set the flag to indicate saving is in progress
        saveButton.setEnabled(false); // Disable the save button
        generateQRButton.setEnabled(false); // Optionally disable the QR button

        String eventName = eventNameEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String maxAttendeesStr = maxAttendeesEditText.getText().toString().trim();
        String maxWaitlistStr = maxWaitlistEditText.getText().toString().trim();

        // Validate required fields
        if (eventName.isEmpty() || date.isEmpty() || time.isEmpty() || description.isEmpty() || maxAttendeesStr.isEmpty()) {
            Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            resetSaveButton();
            return;
        }

        // Parse and validate maxAttendees
        int maxAttendees;
        try {
            maxAttendees = Integer.parseInt(maxAttendeesStr);
            if (maxAttendees <= 0) {
                Toast.makeText(this, "Max attendees must be a positive number.", Toast.LENGTH_SHORT).show();
                resetSaveButton();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number for max attendees.", Toast.LENGTH_SHORT).show();
            resetSaveButton();
            return;
        }

        // Parse and validate maxWaitlist if provided
        Integer maxWaitlist = null;
        if (!maxWaitlistStr.isEmpty()) {
            try {
                maxWaitlist = Integer.parseInt(maxWaitlistStr);
                if (maxWaitlist <= 0) {
                    Toast.makeText(this, "Max waitlist must be a positive number.", Toast.LENGTH_SHORT).show();
                    resetSaveButton();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number for max waitlist.", Toast.LENGTH_SHORT).show();
                resetSaveButton();
                return;
            }
        }

        boolean geolocationEnabled = geolocationCheckBox.isChecked();


        // Retrieve the current user's device ID
        String organizerId = retrieveDeviceId(); // Changed from getUserId() to retrieveDeviceId()
        if (organizerId == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            resetSaveButton();
            return;
        }

        // Show ProgressDialog
        progressDialog.show();

        // Generate a unique event ID
        String eventId = db.collection("Events").document().getId();

        // Generate QR code link using eventId
        qrCodeLink = "eventapp://event/" + eventId;

        // Create Facility object if needed, else set to null
        Facility facility = null; // Adjust as per your application's logic

        // Create Event object
        Event event = new Event(eventId, eventName, date, time, description,
                maxAttendees, maxWaitlist, geolocationEnabled, qrCodeLink, "", 0, organizerId, facility);

        // Save event to Firestore
        db.collection("Events").document(eventId).set(event)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Event saved successfully.");
                    Toast.makeText(this, "Event saved successfully", Toast.LENGTH_SHORT).show();
                    // Upload poster if selected
                    if (posterUri != null) {
                        uploadPoster(eventId);
                    }
                    // Generate the QR code after saving
                    generateQRCode(qrCodeLink);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error saving event", e);
                    Toast.makeText(this, "Error saving event", Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> {
                    // Reset the save button and flag
                    resetSaveButton();
                });
    }


    /**
     * Resets the save button and related UI elements after the save operation.
     */
    private void resetSaveButton() {
        isSaving = false;
        saveButton.setEnabled(true);
        generateQRButton.setEnabled(true);
        progressDialog.dismiss();
    }

    /**
     * Uploads the poster image to Firebase Storage and updates the event document with the poster URL.
     *
     * @param eventId The unique ID of the event.
     */
    private void uploadPoster(String eventId) {
        if (posterUri == null) {
            return; // No poster to upload
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("posters/" + eventId + ".jpg");

        storageRef.putFile(posterUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    db.collection("Events").document(eventId).update("posterUrl", uri.toString())
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Poster URL updated successfully"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating poster URL", e));
                }))
                .addOnFailureListener(e -> Log.e("Storage", "Error uploading poster", e))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Poster uploaded successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to upload poster.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}