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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

import java.text.*;
import java.util.*;



public class EditProfileFragment extends Fragment {

    private static final int MIN_AGE = 1; // Minimum age set to 1
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

    private BottomNavigationView bottomNavigationView;

    private String userId = "userProfile";

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();

                    Glide.with(this)
                            .load(imageUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profileImageView);
                }
                removeProfileImageButton.setVisibility(View.VISIBLE);
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);


        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // UI elements
        profileImageView = view.findViewById(R.id.profile_image);
        editProfileImageButton = view.findViewById(R.id.edit_profile_image_button);
        backButton = view.findViewById(R.id.back_button);
        nameField = view.findViewById(R.id.name_field);
        emailField = view.findViewById(R.id.email_field);
        dobField = view.findViewById(R.id.dob_field);
        phoneField = view.findViewById(R.id.phone_field);
        countrySpinner = view.findViewById(R.id.country_spinner);
        notificationsSwitch = view.findViewById(R.id.notifications_switch);
        saveChangesButton = view.findViewById(R.id.save_changes_button);
        removeProfileImageButton = view.findViewById(R.id.remove_profile_image_button); // Initialize here


        // Buttons and fields
        editProfileImageButton.setOnClickListener(v -> openFileChooser());
        saveChangesButton.setOnClickListener(v -> saveProfileData());
        backButton.setOnClickListener(v -> navigateBack());
        removeProfileImageButton.setOnClickListener(v -> removeProfileImage()); // Set click listener


        setupDateOfBirthField();
        //adding textchanged listener
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (db == null) return;

                DocumentReference userProfileRef = db.collection("users").document(userId);
                userProfileRef.get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                                if (profileImageUrl == null || profileImageUrl.isEmpty()) {
                                    //no uploaded profile image, regenerate avatar
                                    String name = nameField.getText() != null ? nameField.getText().toString().trim() : "";
                                    if (!name.isEmpty()) {
                                        String firstLetter = String.valueOf(name.charAt(0)).toUpperCase(Locale.US);
                                        Bitmap avatarBitmap = AvatarUtil.generateAvatar(firstLetter, 200, requireContext());
                                        profileImageView.setImageBitmap(avatarBitmap);
                                    } else {
                                        //generic pfp
                                        profileImageView.setImageResource(R.drawable.ic_profile);
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                        });
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }


    private void removeProfileImage() {

        //reference to the profile image in Firebase Storage
        StorageReference storageRef = storage.getReference("profile_images/" + userId + ".jpg");

        // delete image from Firebase Storage
        storageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // when we successfully deleted the image from Storage
                    // remove the profileImageUrl field from Firestore
                    DocumentReference userProfileRef = db.collection("users").document(userId);
                    userProfileRef.update("profileImageUrl", FieldValue.delete())
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(getContext(), "Profile image removed", Toast.LENGTH_SHORT).show();
                                //reload the profile image to update the ImageView
                                loadProfileImage();
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Failed to remove profile image URL", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed to delete profile image", Toast.LENGTH_SHORT).show();
                });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //hide BottomNavigationView when entering EditProfileFragment
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }

        //back button
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navigateBack();
                    }
                });

        // Load user data from Firestore
        loadUserProfile();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // get BottomNavigationView back when leaving EditProfileFragment
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    //Navigate back by popping the back stack
    private void navigateBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    //Open file chooser to select profile image
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    // Set up the Date of Birth field with slash checkers and calendar icon
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

    //Date picker dialog for date of birth and validate age
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        final int currentYear = calendar.get(Calendar.YEAR);
        int year = currentYear;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // existing date if available
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
                getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, month1, dayOfMonth);

                    int age = currentYear - year1;
                    if (age >= MIN_AGE && age <= MAX_AGE) {
                        dobField.setText(String.format(Locale.US, "%02d/%02d/%04d", month1 + 1, dayOfMonth, year1));
                    } else {
                        Toast.makeText(getContext(), "Age must be between " + MIN_AGE + " and " + MAX_AGE, Toast.LENGTH_SHORT).show();
                    }
                },
                year, month, day);

        // age constraints
        Calendar minDate = Calendar.getInstance();
        minDate.set(currentYear - MAX_AGE, Calendar.JANUARY, 1);

        Calendar maxDate = Calendar.getInstance();
        maxDate.set(currentYear - MIN_AGE, Calendar.DECEMBER, 31);

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    //TextWatcher for date of birth field to auto-insert slashes
    private class DateOfBirthTextWatcher implements TextWatcher {
        private boolean isUpdating;
        private final String dateFormat = "MM/dd/yyyy";
        private final Calendar calendar = Calendar.getInstance();

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isUpdating) {
                isUpdating = false;
                return;
            }

            String input = s.toString();
            String cleanInput = input.replaceAll("[^\\d]", "");

            if (cleanInput.length() > 8) {
                cleanInput = cleanInput.substring(0, 8);
            }

            int length = cleanInput.length();

            StringBuilder formatted = new StringBuilder();

            if (length >= 2) {
                formatted.append(cleanInput.substring(0, 2));
                formatted.append("/");
                if (length >= 4) {
                    formatted.append(cleanInput.substring(2, 4));
                    formatted.append("/");
                    if (length > 4) {
                        formatted.append(cleanInput.substring(4));
                    }
                } else if (length > 2) {
                    formatted.append(cleanInput.substring(2));
                }
            } else {
                formatted.append(cleanInput);
            }

            isUpdating = true;
            dobField.setText(formatted.toString());
            dobField.setSelection(formatted.length());

            //validate date if length is 10 (MM/DD/YYYY)
            if (formatted.length() == 10) {
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
                sdf.setLenient(false);

                try {
                    Date date = sdf.parse(formatted.toString());
                    // Check if date is within the allowed range
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(date);

                    int age = calendar.get(Calendar.YEAR) - selectedDate.get(Calendar.YEAR);
                    if (age < MIN_AGE || age > MAX_AGE) {
                        dobField.setError("Age must be between " + MIN_AGE + " and " + MAX_AGE);
                    } else {
                        dobField.setError(null);
                    }
                } catch (ParseException e) {
                    dobField.setError("Invalid date");
                }
            } else {
                dobField.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    //validates email is correct
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && (email.endsWith(".com") || email.endsWith(".ca"));
    }

    //saves profile data to Firestore
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
            Toast.makeText(getContext(), "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //email validation
        if (!isValidEmail(email)) {
            Toast.makeText(getContext(), "Invalid email format.", Toast.LENGTH_SHORT).show();
            return;
        }

        //date of birth validation
        if (dobField.getError() != null) {
            Toast.makeText(getContext(), "Please enter a valid date of birth", Toast.LENGTH_SHORT).show();
            return;
        }

        //save user profile data in Firestore
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
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    // navigateBack();

                    if (notificationsEnabled) {
                        NotificationService.sendNotification(userProfile, getContext(), "Success", "You have opted into notifications");
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
                });

        //upload profile image to Firebase Storage if a new image is selected
        if (imageUri != null) {
            StorageReference storageRef = storage.getReference("profile_images/" + userId + ".jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // save the download URL to Firestore if needed
                            userProfileRef.update("profileImageUrl", uri.toString());
                        });
                        Toast.makeText(getContext(), "Profile image uploaded", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Failed to upload profile image", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    //Load user profile data from Firestore
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

                        //set the values to the fields
                        if (name != null) nameField.setText(name);
                        if (email != null) emailField.setText(email);
                        if (dob != null) dobField.setText(dob);
                        if (country != null) {
                            //set the spinner selection
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                    requireContext(), R.array.country_array, android.R.layout.simple_spinner_item);
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
                    Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                });
    }

    //load profile image from Firebase Storage or set default avatar
    private void loadProfileImage() {
        StorageReference storageRef = storage.getReference("profile_images/" + userId + ".jpg");
        storageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(this)
                            .load(uri)
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(R.drawable.ic_profile)
                            .into(profileImageView);

                    // Show the remove button since an image exists
                    removeProfileImageButton.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    //if there's no image, generate a default one with the first letter of the user's name
                    String name = nameField.getText() != null ? nameField.getText().toString().trim() : "";
                    if (!name.isEmpty()) {
                        String firstLetter = String.valueOf(name.charAt(0)).toUpperCase(Locale.US);
                        Bitmap avatarBitmap = AvatarUtil.generateAvatar(firstLetter, 200, requireContext());
                        profileImageView.setImageBitmap(avatarBitmap);
                    } else {
                        // Set generic default image if the name is unavailable
                        profileImageView.setImageResource(R.drawable.ic_profile);
                    }

                    //hide rm button
                    removeProfileImageButton.setVisibility(View.GONE);
                });
    }
}