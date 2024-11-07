package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddFacilityActivity extends AppCompatActivity {

    private static final String TAG = "AddFacilityActivity";
    private static final String FACILITY_COLLECTION = "Facilities";

    private ImageView facilityImageView;
    private EditText facilityNameField, facilityLocationField;
    private Button uploadFacilityImageButton, saveFacilityButton;

    private Uri facilityImageUri;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private String userId;

    private String facilityId; // If editing an existing facility

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    facilityImageUri = result.getData().getData();
                    Glide.with(this)
                            .load(facilityImageUri)
                            .apply(RequestOptions.centerCropTransform())
                            .into(facilityImageView);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_facility);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        facilityImageView = findViewById(R.id.facilityImageView);
        facilityNameField = findViewById(R.id.facility_name);
        facilityLocationField = findViewById(R.id.facility_location);
        uploadFacilityImageButton = findViewById(R.id.uploadFacilityImageButton);
        saveFacilityButton = findViewById(R.id.saveFacilityButton);

        Intent intent = getIntent();
        if (intent.hasExtra("facilityId")) {
            facilityId = intent.getStringExtra("facilityId");
            loadFacilityDetails(facilityId);
        }


        uploadFacilityImageButton.setOnClickListener(v -> openImagePicker());
        saveFacilityButton.setOnClickListener(v -> saveFacilityDetails());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
        Toast.makeText(this, "Select an image for the facility", Toast.LENGTH_SHORT).show();
    }
    private void loadFacilityDetails(String facilityId) {
        db.collection(FACILITY_COLLECTION).document(facilityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Facility facility = documentSnapshot.toObject(Facility.class);
                        if (facility != null) {
                            facilityNameField.setText(facility.getName());
                            facilityLocationField.setText(facility.getLocation());
                            if (facility.getImageUrls() != null && !facility.getImageUrls().isEmpty()) {
                                Glide.with(this)
                                        .load(facility.getImageUrls().get(0))
                                        .apply(RequestOptions.centerCropTransform())
                                        .placeholder(R.drawable.ic_placeholder_image)
                                        .into(facilityImageView);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load facility details", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading facility details: ", e);
                });
    }


    private void saveFacilityDetails() {
        String name = facilityNameField.getText().toString().trim();
        String location = facilityLocationField.getText().toString().trim();

        if (name.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please enter all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (facilityImageUri != null) {
            String imageName = UUID.randomUUID().toString() + ".jpg";
            StorageReference storageRef = storage.getReference("facility_images/" + userId + "/" + imageName);
            storageRef.putFile(facilityImageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                List<String> imageUrls = new ArrayList<>();
                                imageUrls.add(uri.toString());
                                saveFacilityToFirestore(name, location, imageUrls);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to retrieve image URL", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error retrieving image URL: ", e);
                            }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error uploading image: ", e);
                    });
        } else {
            saveFacilityToFirestore(name, location, null);
        }
    }

    private void saveFacilityToFirestore(String name, String location, List<String> imageUrls) {
        Map<String, Object> facility = new HashMap<>();
        facility.put("name", name);
        facility.put("location", location);
        facility.put("organizerId", userId);
        facility.put("imageUrls", imageUrls != null ? imageUrls : new ArrayList<String>()); // make sure imageUrls is never null

        if (facilityId != null) {
            db.collection(FACILITY_COLLECTION).document(facilityId)
                    .set(facility, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Facility updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error updating facility", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error updating facility: ", e);
                    });
        } else {
            db.collection(FACILITY_COLLECTION).add(facility)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Facility added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error adding facility", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error adding facility: ", e);
                    });
        }
    }

}

