package com.example.myapplication.Views;

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
import com.example.myapplication.BaseActivity;
import com.example.myapplication.Controllers.EditProfileController;
import com.example.myapplication.GroupEntrantsActivity;
import com.example.myapplication.Models.User;
import com.example.myapplication.OrganizerNotificationActivity;
import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * OrganizerProfileView allows organizers to view and edit their profiles.
 */
public class OrganizerProfileView extends BaseActivity {

    private static final int MIN_AGE = 1;
    private static final int MAX_AGE = 100;

    private CircleImageView profileImageView;
    private ImageButton editProfileImageButton, backButton, removeProfileImageButton;
    private EditText nameField, emailField, dobField, phoneField;
    private Spinner countrySpinner;
    private Button saveChangesButton, manageFacilityButton, myEvents, notifGroups;

    private Uri imageUri;
    private EditProfileController controller;
    private String userId; // Add userId field

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
        setContentView(R.layout.activity_edit_organizer_profile);

        controller = new EditProfileController();
        userId = retrieveDeviceId(); // Initialize userId

        initializeUI();
        setListeners();

        loadUserProfile();
    }

    private void initializeUI() {
        profileImageView = findViewById(R.id.profile_image);
        editProfileImageButton = findViewById(R.id.edit_profile_image_button);
        backButton = findViewById(R.id.back_button);
        nameField = findViewById(R.id.name_field);
        emailField = findViewById(R.id.email_field);
        dobField = findViewById(R.id.dob_field);
        phoneField = findViewById(R.id.phone_field);
        countrySpinner = findViewById(R.id.country_spinner);
        saveChangesButton = findViewById(R.id.save_changes_button);
        removeProfileImageButton = findViewById(R.id.remove_profile_image_button);
        manageFacilityButton = findViewById(R.id.manage_facility_button);
        myEvents = findViewById(R.id.my_events_button);
        notifGroups =findViewById(R.id.notif_groups);

        setupDateOfBirthField();
    }

    private void setListeners() {
        editProfileImageButton.setOnClickListener(v -> openFileChooser());
        saveChangesButton.setOnClickListener(v -> saveProfileData());
        backButton.setOnClickListener(v -> finish());
        removeProfileImageButton.setOnClickListener(v -> deleteProfileImage());
        manageFacilityButton.setOnClickListener(v -> startActivity(new Intent(this, AddFacilityView.class)));
        myEvents.setOnClickListener(v -> startActivity(new Intent(this, HomeView.class)));
        notifGroups.setOnClickListener(v -> startActivity(new Intent(this, OrganizerNotificationActivity.class)));

    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void loadUserProfile() {
        controller.loadUserProfile(new EditProfileController.EditProfileListener() {
            @Override
            public void onProfileLoaded(User user) {
                runOnUiThread(() -> populateUIWithUserData(user));
            }

            @Override
            public void onProfileLoadFailed(Exception e) {
                runOnUiThread(() -> Toast.makeText(OrganizerProfileView.this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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

    private void populateUIWithUserData(User user) {
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        dobField.setText(user.getDob());
        phoneField.setText(user.getPhone());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.country_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(user.getCountry());
        countrySpinner.setSelection(spinnerPosition);

        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getProfileImageUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImageView);
            removeProfileImageButton.setVisibility(View.VISIBLE);
        } else {
            generateDefaultAvatar(user.getName());
        }
    }

    private void saveProfileData() {
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String dob = dobField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String country = countrySpinner.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || dob.isEmpty() || country.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(userId, name, null, email, dob, phone, country, true);

        controller.saveUserProfile(user, imageUri, new EditProfileController.EditProfileListener() {
            @Override
            public void onProfileSavedSuccessfully() {
                runOnUiThread(() -> Toast.makeText(OrganizerProfileView.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onProfileSaveFailed(Exception e) {
                runOnUiThread(() -> Toast.makeText(OrganizerProfileView.this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onImageUploaded(String imageUrl) {}

            @Override
            public void onImageUploadFailed(Exception e) {
                runOnUiThread(() -> Toast.makeText(OrganizerProfileView.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onProfileLoaded(User user) {}

            @Override
            public void onProfileLoadFailed(Exception e) {}

            @Override
            public void onImageDeletedSuccessfully() {}

            @Override
            public void onImageDeleteFailed(Exception e) {}
        });
    }

    private void deleteProfileImage() {
        controller.deleteProfileImage(new EditProfileController.EditProfileListener() {
            @Override
            public void onImageDeletedSuccessfully() {
                runOnUiThread(() -> {
                    Toast.makeText(OrganizerProfileView.this, "Profile image deleted.", Toast.LENGTH_SHORT).show();
                    loadUserProfile();
                });
            }

            @Override
            public void onImageDeleteFailed(Exception e) {
                runOnUiThread(() -> Toast.makeText(OrganizerProfileView.this, "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

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
        });
    }

    private void setupDateOfBirthField() {
        dobField.setInputType(InputType.TYPE_CLASS_NUMBER);
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

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String date = String.format(Locale.US, "%02d/%02d/%04d", month1 + 1, dayOfMonth, year1);
            dobField.setText(date);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void generateDefaultAvatar(String name) {
        if (name != null && !name.isEmpty()) {
            String firstLetter = name.substring(0, 1).toUpperCase(Locale.US);
            Bitmap avatar = AvatarUtil.generateAvatar(firstLetter, 200, this);
            profileImageView.setImageBitmap(avatar);
            removeProfileImageButton.setVisibility(View.GONE);
        } else {
            profileImageView.setImageResource(R.drawable.ic_profile);
            removeProfileImageButton.setVisibility(View.GONE);
        }
    }
}
