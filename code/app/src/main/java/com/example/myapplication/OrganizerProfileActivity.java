package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.*;
import android.util.Patterns;
import android.view.*;
import android.widget.*;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

import java.text.*;
import java.util.*;


public class OrganizerProfileActivity extends AppCompatActivity {

    private static final int MIN_AGE = 1;
    private static final int MAX_AGE = 100;

    private ImageButton removeProfileImageButton;
    private CircleImageView profileImageView;
    private ImageButton editProfileImageButton, backButton;
    private EditText nameField, emailField, dobField, phoneField;
    private Spinner countrySpinner;
    private Switch notificationsSwitch;
    private Button saveChangesButton, buttonNotificationGroups;

    private Uri imageUri;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private BottomNavigationView bottomNavigationView;

    private String userId = "Organizer1";

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();

                    Glide.with(this)
                            .load(imageUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profileImageView);
                }
                removeProfileImageButton.setVisibility(View.VISIBLE);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_profile);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize UI elements
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
        buttonNotificationGroups = findViewById(R.id.buttonManageNotifications);


        // Button listeners
        editProfileImageButton.setOnClickListener(v -> openFileChooser());
        saveChangesButton.setOnClickListener(v -> saveProfileData());
        backButton.setOnClickListener(v -> onBackPressed());
        removeProfileImageButton.setOnClickListener(v -> removeProfileImage());
        // Set up manage notifications button
        buttonNotificationGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openManageNotifications();
            }
        });


        // Set up Date of Birth field
        setupDateOfBirthField();

        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DocumentReference userProfileRef = db.collection("Organizers").document(userId);
                userProfileRef.get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                                if (profileImageUrl == null || profileImageUrl.isEmpty()) {
                                    String name = nameField.getText().toString().trim();
                                    if (!name.isEmpty()) {
                                        String firstLetter = String.valueOf(name.charAt(0)).toUpperCase(Locale.US);
                                        Bitmap avatarBitmap = AvatarUtil.generateAvatar(firstLetter, 200, OrganizerProfileActivity.this);
                                        profileImageView.setImageBitmap(avatarBitmap);
                                    } else {
                                        profileImageView.setImageResource(R.drawable.ic_profile);
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(Throwable::printStackTrace);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }

        loadUserProfile();
    }

    private void removeProfileImage() {
        StorageReference storageRef = storage.getReference("profile_images/" + userId + ".jpg");
        storageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    DocumentReference userProfileRef = db.collection("Organizers").document(userId);
                    userProfileRef.update("profileImageUrl", FieldValue.delete())
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(this, "Profile image removed", Toast.LENGTH_SHORT).show();
                                loadProfileImage();
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                Toast.makeText(this, "Failed to remove profile image URL", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to delete profile image", Toast.LENGTH_SHORT).show();
                });
    }

    private void openManageNotifications() {
        Intent intent = new Intent(OrganizerProfileActivity.this, ManageNotificationGroupsActivity.class);
        startActivity(intent);
    }

    public void togglePreferences(View view) {
        // Find the notifications container by ID
        LinearLayout notificationsContainer = findViewById(R.id.notifications_container);

        // Toggle visibility
        if (notificationsContainer.getVisibility() == View.GONE) {
            notificationsContainer.setVisibility(View.VISIBLE);
        } else {
            notificationsContainer.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
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
        final int currentYear = calendar.get(Calendar.YEAR);
        int year = currentYear;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String dobText = dobField.getText().toString();
        if (dobText.length() == 10) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                Date date = sdf.parse(dobText);
                calendar.setTime(date);
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, month1, dayOfMonth);

                    int age = currentYear - year1;
                    if (age >= MIN_AGE && age <= MAX_AGE) {
                        dobField.setText(String.format(Locale.US, "%02d/%02d/%04d", month1 + 1, dayOfMonth, year1));
                    } else {
                        Toast.makeText(this, "Age must be between " + MIN_AGE + " and " + MAX_AGE, Toast.LENGTH_SHORT).show();
                    }
                },
                year, month, day);

        Calendar minDate = Calendar.getInstance();
        minDate.set(currentYear - MAX_AGE, Calendar.JANUARY, 1);

        Calendar maxDate = Calendar.getInstance();
        maxDate.set(currentYear - MIN_AGE, Calendar.DECEMBER, 31);

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private class DateOfBirthTextWatcher implements TextWatcher {
        private boolean isUpdating;
        private final String dateFormat = "MM/dd/yyyy";
        private final Calendar calendar = Calendar.getInstance();

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (isUpdating) return;

            isUpdating = true;

            String text = s.toString().replaceAll("[^\\d]", "");

            if (text.length() >= 6) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy", Locale.US);
                    Date date = sdf.parse(text);
                    if (date != null) {
                        calendar.setTime(date);
                        String formattedDate = new SimpleDateFormat(dateFormat, Locale.US).format(calendar.getTime());
                        dobField.setText(formattedDate);
                        dobField.setSelection(formattedDate.length());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            isUpdating = false;
        }
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && (email.endsWith(".com") || email.endsWith(".ca"));
    }

    private void saveProfileData() {
        String name = nameField.getText() != null ? nameField.getText().toString().trim() : "";
        String email = emailField.getText() != null ? emailField.getText().toString().trim() : "";
        String dob = dobField.getText() != null ? dobField.getText().toString().trim() : "";
        String country = countrySpinner.getSelectedItem() != null
                ? countrySpinner.getSelectedItem().toString()
                : "";
        boolean notificationsEnabled = notificationsSwitch.isChecked();
        String phone = phoneField.getText() != null ? phoneField.getText().toString().trim() : "";

        // Validation
        if (name.isEmpty() || email.isEmpty() || dob.isEmpty() || country.isEmpty()) {
            Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Email validation
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Date of birth validation
        if (dobField.getError() != null) {
            Toast.makeText(this, "Please enter a valid date of birth", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save user profile data in Firestore
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", name);
        userProfile.put("email", email);
        userProfile.put("dob", dob);
        userProfile.put("country", country);
        userProfile.put("notificationsEnabled", notificationsEnabled);
        if (!phone.isEmpty()) userProfile.put("phone", phone);

        DocumentReference userProfileRef = db.collection("Organizers").document(userId);
        userProfileRef.set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
                });

        // Upload profile image to Firebase Storage if a new image is selected
        if (imageUri != null) {
            StorageReference storageRef = storage.getReference("profile_images/" + userId + ".jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Save the download URL to Firestore if needed
                            userProfileRef.update("profileImageUrl", uri.toString());
                        });
                        Toast.makeText(this, "Profile image uploaded", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to upload profile image", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadUserProfile() {
        DocumentReference userProfileRef = db.collection("Organizers").document(userId);
        userProfileRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String dob = documentSnapshot.getString("dob");
                        String country = documentSnapshot.getString("country");
                        Boolean notificationsEnabled = documentSnapshot.getBoolean("notificationsEnabled");
                        String phone = documentSnapshot.getString("phone");

                        // Set the values to the fields
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
                        if (notificationsEnabled != null)
                            notificationsSwitch.setChecked(notificationsEnabled);
                        if (phone != null) phoneField.setText(phone);

                        loadProfileImage();
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
                    String name = nameField.getText() != null ? nameField.getText().toString().trim() : "";
                    if (!name.isEmpty()) {
                        String firstLetter = String.valueOf(name.charAt(0)).toUpperCase(Locale.US);
                        Bitmap avatarBitmap = AvatarUtil.generateAvatar(firstLetter, 200, this);
                        profileImageView.setImageBitmap(avatarBitmap);
                    } else {
                        profileImageView.setImageResource(R.drawable.ic_profile);
                    }
                    removeProfileImageButton.setVisibility(View.GONE);
                });
    }
}