package com.example.myapplication;

import com.example.myapplication.Models.User;
import com.example.myapplication.Controllers.EditProfileController;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.AvatarUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private Button saveChangesButton;

    private Uri imageUri;
    private EditProfileController controller;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        controller = new EditProfileController();

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
        saveChangesButton = findViewById(R.id.save_changes_button);

        // Buttons for admin-specific actions
        Button buttonBrowseUsers = findViewById(R.id.button_browse_user_profiles);
        Button buttonBrowseEvents = findViewById(R.id.button_browse_events);
        Button buttonBrowseFacilities = findViewById(R.id.button_browse_facilities);
        Button buttonBrowseImages = findViewById(R.id.button_browse_images);

        // Set listeners
        editProfileImageButton.setOnClickListener(v -> openFileChooser());
        saveChangesButton.setOnClickListener(v -> saveProfileData());
        backButton.setOnClickListener(v -> onBackPressed());
        removeProfileImageButton.setOnClickListener(v -> deleteProfileImage());

        buttonBrowseUsers.setOnClickListener(v -> BrowseUsers());
        buttonBrowseEvents.setOnClickListener(v -> BrowseEvents());
        buttonBrowseFacilities.setOnClickListener(v -> BrowseFacilities());
        buttonBrowseImages.setOnClickListener(v -> BrowseImages());



        setupDateOfBirthField();

        // Load user profile
        loadUserProfile();
    }

    private void BrowseUsers(){
        Intent intent = new Intent(this, BrowseUsersActivity.class);
        startActivity(intent);
    }

    private void BrowseEvents(){
        Intent intent = new Intent(this, BrowseEventsActivity.class);
        startActivity(intent);
    }

    private void BrowseFacilities(){
        Intent intent = new Intent(this, BrowseFacilitiesActivity.class);
        startActivity(intent);
    }

    private void BrowseImages(){
        Intent intent = new Intent(this, BrowseImagesActivity.class);
        startActivity(intent);
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

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
        dobField.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;

                String input = s.toString().replaceAll("[^\\d]", "");
                StringBuilder formattedDate = new StringBuilder();
                if (input.length() >= 2) formattedDate.append(input.substring(0, 2)).append("/");
                if (input.length() >= 4) formattedDate.append(input.substring(2, 4)).append("/");
                if (input.length() > 4) formattedDate.append(input.substring(4));

                isUpdating = true;
                dobField.setText(formattedDate.toString());
                dobField.setSelection(formattedDate.length());
                isUpdating = false;
            }
        });
    }

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

    private int calculateAge(Calendar selectedDate) {
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - selectedDate.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < selectedDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    private void deleteProfileImage() {
        controller.deleteProfileImage(new EditProfileController.EditProfileListener() {
            @Override
            public void onProfileLoaded(User user) {}

            @Override
            public void onProfileLoadFailed(Exception e) {}

            @Override
            public void onProfileSavedSuccessfully() {}

            @Override
            public void onProfileSaveFailed(Exception e) {}

            @Override
            public void onImageUploaded(String imageUrl) {}

            @Override
            public void onImageUploadFailed(Exception e) {}

            @Override
            public void onImageDeletedSuccessfully() {
                runOnUiThread(() -> {
                    Toast.makeText(AdminProfileActivity.this, "Profile image removed", Toast.LENGTH_SHORT).show();
                    profileImageView.setImageResource(R.drawable.ic_profile);
                    removeProfileImageButton.setVisibility(View.GONE);
                });
            }

            @Override
            public void onImageDeleteFailed(Exception e) {
                runOnUiThread(() -> Toast.makeText(AdminProfileActivity.this, "Failed to delete profile image", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadUserProfile() {
        controller.loadUserProfile(new EditProfileController.EditProfileListener() {
            @Override
            public void onProfileLoaded(User user) {
                runOnUiThread(() -> {
                    nameField.setText(user.getName());
                    emailField.setText(user.getEmail());
                    dobField.setText(user.getDob());
                    phoneField.setText(user.getPhone());

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            AdminProfileActivity.this, R.array.country_array, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    countrySpinner.setAdapter(adapter);
                    countrySpinner.setSelection(adapter.getPosition(user.getCountry()));

                    if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                        Glide.with(AdminProfileActivity.this)
                                .load(user.getProfileImageUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(profileImageView);
                        removeProfileImageButton.setVisibility(View.VISIBLE);
                    } else {
                        profileImageView.setImageResource(R.drawable.ic_profile);
                        removeProfileImageButton.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onProfileLoadFailed(Exception e) {
                runOnUiThread(() -> Toast.makeText(AdminProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onProfileSavedSuccessfully() {}

            @Override
            public void onProfileSaveFailed(Exception e) {}

            @Override
            public void onImageUploaded(String imageUrl) {}

            @Override
            public void onImageUploadFailed(Exception e) {}

            @Override
            public void onImageDeletedSuccessfully() {}

            @Override
            public void onImageDeleteFailed(Exception e) {}
        });
    }

    private void saveProfileData() {
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String dob = dobField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String country = countrySpinner.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || dob.isEmpty() || country.isEmpty()) {
            Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setDob(dob);
        user.setPhone(phone);
        user.setCountry(country);

        controller.saveUserProfile(user, imageUri, new EditProfileController.EditProfileListener() {
            @Override
            public void onProfileLoaded(User user) {}

            @Override
            public void onProfileLoadFailed(Exception e) {}

            @Override
            public void onProfileSavedSuccessfully() {
                runOnUiThread(() -> {
                    Toast.makeText(AdminProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    loadUserProfile();
                });
            }

            @Override
            public void onProfileSaveFailed(Exception e) {
                runOnUiThread(() -> Toast.makeText(AdminProfileActivity.this, "Failed to save profile", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onImageUploaded(String imageUrl) {}

            @Override
            public void onImageUploadFailed(Exception e) {}

            @Override
            public void onImageDeletedSuccessfully() {}

            @Override
            public void onImageDeleteFailed(Exception e) {}
        });
    }
}
