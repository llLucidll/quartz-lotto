package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EntrantProfileActivity extends AppCompatActivity {

    private static final String TAG = "EntrantProfileActivity";
    private static final String PREFS_NAME = "UserProfilePrefs";

    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;

    // UI elements
    private EditText editTextName, editTextEmail, editTextDOB, editTextContact;
    private Timestamp dobTimestamp;  // For storing selected date as Timestamp
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_profile);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize UI elements
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextDOB = findViewById(R.id.editTextDOB);
        editTextDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        editTextContact = findViewById(R.id.editTextContact);
        buttonSave = findViewById(R.id.buttonSave);

        // Load profile data from Firestore
        loadUserProfileFromFirestore();

        // Set up save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void loadUserProfileFromFirestore() {
        // Reference the specific document in Firestore
        DocumentReference docRef = db.collection("Organizers").document("Organizer1");

        // Fetch the document
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Retrieve fields from the document
                    String name = document.getString("Name");
                    String email = document.getString("Email");
                    Timestamp dobTimestamp = document.getTimestamp("Date of Birth");
                    Long contact = document.getLong("Contact #");
                    Boolean notifications = document.getBoolean("Notifications");

                    // Format Date of Birth (Timestamp) to ignore time
                    String dob = "";
                    if (dobTimestamp != null) {
                        Date dobDate = dobTimestamp.toDate();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
                        dob = dateFormat.format(dobDate);  // Format only date
                    }

                    // Display data in UI
                    editTextName.setText(name);
                    editTextEmail.setText(email);
                    editTextDOB.setText(dob);
                    editTextContact.setText(contact != null ? contact.toString() : "");

                    Toast.makeText(this, "Profile loaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Document not found");
                    Toast.makeText(this, "Profile data not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Error fetching document", task.getException());
                Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Set the selected date on the EditText
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    dobTimestamp = new Timestamp(calendar.getTime());
                    editTextDOB.setText(new SimpleDateFormat("MMMM d, yyyy", Locale.US).format(calendar.getTime()));
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void saveUserProfile() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String contact = editTextContact.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || dobTimestamp == null || contact.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Long contactNumber;
        try {
            contactNumber = Long.parseLong(contact);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid contact number", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("Name", name);
        userProfile.put("Email", email);
        userProfile.put("Date of Birth", dobTimestamp);
        userProfile.put("Contact #", contactNumber);
        userProfile.put("Notifications", true);

        DocumentReference docRef = db.collection("Organizers").document("Organizer1");

        docRef.set(userProfile).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error saving profile", e);
            Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show();
        });
    }

}
