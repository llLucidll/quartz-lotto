package com.example.myapplication;


import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Bundle;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;



public class CreateEventActivity extends AppCompatActivity {
    private EditText eventNameEditText, dateEditText, timeEditText, descriptionEditText, maxAttendeesEditText, maxWaitlistEditText;
    private CheckBox geolocationCheckBox;
    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        // Initialize UI elements
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

        // Generate QR Code button action
        generateQRButton.setOnClickListener(view -> generateQRCode());

        // Save button action
        saveButton.setOnClickListener(view -> saveEvent());
    }
    private void generateQRCode() {
        String eventName = eventNameEditText.getText().toString();
        if (eventName.isEmpty()) {
            Toast.makeText(this, "Event name is required to generate a QR code", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique link based on the event name
        String qrCodeLink = "https://example.com/event/" + eventName.replace(" ", "_"); // Replace spaces with underscores for URL

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrCodeLink, BarcodeFormat.QR_CODE, 300, 300);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveEvent() {
        String eventName = eventNameEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        int maxAttendees = Integer.parseInt(maxAttendeesEditText.getText().toString());
        Integer maxWaitlist = !maxWaitlistEditText.getText().toString().isEmpty() ?
                Integer.parseInt(maxWaitlistEditText.getText().toString()) : null;
        boolean geolocationEnabled = geolocationCheckBox.isChecked();
        String qrCodeLink = "https://example.com/qr/" + eventName; // Placeholder link

        Event event = new Event(eventName, date, time, description, maxAttendees, maxWaitlist, geolocationEnabled, qrCodeLink);

        // Passing event data back to HomePageActivity
        Intent intent = new Intent();
        intent.putExtra("eventName", eventName);
        setResult(RESULT_OK, intent);
        finish();
    }

}

