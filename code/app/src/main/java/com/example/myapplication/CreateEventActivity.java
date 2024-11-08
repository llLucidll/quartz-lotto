package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {
    private EditText eventNameEditText, dateEditText, timeEditText, descriptionEditText, maxAttendeesEditText, maxWaitlistEditText;
    private CheckBox geolocationCheckBox;
    private ImageView qrCodeImageView;
    private Button uploadPosterButton, saveButton, generateQRButton;
    private Uri posterUri;
    private FirebaseFirestore db;
    private ActivityResultLauncher<Intent> posterPickerLauncher;
    private String qrCodeLink; // Class-level variable to hold the QR code link

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event); // Ensure this is the correct layout file

        db = FirebaseFirestore.getInstance();

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
                    }
                }
        );

        uploadPosterButton.setOnClickListener(v -> openPosterPicker());

        // Set up listeners
        saveButton.setOnClickListener(view -> {
            saveEvent();
        });

        generateQRButton.setOnClickListener(view -> {
            if (qrCodeLink != null && !qrCodeLink.isEmpty()) {
                generateQRCode(qrCodeLink);
            } else {
                Toast.makeText(this, "Please save the event first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openPosterPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        posterPickerLauncher.launch(intent);
    }

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
     */
    private void generateQRCode(String qrCodeLink) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrCodeLink, BarcodeFormat.QR_CODE, 300, 300);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the event details to Firestore.
     */
    private void saveEvent() {
        String eventName = eventNameEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String maxAttendeesStr = maxAttendeesEditText.getText().toString();
        String maxWaitlistStr = maxWaitlistEditText.getText().toString();

        if (eventName.isEmpty() || date.isEmpty() || time.isEmpty() || description.isEmpty() || maxAttendeesStr.isEmpty()) {
            Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int maxAttendees = Integer.parseInt(maxAttendeesStr);
        Integer maxWaitlist = !maxWaitlistStr.isEmpty() ? Integer.parseInt(maxWaitlistStr) : null;
        boolean geolocationEnabled = geolocationCheckBox.isChecked();

        CollectionReference eventsRef = db.collection("Events");
        String eventId = eventsRef.document().getId(); // Generate event ID

        // Generate QR code link using eventId
        qrCodeLink = "eventapp://event/" + eventId;

        Map<String, Object> event = new HashMap<>();
        event.put("eventName", eventName);
        event.put("description", description);
        event.put("drawDate", date);
        event.put("eventDateTime", time);
        event.put("maxAttendees", maxAttendees);
        event.put("maxOnWaitList", maxWaitlist);
        event.put("geolocationEnabled", geolocationEnabled);
        event.put("qrCodeLink", qrCodeLink); // Save the QR code link
        event.put("eventId", eventId); // Save the eventId for future reference

        eventsRef.document(eventId).set(event)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Event saved successfully.");
                    if (posterUri != null) {
                        uploadPoster(eventId);
                    }
                    Toast.makeText(this, "Event saved successfully", Toast.LENGTH_SHORT).show();
                    // Optionally, generate the QR code immediately after saving
                    generateQRCode(qrCodeLink);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error saving event", e);
                    Toast.makeText(this, "Error saving event", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadPoster(String eventId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("posters/" + eventId + ".jpg");
        storageRef.putFile(posterUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    db.collection("Events").document(eventId).update("posterUrl", uri.toString())
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Poster URL updated successfully"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating poster URL", e));
                }))
                .addOnFailureListener(e -> Log.e("Storage", "Error uploading poster", e));
    }
}
