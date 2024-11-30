package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Views.AddFacilityView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * EditProfileActivity allows entrants to edit their profile and updates data on Firestore.
 */
public class EditProfileActivity extends BaseActivity {

    private static final String TAG = "EditProfileActivity";

    private CircleImageView profileImageView;
    private ImageButton editProfileImageButton, removeProfileImageButton;
    private EditText nameField, emailField, dobField, phoneField;
    private Spinner countrySpinner;
    private Button saveChangesButton, addFacilityButton;
    private Switch notificationSwitch;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private String deviceId;
    private Uri imageUri;

    private boolean isAdmin = false;
    private boolean isOrganizer = false;
    private boolean notificationsPerm = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        deviceId = retrieveDeviceId();
        if (deviceId == null) {
            Toast.makeText(this, "Device ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Device ID: " + deviceId);

        initializeUI();
        setListeners();
        loadUserProfile();
    }

    private void initializeUI() {
        profileImageView = findViewById(R.id.profile_image);
        editProfileImageButton = findViewById(R.id.edit_profile_image_button);
        removeProfileImageButton = findViewById(R.id.remove_profile_image_button);
        nameField = findViewById(R.id.name_field);
        emailField = findViewById(R.id.email_field);
        dobField = findViewById(R.id.dob_field);
        phoneField = findViewById(R.id.phone_field);
        countrySpinner = findViewById(R.id.country_spinner);
        saveChangesButton = findViewById(R.id.save_changes_button);
        addFacilityButton = findViewById(R.id.add_facility_button);
        notificationSwitch = findViewById(R.id.notifications_switch);

        dobField.setInputType(InputType.TYPE_CLASS_DATETIME);
        dobField.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showDatePicker();
                return true;
            }
            return false;
        });

        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Generate avatar as user types their name
                generateDefaultAvatar(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

    }

    private void setListeners() {
        editProfileImageButton.setOnClickListener(v -> openFileChooser());
        saveChangesButton.setOnClickListener(v -> saveProfileData());
        removeProfileImageButton.setOnClickListener(v -> deleteProfileImage());
        addFacilityButton.setOnClickListener(v -> addFacility());
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> notificationsPerm = isChecked);
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Log.d(TAG, "Image URI: " + imageUri.toString());
            Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImageView);
            removeProfileImageButton.setVisibility(View.VISIBLE);
        }
    }

    private void loadUserProfile() {
        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String dob = documentSnapshot.getString("dob");
                        String phone = documentSnapshot.getString("phone");
                        String country = documentSnapshot.getString("country");
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                        Boolean isOrganizerValue = documentSnapshot.getBoolean("isOrganizer");
                        Boolean isAdminValue = documentSnapshot.getBoolean("isOrganizer");
                        Boolean notificationsValue = documentSnapshot.getBoolean("notificationsPerm");

                        isOrganizer = isOrganizerValue != null && isOrganizerValue;
                        isAdmin = isAdminValue != null && isAdminValue;
                        notificationsPerm = notificationsValue != null && notificationsValue;

                        nameField.setText(name);
                        emailField.setText(email);
                        dobField.setText(dob);
                        phoneField.setText(phone);
                        notificationSwitch.setChecked(notificationsPerm);

                        if (!TextUtils.isEmpty(country)) {
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                    this, R.array.country_array, android.R.layout.simple_spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            countrySpinner.setAdapter(adapter);
                            int spinnerPosition = adapter.getPosition(country);
                            countrySpinner.setSelection(spinnerPosition);
                        }

                        if (!TextUtils.isEmpty(profileImageUrl)) {
                            Glide.with(this)
                                    .load(profileImageUrl)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(profileImageView);
                            removeProfileImageButton.setVisibility(View.VISIBLE);
                        } else {
                            generateDefaultAvatar(name);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading profile: ", e));
    }

    private void saveProfileData() {
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String dob = dobField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String country = countrySpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(dob) || TextUtils.isEmpty(country)) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("name", name);
        profileData.put("email", email);
        profileData.put("dob", dob);
        profileData.put("phone", phone);
        profileData.put("country", country);
        profileData.put("isOrganizer", isOrganizer);
        profileData.put("isAdmin", isAdmin);
        profileData.put("notificationsPerm", notificationsPerm);
        profileData.put("events", null);
        profileData.put("eventsAttending", new HashMap<>());

        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.set(profileData)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e(TAG, "Error saving profile: ", e));

        if (imageUri != null) {
            uploadProfileImage(userRef);
        }
    }

    private void uploadProfileImage(DocumentReference userRef) {
        if (imageUri == null) {
            Log.e(TAG, "No imageUri to upload.");
            return;
        }

        StorageReference storageRef = storage.getReference("profile_images/" + deviceId + ".jpg");
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "Image uploaded successfully.");
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                Log.d(TAG, "Image URL: " + uri.toString());
                                userRef.update("profileImageUrl", uri.toString())
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Profile image URL updated in Firestore."))
                                        .addOnFailureListener(e -> Log.e(TAG, "Failed to update Firestore with image URL.", e));
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to get download URL.", e));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error uploading image.", e));
    }

    private void deleteProfileImage() {
        StorageReference storageRef = storage.getReference("profile_images/" + deviceId + ".jpg");
        storageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Image deleted from Firebase Storage.");
                    DocumentReference userRef = db.collection("users").document(deviceId);
                    userRef.update("profileImageUrl", null)
                            .addOnSuccessListener(aVoid1 -> {
                                profileImageView.setImageResource(R.drawable.ic_profile);
                                removeProfileImageButton.setVisibility(View.GONE);
                                Log.d(TAG, "Profile image URL removed from Firestore.");
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to remove image URL from Firestore.", e));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete image from Firebase Storage.", e));
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
        String firstLetter = (name != null && !name.isEmpty()) ? name.substring(0, 1).toUpperCase(Locale.US) : "?";
        Bitmap avatar = AvatarUtil.generateAvatar(firstLetter, 200, this);
        profileImageView.setImageBitmap(avatar);
        removeProfileImageButton.setVisibility(View.GONE);
    }

    private void addFacility() {
        Intent intent = new Intent(this, AddFacilityView.class);
        startActivity(intent);
        isOrganizer = true; // Update the organizer status
        db.collection("users").document(deviceId).update("isOrganizer", true)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Updated isOrganizer to true."))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update isOrganizer: ", e));
    }
}
