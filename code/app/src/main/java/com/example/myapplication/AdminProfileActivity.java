package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Views.HomeView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminProfileActivity extends BaseActivity {

    private static final String TAG = "AdminProfileActivity";

    private CircleImageView profileImageView;
    private ImageButton editProfileImageButton, removeProfileImageButton, backButton;
    private EditText nameField, emailField, dobField, phoneField;
    private Spinner countrySpinner;
    private Button saveChangesButton, buttonBrowseUsers, buttonBrowseEvents, buttonBrowseFacilities, buttonBrowseImages, buttonBrowseQR;

    private Uri imageUri;
    private boolean isAdmin = true; // Default for admins
    private boolean isOrganizer = true; // Default for admins
    private boolean notificationsPerm = false;
    private Button myEventsButton;


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

        initializeUI();
        setListeners();
        loadUserProfile();
    }

    private void initializeUI() {
        profileImageView = findViewById(R.id.profile_image);
        editProfileImageButton = findViewById(R.id.edit_profile_image_button);
        removeProfileImageButton = findViewById(R.id.remove_profile_image_button);
        backButton = findViewById(R.id.back_button);
        nameField = findViewById(R.id.name_field);
        emailField = findViewById(R.id.email_field);
        dobField = findViewById(R.id.dob_field);
        phoneField = findViewById(R.id.phone_field);
        countrySpinner = findViewById(R.id.country_spinner);
        saveChangesButton = findViewById(R.id.save_changes_button);
        myEventsButton = findViewById(R.id.my_events_button);


        // Buttons for admin-specific actions
        buttonBrowseUsers = findViewById(R.id.button_browse_user_profiles);
        buttonBrowseEvents = findViewById(R.id.button_browse_events);
        buttonBrowseFacilities = findViewById(R.id.button_browse_facilities);
        buttonBrowseImages = findViewById(R.id.button_browse_images);
        buttonBrowseQR = findViewById(R.id.button_browse_qrhashdata);

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

    private void BrowseQR(){
        Intent intent = new Intent(this, ManageQrLinksActivity.class);
        startActivity(intent);
    }


    private void setListeners() {
        editProfileImageButton.setOnClickListener(v -> openFileChooser());
        saveChangesButton.setOnClickListener(v -> saveProfileData());
        backButton.setOnClickListener(v -> finish());
        removeProfileImageButton.setOnClickListener(v -> deleteProfileImage());
        buttonBrowseUsers.setOnClickListener(v -> BrowseUsers());
        buttonBrowseEvents.setOnClickListener(v -> BrowseEvents());
        buttonBrowseFacilities.setOnClickListener(v -> BrowseFacilities());
        buttonBrowseImages.setOnClickListener(v -> BrowseImages());
        buttonBrowseQR.setOnClickListener(v -> BrowseQR());
        myEventsButton.setOnClickListener(v -> openMyEvents());

    }

    private void openMyEvents() {
        Intent intent = new Intent(this, HomeFragment.class); // changed
        startActivity(intent);
    }


    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void loadUserProfile() {
        String deviceId = retrieveDeviceId();
        if (deviceId == null || deviceId.isEmpty()) {
            Toast.makeText(this, "Device ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

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

                        nameField.setText(name);
                        emailField.setText(email);
                        dobField.setText(dob);
                        phoneField.setText(phone);

                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                this, R.array.country_array, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        countrySpinner.setAdapter(adapter);
                        countrySpinner.setSelection(adapter.getPosition(country));

                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            // Load profile image from URL
                            Glide.with(this)
                                    .load(profileImageUrl)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(profileImageView);
                            removeProfileImageButton.setVisibility(View.VISIBLE);
                        } else {
                            // Auto-generate avatar if no profile image URL
                            generateDefaultAvatar(name);
                        }
                    } else {
                        // If no profile exists, generate a default avatar and initialize fields
                        generateDefaultAvatar(null);
                        initializeDefaultFields();
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to load profile.", e));
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

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("name", name);
        profileData.put("email", email);
        profileData.put("dob", dob);
        profileData.put("phone", phone);
        profileData.put("country", country);
        profileData.put("isAdmin", isAdmin);
        profileData.put("isOrganizer", isOrganizer);
        profileData.put("notificationsPerm", notificationsPerm);

        String deviceId = retrieveDeviceId();
        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.set(profileData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                    if (imageUri != null) uploadProfileImage(userRef);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update profile.", e));
    }

    private void uploadProfileImage(DocumentReference userRef) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_images/" + retrieveDeviceId() + ".jpg");
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    userRef.update("profileImageUrl", uri.toString())
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Profile image updated successfully."));
                }))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to upload profile image.", e));
    }

    private void deleteProfileImage() {
        DocumentReference userRef = db.collection("users").document(retrieveDeviceId());
        userRef.update("profileImageUrl", null)
                .addOnSuccessListener(aVoid -> {
                    profileImageView.setImageResource(R.drawable.ic_profile);
                    removeProfileImageButton.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete profile image.", e));
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

    private void initializeDefaultFields() {
        nameField.setText("");
        emailField.setText("");
        dobField.setText("");
        phoneField.setText("");
        isAdmin = true;
        isOrganizer = true;
        notificationsPerm = false;

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.country_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        // Generate a default avatar when no user data exists
        generateDefaultAvatar(null);
    }

    private void generateDefaultAvatar(String name) {
        String firstLetter = (name != null && !name.isEmpty()) ? name.substring(0, 1).toUpperCase(Locale.US) : "?";
        Bitmap avatar = AvatarUtil.generateAvatar(firstLetter, 200, this);
        profileImageView.setImageBitmap(avatar);
        removeProfileImageButton.setVisibility(View.GONE);
    }

}
