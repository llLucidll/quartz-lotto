package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

public class EditProfileActivity extends BaseActivity {

    private static final int MIN_AGE = 1;
    private static final int MAX_AGE = 100;

    private ImageButton removeProfileImageButton;
    private CircleImageView profileImageView;
    private ImageButton editProfileImageButton, backButton;
    private EditText nameField, emailField, dobField, phoneField;
    private Spinner countrySpinner;
    private Switch notificationsSwitch;
    private Button saveChangesButton;

    private Uri imageUri;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private String userId;

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
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        userId = retrieveDeviceId();

        if (userId == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeUIElements();
        setupListeners();
        setupDateOfBirthField();
        loadUserProfile();
    }

    private void initializeUIElements() {
        profileImageView = findViewById(R.id.profile_image);
        editProfileImageButton = findViewById(R.id.edit_profile_image_button);
        backButton = findViewById(R.id.back_button);
        nameField = findViewById(R.id.name_field);
        emailField = findViewById(R.id.email_field);
        dobField = findViewById(R.id.dob_field);
        phoneField = findViewById(R.id.phone_field);
        countrySpinner = findViewById(R.id.country_spinner);
        notificationsSwitch = findViewById(R.id.notifications_switch);
        saveChangesButton = findViewById(R.id.save_changes_button);
        removeProfileImageButton = findViewById(R.id.remove_profile_image_button);
    }

    private void setupListeners() {
        editProfileImageButton.setOnClickListener(v -> openFileChooser());
        saveChangesButton.setOnClickListener(v -> saveProfileData());
        backButton.setOnClickListener(v -> finish());
        removeProfileImageButton.setOnClickListener(v -> removeProfileImage());

        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateProfileAvatar();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateProfileAvatar() {
        String name = nameField.getText() != null ? nameField.getText().toString().trim() : "";
        if (!name.isEmpty()) {
            String firstLetter = String.valueOf(name.charAt(0)).toUpperCase(Locale.US);
            Bitmap avatarBitmap = AvatarUtil.generateAvatar(firstLetter, 200, this);
            profileImageView.setImageBitmap(avatarBitmap);
        } else {
            profileImageView.setImageResource(R.drawable.ic_profile);
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void removeProfileImage() {
        StorageReference storageRef = storage.getReference("profile_images/" + userId + ".jpg");
        storageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    DocumentReference userProfileRef = db.collection("users").document(userId);
                    userProfileRef.update("profileImageUrl", FieldValue.delete())
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(this, "Profile image removed", Toast.LENGTH_SHORT).show();
                                loadProfileImage();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to remove profile image URL", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete profile image", Toast.LENGTH_SHORT).show());
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
        dobField.addTextChangedListener(new DateOfBirthTextWatcher());
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
                    dobField.setText(String.format(Locale.US, "%02d/%02d/%04d", month1 + 1, dayOfMonth, year1));
                },
                year, month, day);

        datePickerDialog.show();
    }

    private class DateOfBirthTextWatcher implements TextWatcher {
        private boolean isUpdating;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isUpdating) return;
            String cleanInput = s.toString().replaceAll("[^\\d]", "");
            StringBuilder formatted = new StringBuilder();
            if (cleanInput.length() >= 2) {
                formatted.append(cleanInput.substring(0, 2)).append("/");
                if (cleanInput.length() >= 4) formatted.append(cleanInput.substring(2, 4)).append("/");
                if (cleanInput.length() > 4) formatted.append(cleanInput.substring(4));
            } else {
                formatted.append(cleanInput);
            }

            isUpdating = true;
            dobField.setText(formatted.toString());
            dobField.setSelection(formatted.length());
            isUpdating = false;
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private void saveProfileData() {
        String name = nameField.getText() != null ? nameField.getText().toString().trim() : "";
        String email = emailField.getText() != null ? emailField.getText().toString().trim() : "";
        String dob = dobField.getText() != null ? dobField.getText().toString().trim() : "";
        String country = countrySpinner.getSelectedItem() != null ? countrySpinner.getSelectedItem().toString() : "";
        boolean notificationsEnabled = notificationsSwitch.isChecked();
        String phone = phoneField.getText() != null ? phoneField.getText().toString().trim() : "";

        if (name.isEmpty() || email.isEmpty() || dob.isEmpty() || country.isEmpty()) {
            Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", name);
        userProfile.put("email", email);
        userProfile.put("dob", dob);
        userProfile.put("country", country);
        userProfile.put("notificationsEnabled", notificationsEnabled);
        if (!phone.isEmpty()) userProfile.put("phone", phone);

        DocumentReference userProfileRef = db.collection("users").document(userId);
        userProfileRef.set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    if (imageUri != null) {
                        uploadProfileImage(userProfileRef);
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadProfileImage(DocumentReference userProfileRef) {
        StorageReference storageRef = storage.getReference("profile_images/" + userId + ".jpg");
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    userProfileRef.update("profileImageUrl", uri.toString())
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile image uploaded", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                Toast.makeText(this, "Error saving profile image URL", Toast.LENGTH_SHORT).show();
                            });
                }))
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to upload profile image", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadUserProfile() {
        DocumentReference userProfileRef = db.collection("users").document(userId);
        userProfileRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String dob = documentSnapshot.getString("dob");
                        String country = documentSnapshot.getString("country");
                        Boolean notificationsEnabled = documentSnapshot.getBoolean("notificationsEnabled");
                        String phone = documentSnapshot.getString("phone");

                        // Populate UI fields
                        if (name != null) nameField.setText(name);
                        if (email != null) emailField.setText(email);
                        if (dob != null) dobField.setText(dob);
                        if (country != null) {
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                    this, R.array.country_array, android.R.layout.simple_spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            countrySpinner.setAdapter(adapter);

                            int spinnerPosition = adapter.getPosition(country);
                            countrySpinner.setSelection(spinnerPosition);
                        }
                        if (phone != null) phoneField.setText(phone);
                        if (notificationsEnabled != null) notificationsSwitch.setChecked(notificationsEnabled);

                        loadProfileImage();
                    } else {
                        Toast.makeText(this, "No profile found. Please complete your profile.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadProfileImage() {
        StorageReference storageRef = storage.getReference("profile_images/" + userId + ".jpg");
        storageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(this)
                            .load(uri)
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(R.drawable.ic_profile)
                            .into(profileImageView);
                    removeProfileImageButton.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    updateProfileAvatar();
                    removeProfileImageButton.setVisibility(View.GONE);
                });
    }
}
