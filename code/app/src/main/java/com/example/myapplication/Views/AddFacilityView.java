// com/example/myapplication/AddFacilityActivity.java

package com.example.myapplication.Views;

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
import com.example.myapplication.Controllers.AddFacilityController;
import com.example.myapplication.Models.Facility;
import com.example.myapplication.R;

public class AddFacilityView extends AppCompatActivity {

    private static final String TAG = "AddFacilityActivity";

    private ImageView facilityImageView;
    private EditText facilityNameField, facilityLocationField;
    private Uri facilityImageUri;

    private Button uploadFacilityImageButton, saveFacilityButton;

    private AddFacilityController controller;

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

        controller = new AddFacilityController();

        facilityImageView = findViewById(R.id.facilityImageView);
        facilityNameField = findViewById(R.id.facility_name);
        facilityLocationField = findViewById(R.id.facility_location);
        uploadFacilityImageButton = findViewById(R.id.uploadFacilityImageButton);
        saveFacilityButton = findViewById(R.id.saveFacilityButton);

        Intent intent = getIntent();
        if (intent.hasExtra("facilityId")) {
            // Load existing facility details for editing
            loadFacilityDetails();
        }

        uploadFacilityImageButton.setOnClickListener(v -> openImagePicker());
        saveFacilityButton.setOnClickListener(v -> saveFacilityDetails());
    }

    /**
     * Opens the image picker to select an image for the facility.
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
        Toast.makeText(this, "Select an image for the facility", Toast.LENGTH_SHORT).show();
    }

    /**
     * Loads the facility details via the Controller.
     */
    private void loadFacilityDetails() {
        controller.loadFacility(new AddFacilityController.AddFacilityListener() {
            @Override
            public void onFacilitySavedSuccessfully() {
                // Not used here
            }

            @Override
            public void onFacilitySaveFailed(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(AddFacilityView.this, "Failed to load facility details", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading facility details: ", e);
                });
            }

            @Override
            public void onFacilityLoaded(Facility facility) {
                runOnUiThread(() -> {
                    facilityNameField.setText(facility.getName());
                    facilityLocationField.setText(facility.getLocation());
                    if (facility.getImageUrl() != null && !facility.getImageUrl().isEmpty()) {
                        Glide.with(AddFacilityView.this)
                                .load(facility.getImageUrl())
                                .apply(RequestOptions.centerCropTransform())
                                .placeholder(R.drawable.ic_placeholder_image)
                                .into(facilityImageView);
                    }
                });
            }

            @Override
            public void onFacilityLoadFailed(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(AddFacilityView.this, "Failed to load facility details", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading facility details: ", e);
                });
            }

            @Override
            public void onImageUploaded(String imageUrl) {
                // Not used here
            }

            @Override
            public void onImageUploadFailed(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(AddFacilityView.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error uploading image: ", e);
                });
            }
        });
    }

    /**
     * Saves the facility details using the Controller.
     */
    private void saveFacilityDetails() {
        String name = facilityNameField.getText().toString().trim();
        String location = facilityLocationField.getText().toString().trim();

        if (name.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please enter all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        controller.saveFacility(name, location, facilityImageUri, new AddFacilityController.AddFacilityListener() {
            @Override
            public void onFacilitySavedSuccessfully() {
                runOnUiThread(() -> {
                    Toast.makeText(AddFacilityView.this, "Facility saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onFacilitySaveFailed(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(AddFacilityView.this, "Error saving facility", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error saving facility: ", e);
                });
            }

            @Override
            public void onFacilityLoaded(Facility facility) {
                // Not used here
            }

            @Override
            public void onFacilityLoadFailed(Exception e) {
                // Not used here
            }

            @Override
            public void onImageUploaded(String imageUrl) {
                // Not used here
            }

            @Override
            public void onImageUploadFailed(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(AddFacilityView.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error uploading image: ", e);
                });
            }
        });
    }
}
