package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OrganizerProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserProfilePrefs";
    private SharedPreferences sharedPreferences;

    // UI elements
    private EditText editTextName, editTextEmail, editTextDOB, editTextContact;
    private Button buttonSave, buttonManageNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_profile); // Ensure layout name is correct

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize UI elements
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextDOB = findViewById(R.id.editTextDOB);
        editTextContact = findViewById(R.id.editTextContact);
        buttonSave = findViewById(R.id.buttonSave);
        buttonManageNotifications = findViewById(R.id.buttonManageNotifications); // New button for managing notifications

        // Load existing profile information
        loadUserProfile();

        // Set up the save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });

        // Set up the manage notifications button
        buttonManageNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openManageNotifications();
            }
        });
    }

    private void loadUserProfile() {
        // Retrieve data from SharedPreferences
        String name = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email", "");
        String dob = sharedPreferences.getString("dob", "");
        String contact = sharedPreferences.getString("contact", "");

        // Populate UI elements with user data
        editTextName.setText(name);
        editTextEmail.setText(email);
        editTextDOB.setText(dob);
        editTextContact.setText(contact);
    }

    private void saveUserProfile() {
        // Get data from UI elements
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String dob = editTextDOB.getText().toString().trim();
        String contact = editTextContact.getText().toString().trim();

        // Validate input data
        if (name.isEmpty() || email.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save data to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("dob", dob);
        editor.putString("contact", contact);
        editor.apply();

        // Show a success message
        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
    }

    // Method to open ManageNotificationGroupsActivity
    private void openManageNotifications() {
        Intent intent = new Intent(OrganizerProfileActivity.this, ManageNotificationGroupsActivity.class);
        startActivity(intent);
    }
}
