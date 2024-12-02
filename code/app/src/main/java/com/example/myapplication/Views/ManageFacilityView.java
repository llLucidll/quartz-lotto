package com.example.myapplication.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.BaseActivity;
import com.example.myapplication.Controllers.ManageFacilityController;
import com.example.myapplication.Models.Facility;
import com.example.myapplication.R;

/**
 * ManageFacilityView allows users to view, edit, or delete their facility details.
 */
public class ManageFacilityView extends BaseActivity {

    private static final String TAG = "ManageFacilitiesActivity";

    private ImageView facilityImageView;
    private TextView facilityNameTextView, facilityLocationTextView;
    private Button addEditFacilityButton, deleteFacilityButton;

    private ManageFacilityController controller;

    private Facility currentFacility;

    /**
     * Initializes the activity, sets up UI elements, and loads facility details.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_facilities);

        // Initialize the controller with the context
        controller = new ManageFacilityController(this);

        facilityImageView = findViewById(R.id.facilityImageView);
        facilityNameTextView = findViewById(R.id.facility_name_text_view);
        facilityLocationTextView = findViewById(R.id.facility_location_text_view);
        addEditFacilityButton = findViewById(R.id.edit_facility_button);
        deleteFacilityButton = findViewById(R.id.delete_facility_button);

        addEditFacilityButton.setOnClickListener(v -> {
            if (currentFacility != null) {
                // Edit existing facility
                Intent intent = new Intent(ManageFacilityView.this, AddFacilityView.class);
                intent.putExtra("facilityId", currentFacility.getId());
                startActivity(intent);
            } else {
                // Add new facility
                Intent intent = new Intent(ManageFacilityView.this, AddFacilityView.class);
                startActivity(intent);
            }
        });

        deleteFacilityButton.setOnClickListener(v -> {
            if (currentFacility != null) {
                confirmDeleteFacility(currentFacility);
            }
        });

        // Load facility details
        loadFacility();
    }

    /**
     * Called when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadFacility();
    }

    /**
     * Loads facility using the Controller and device ID.
     */
    private void loadFacility() {
        String deviceId = retrieveDeviceId();
        if (deviceId == null || deviceId.isEmpty()) {
            Toast.makeText(this, "Device ID not found. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        controller.loadFacility(new ManageFacilityController.ManageFacilitiesListener() {
            @Override
            public void onFacilityLoaded(Facility facility) {
                runOnUiThread(() -> {
                    currentFacility = facility;
                    updateUIWithFacility(facility);
                });
            }

            @Override
            public void onFacilityLoadFailed(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(ManageFacilityView.this, "No facility found. Please add one.", Toast.LENGTH_SHORT).show();
                    clearFacilityUI();
                });
            }

            @Override
            public void onFacilitySavedSuccessfully() {
                // Not used here
            }

            @Override
            public void onFacilitySaveFailed(Exception e) {
                // Not used here
            }

            @Override
            public void onFacilityDeletedSuccessfully() {
                runOnUiThread(() -> {
                    Toast.makeText(ManageFacilityView.this, "Facility deleted successfully", Toast.LENGTH_SHORT).show();
                    clearFacilityUI();
                });
            }

            @Override
            public void onFacilityDeleteFailed(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(ManageFacilityView.this, "Error deleting facility", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error deleting facility: ", e);
                });
            }

            @Override
            public void onImageDeletedSuccessfully() {
                Log.d(TAG, "Image deleted successfully.");
            }

            @Override
            public void onImageDeleteFailed(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(ManageFacilityView.this, "Error deleting image from Storage", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error deleting image from Storage: ", e);
                });
            }
        }, deviceId); // Pass deviceId to the controller
    }

    /**
     * Updates the UI with the facility details.
     */
    private void updateUIWithFacility(Facility facility) {
        facilityNameTextView.setText(facility.getName());
        facilityLocationTextView.setText(facility.getLocation());

        if (facility.getImageUrl() != null && !facility.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(facility.getImageUrl())
                    .apply(RequestOptions.centerCropTransform())
                    .placeholder(R.drawable.ic_placeholder_image)
                    .into(facilityImageView);
        } else {
            facilityImageView.setImageResource(R.drawable.ic_placeholder_image);
        }

        addEditFacilityButton.setText("Edit Facility");
        deleteFacilityButton.setVisibility(View.VISIBLE);
    }

    /**
     * Clears the UI when no facility is present.
     */
    private void clearFacilityUI() {
        currentFacility = null;
        facilityNameTextView.setText("N/A");
        facilityLocationTextView.setText("N/A");
        facilityImageView.setImageResource(R.drawable.ic_placeholder_image);
        addEditFacilityButton.setText("Add Facility");
        deleteFacilityButton.setVisibility(View.GONE);
    }

    /**
     * Confirms deletion of a facility with the user.
     */
    private void confirmDeleteFacility(Facility facility) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Facility")
                .setMessage("Are you sure you want to delete this facility and its image?")
                .setPositiveButton("Yes", (dialog, which) -> deleteFacility(facility))
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Deletes a facility using the Controller.
     */
    private void deleteFacility(Facility facility) {
        String deviceId = retrieveDeviceId();
        if (deviceId == null || deviceId.isEmpty()) {
            Toast.makeText(this, "Device ID not found. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageUrl = facility.getImageUrl();

        controller.deleteFacility(imageUrl, new ManageFacilityController.ManageFacilitiesListener() {
            @Override
            public void onFacilityLoaded(Facility facility) {
                // Not used here
            }

            @Override
            public void onFacilityLoadFailed(Exception e) {
                // Not used here
            }

            @Override
            public void onFacilitySavedSuccessfully() {
                // Not used here
            }

            @Override
            public void onFacilitySaveFailed(Exception e) {
                // Not used here
            }

            @Override
            public void onFacilityDeletedSuccessfully() {
                runOnUiThread(() -> {
                    Toast.makeText(ManageFacilityView.this, "Facility deleted successfully", Toast.LENGTH_SHORT).show();
                    clearFacilityUI();
                });
            }

            @Override
            public void onFacilityDeleteFailed(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(ManageFacilityView.this, "Error deleting facility", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error deleting facility: ", e);
                });
            }

            @Override
            public void onImageDeletedSuccessfully() {
                Log.d(TAG, "Image deleted successfully.");
            }

            @Override
            public void onImageDeleteFailed(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(ManageFacilityView.this, "Error deleting image from Storage", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error deleting image from Storage: ", e);
                });
            }
        }, deviceId); // Pass the deviceId to the controller

    }
}
