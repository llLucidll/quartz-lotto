package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
Activity view for creating an event.
 */
public class CreateEventActivity extends AppCompatActivity {
    private EditText eventNameEditText, dateEditText, timeEditText, descriptionEditText, maxAttendeesEditText, maxWaitlistEditText;
    private CheckBox geolocationCheckBox;
    private ImageView qrCodeImageView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        db = FirebaseFirestore.getInstance();

        eventNameEditText = findViewById(R.id.eventNameEditText);
        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        maxAttendeesEditText = findViewById(R.id.maxAttendeesEditText);
        maxWaitlistEditText = findViewById(R.id.maxWaitlistEditText);
        geolocationCheckBox = findViewById(R.id.geolocationCheckBox);
        Button saveButton = findViewById(R.id.saveButton);
        Button generateQRButton = findViewById(R.id.generateQRButton);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);

        generateQRButton.setOnClickListener(view -> generateQRCode());
        //saveButton.setOnClickListener(view -> saveEvent());
        saveButton.setOnClickListener(view -> {
            saveEvent();
            finish();
        });
    }

    /**
     * Generates a QR code for the event and displays it in the ImageView.
     */
    private void generateQRCode() {
        String eventName = eventNameEditText.getText().toString();
        if (eventName.isEmpty()) {
            Toast.makeText(this, "Event name is required to generate a QR code", Toast.LENGTH_SHORT).show();
            return;
        }

        String qrCodeLink = "https://example.com/event/" + eventName.replace(" ", "_");

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
        int maxAttendees = Integer.parseInt(maxAttendeesEditText.getText().toString());
        Integer maxWaitlist = !maxWaitlistEditText.getText().toString().isEmpty() ? Integer.parseInt(maxWaitlistEditText.getText().toString()) : null;
        boolean geolocationEnabled = geolocationCheckBox.isChecked();
        String qrCodeLink = "https://example.com/qr/" + eventName;

        // Generate unique IDs for the event and its waitlist
        CollectionReference eventsRef = db.collection("Events");
        String eventId = eventsRef.document().getId(); // Generate event ID
        String waitlistId = db.collection("Waitlists").document().getId(); // Generate waitlist ID

        // Create a map to store the event details
        Map<String, Object> event = new HashMap<>();
        event.put("eventName", eventName);
        event.put("description", description);
        event.put("drawDate", date);
        event.put("eventDateTime", time);
        event.put("maxAttendees", maxAttendees);
        event.put("maxOnWaitList", maxWaitlist);
        event.put("geolocationEnabled", geolocationEnabled);
        event.put("qrHash", qrCodeLink);
        event.put("waitlist_id", waitlistId);

        // Save the event to Firestore
        eventsRef.document(eventId).set(event)
                .addOnSuccessListener(aVoid -> {
                    // Event saved successfully, now create a new waitlist document
                    Map<String, Object> emptyWaitlist = new HashMap<>();
                    // Add initial empty data as required
                    db.collection("Waitlists").document(waitlistId).set(emptyWaitlist)
                            .addOnSuccessListener(waitlistVoid -> {
                                // Waitlist created successfully
                                Log.d("Firestore", "Event and empty waitlist saved successfully.");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "Error saving waitlist", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error saving event", e);
                });
    }
}
