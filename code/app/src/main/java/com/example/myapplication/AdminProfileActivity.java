package com.example.myapplication;

import com.example.myapplication.Models.User;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.AvatarUtil;
import com.example.myapplication.BaseActivity;
import com.example.myapplication.BrowseFacilitiesActivity;
import com.example.myapplication.BrowseUsersActivity;
import com.example.myapplication.EditProfileActivity;
import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * AdminProfileActivity allows admins to view and edit their profiles.
 * It extends BaseActivity to utilize dynamic user identification.
 */
public class AdminProfileActivity extends BaseActivity {

    private static final int MIN_AGE = 1;
    private static final int MAX_AGE = 100;

    private ImageButton removeProfileImageButton;
    private CircleImageView profileImageView;
    private EditText nameField, emailField, dobField, phoneField;
    private Spinner countrySpinner;

    private Uri imageUri;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private String deviceId;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    Glide.with(this)
                            .load(imageUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profileImageView);

                    removeProfileImageButton.setVisibility(View.VISIBLE);

                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Retrieve deviceId from the parent activity
        deviceId = retrieveDeviceId();
        if (deviceId == null) {
            Toast.makeText(this, "Device ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI elements
        profileImageView = findViewById(R.id.profile_image);
        ImageButton editProfileImageButton = findViewById(R.id.edit_profile_image_button);
        ImageButton backButton = findViewById(R.id.back_button);
        nameField = findViewById(R.id.name_field);
        emailField = findViewById(R.id.email_field);
        dobField = findViewById(R.id.dob_field);
        phoneField = findViewById(R.id.phone_field);
        countrySpinner = findViewById(R.id.country_spinner);
        removeProfileImageButton = findViewById(R.id.remove_profile_image_button);

        Button buttonSwitchAttendee = findViewById(R.id.buttonSwitchAttendee);
        Button browseUserProfilesButton = findViewById(R.id.button_browse_user_profiles);
        Button browseFacilitiesButton = findViewById(R.id.button_browse_facilities);
        Button browseImagesButton = findViewById(R.id.button_browse_images);
        Button browseEventsButton = findViewById(R.id.button_browse_events);
        Button saveChangesButton = findViewById(R.id.save_changes_button);


        // Set listeners
        editProfileImageButton.setOnClickListener(v -> openFileChooser());
        saveChangesButton.setOnClickListener(v -> saveProfileData());
        backButton.setOnClickListener(v -> onBackPressed());
        removeProfileImageButton.setOnClickListener(v -> removeProfileImage());
        buttonSwitchAttendee.setOnClickListener(v -> switchProfileAttendee());

        browseUserProfilesButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProfileActivity.this, BrowseUsersActivity.class);
            startActivity(intent);
        });

        browseFacilitiesButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProfileActivity.this, BrowseFacilitiesActivity.class);
            startActivity(intent);
        });

        browseEventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProfileActivity.this, BrowseEventsActivity.class);
            startActivity(intent);
        });


        browseImagesButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProfileActivity.this, BrowseImagesActivity.class);
            startActivity(intent);
        });

        setupDOBInputRestrictions();
        setupDateOfBirthField();

        setupNameFieldTextWatcher();

        loadUserProfile();
    }

    /**
     * Opens the file chooser to select a profile image.
     */
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    /**
     * Sets up input restrictions for the Date of Birth field.
     */
    private void setupDOBInputRestrictions() {
        dobField.setInputType(InputType.TYPE_CLASS_DATETIME);
        dobField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_calendar, 0);
        dobField.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (dobField.getRight() - dobField.getCompoundDrawables()[2].getBounds().width())) {
                    showDatePicker();
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * Sets up the Date of Birth field with a DatePicker.
     */
    private void setupDateOfBirthField() {
        dobField.setInputType(InputType.TYPE_CLASS_DATETIME);
        dobField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_calendar, 0);
        dobField.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (dobField.getRight() - dobField.getCompoundDrawables()[2].getBounds().width())) {
                    showDatePicker();
                    return true;
                }
            }
            return false;
        });
        dobField.addTextChangedListener(new DateOfBirthTextWatcher());
    }

    /**
     * Displays a DatePickerDialog for selecting Date of Birth.
     */
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, month1, dayOfMonth);
                    int age = calculateAge(selectedDate);

                    if (age >= MIN_AGE && age <= MAX_AGE) {
                        String formattedDate = String.format(Locale.US, "%02d/%02d/%04d", month1 + 1, dayOfMonth, year1);
                        dobField.setText(formattedDate);
                        dobField.setError(null);
                    } else {
                        dobField.setError("Age must be between " + MIN_AGE + " and " + MAX_AGE);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    /**
     * Calculates the age based on the selected date.
     *
     * @param selectedDate The selected birth date.
     * @return The calculated age.
     */
    private int calculateAge(Calendar selectedDate) {
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - selectedDate.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < selectedDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    /**
     * TextWatcher for formatting and validating the Date of Birth field.
     */
    private class DateOfBirthTextWatcher implements TextWatcher {
        private boolean isUpdating;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (isUpdating) return;
            isUpdating = true;

            String input = s.toString().replaceAll("[^\\d]", "");

            if (input.length() > 8) {
                input = input.substring(0, 8);
            }

            String formattedDate = formatDate(input);
            dobField.setText(formattedDate);
            dobField.setSelection(formattedDate.length());

            if (formattedDate.length() == 10) {
                if (!isDOBValid(formattedDate)) {
                    dobField.setError("Invalid date or age not between " + MIN_AGE + " and " + MAX_AGE);
                } else {
                    dobField.setError(null); // Clear error if valid
                }
            } else {
                dobField.setError(null); // Clear error if incomplete
            }

            isUpdating = false;
        }

        /**
         * Formats the input string into MM/DD/YYYY format.
         *
         * @param input The raw input string.
         * @return The formatted date string.
         */
        private String formatDate(String input) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < input.length() && i < 8; i++) {
                sb.append(input.charAt(i));
                if (i == 1 || i == 3) {
                    sb.append('/');
                }
            }

            return sb.toString();
        }
    }

    /**
     * Sets up a TextWatcher to update the profile picture based on the first letter of the name.
     */
    private void setupNameFieldTextWatcher() {
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateProfilePictureBasedOnFirstLetter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Updates the profile picture based on the first letter of the user's name.
     *
     * @param name The user's name.
     */
    private void updateProfilePictureBasedOnFirstLetter(String name) {
        if (!name.isEmpty()) {
            String firstLetter = String.valueOf(name.charAt(0)).toUpperCase(Locale.US);
            Bitmap avatarBitmap = AvatarUtil.generateAvatar(firstLetter, 200, this);
            profileImageView.setImageBitmap(avatarBitmap);
            removeProfileImageButton.setVisibility(View.GONE);
        } else {
            profileImageView.setImageResource(R.drawable.ic_profile);
            removeProfileImageButton.setVisibility(View.GONE);
        }
    }

    /**
     * Loads the admin profile using the `deviceId`.
     */
    private void loadUserProfile() {
        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            nameField.setText(user.getName());
                            emailField.setText(user.getEmail());
                            dobField.setText(user.getDob());
                            phoneField.setText(user.getPhone());

                            if (user.getCountry() != null) {
                                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                        this, R.array.country_array, android.R.layout.simple_spinner_item);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                countrySpinner.setAdapter(adapter);
                                int position = adapter.getPosition(user.getCountry());
                                countrySpinner.setSelection(position);
                            }

                            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                                Glide.with(this)
                                        .load(user.getProfileImageUrl())
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(profileImageView);
                            }
                        }
                    } else {
                        Toast.makeText(this, "No profile found. Please create your profile.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Generates a default avatar based on the first letter of the user's name.
     *
     * @param name The user's name.
     */
    private void generateDefaultAvatar(String name) {
        if (name != null && !name.isEmpty()) {
            String firstLetter = String.valueOf(name.charAt(0)).toUpperCase(Locale.US);
            Bitmap avatarBitmap = AvatarUtil.generateAvatar(firstLetter, 200, this);
            profileImageView.setImageBitmap(avatarBitmap);
        } else {
            profileImageView.setImageResource(R.drawable.ic_profile);
        }
        removeProfileImageButton.setVisibility(View.GONE);
    }

    /**
     * Removes the profile image from Firebase Storage and updates Firestore.
     */
    private void removeProfileImage() {
        StorageReference storageRef = storage.getReference("profile_images/" + deviceId + ".jpg");
        storageRef.delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Profile image removed", Toast.LENGTH_SHORT).show();
            DocumentReference userProfileRef = db.collection("Admin").document(deviceId);
            userProfileRef.update("profileImageUrl", FieldValue.delete())
                    .addOnSuccessListener(aVoid1 -> loadUserProfile())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update Firestore", Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to delete profile image", Toast.LENGTH_SHORT).show());
    }


    /**
     * Saves the admin profile data to Firestore.
     */
    private void saveProfileData() {
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String dob = dobField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String country = countrySpinner.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || dob.isEmpty() || country.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(deviceId, name, null, email, dob, phone, country, true);

        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.set(user)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        if (imageUri != null) {
            StorageReference imageRef = storage.getReference("profile_images/" + deviceId + ".jpg");
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        userRef.update("profileImageUrl", uri.toString())
                                .addOnSuccessListener(aVoid -> loadUserProfile())
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile image URL.", Toast.LENGTH_SHORT).show());
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload profile image.", Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * Validates the Date of Birth format and age range.
     *
     * @param dob The Date of Birth string.
     * @return True if valid, false otherwise.
     */
    private boolean isDOBValid(String dob) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        sdf.setLenient(false);
        Date date;

        try {
            date = sdf.parse(dob);
        } catch (ParseException e) {
            return false;
        }

        if (date == null) {
            return false;
        }

        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(date);

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age >= MIN_AGE && age <= MAX_AGE;
    }

    /**
     * Switches the profile to attendee mode.
     */
    private void switchProfileAttendee() {
        Intent intent = new Intent(AdminProfileActivity.this, OrganizerProfileActivity.class);
        startActivity(intent);
    }
}
