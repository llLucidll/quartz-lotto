package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EntrantProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserProfilePrefs";
    private SharedPreferences sharedPreferences;

    // UI elements
    private EditText editTextName, editTextEmail, editTextDOB, editTextContact;
    private Button buttonSave;
    private CheckBox checkBoxNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_profile);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize UI elements
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextDOB = findViewById(R.id.editTextDOB);
        editTextContact = findViewById(R.id.editTextContact);
        buttonSave = findViewById(R.id.buttonSave);
        checkBoxNotifications = findViewById(R.id.checkBoxNotifications);

        // Load existing profile information
        loadUserProfile();

        // Set up the save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void loadUserProfile() {
        // Retrieve data from SharedPreferences
        String name = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email", "");
        String dob = sharedPreferences.getString("dob", "");
        String contact = sharedPreferences.getString("contact", "");
        boolean receiveNotifications = sharedPreferences.getBoolean("receiveNotifications", true); // Default to true

        // Populate UI elements with user data
        editTextName.setText(name);
        editTextEmail.setText(email);
        editTextDOB.setText(dob);
        editTextContact.setText(contact);

        // Set CheckBox based on notification preference
        checkBoxNotifications.setChecked(receiveNotifications);
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

        // Retrieve the CheckBox state before saving
        // Sending/receiving notification
        boolean receiveNotifications = checkBoxNotifications.isChecked();
        Log.d("EntrantProfileActivity", "Saving notification preference: " + receiveNotifications); // Debug log

        // Save data to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("dob", dob);
        editor.putString("contact", contact);
        editor.putBoolean("receiveNotifications", receiveNotifications); // Save notification preference
        editor.apply(); // Apply changes asynchronously

        // Show a success message
        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
    }
}
