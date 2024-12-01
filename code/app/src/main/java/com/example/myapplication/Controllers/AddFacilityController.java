package com.example.myapplication.Controllers;

import android.net.Uri;

import com.example.myapplication.Models.Facility;
import com.example.myapplication.Repositories.FacilityRepository;

public class AddFacilityController {

    private FacilityRepository repository;

    public AddFacilityController() {
        repository = new FacilityRepository();
    }

    // Listener Interface to communicate with the View
    public interface AddFacilityListener {
        void onFacilitySavedSuccessfully();
        void onFacilitySaveFailed(Exception e);
        void onFacilityLoaded(Facility facility);
        void onFacilityLoadFailed(Exception e);
        void onImageUploaded(String imageUrl);
        void onImageUploadFailed(Exception e);
    }

    /**
     * Saves the facility details.
     *
     * @param name      The name of the facility.
     * @param location  The location of the facility.
     * @param imageUri  The URI of the facility image. Can be null if no image is provided.
     * @param listener  The listener to handle callbacks.
     * @param deviceId  The device ID used as the unique identifier for the user.
     */
    public void saveFacility(String name, String location, Uri imageUri, final AddFacilityListener listener, String deviceId) {
        if (deviceId == null || deviceId.isEmpty()) {
            listener.onFacilitySaveFailed(new Exception("Device ID not found. Cannot save facility."));
            return;
        }

        if (imageUri != null) {
            // Upload the image first
            repository.uploadImage(imageUri, deviceId, new FacilityRepository.UploadImageCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    // Proceed to save the facility with the uploaded image URL
                    proceedToSave(name, location, imageUrl, listener, deviceId);
                }

                @Override
                public void onFailure(Exception e) {
                    // Notify the listener about the image upload failure
                    listener.onImageUploadFailed(e);
                }
            });
        } else {
            // No image to upload, proceed to save the facility without an image URL
            proceedToSave(name, location, null, listener, deviceId);
        }
    }

    /**
     * Helper method to save the facility details after handling the image upload.
     *
     * @param name       The name of the facility.
     * @param location   The location of the facility.
     * @param imageUrl   The URL of the uploaded image. Can be null if no image is provided.
     * @param listener   The listener to handle callbacks.
     * @param deviceId   The device ID used as the unique identifier for the user.
     */
    private void proceedToSave(String name, String location, String imageUrl, AddFacilityListener listener, String deviceId) {
        // Create a Facility object using deviceId as the facility ID
        Facility facility = new Facility(imageUrl, location, name, deviceId);

        // Save the facility using the repository
        repository.saveFacility(facility, deviceId, new FacilityRepository.FirestoreCallback() {
            @Override
            public void onSuccess() {
                listener.onFacilitySavedSuccessfully();
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFacilitySaveFailed(e);
            }
        });
    }

    /**
     * Loads the facility details for the current user.
     *
     * @param listener The listener to handle callbacks.
     * @param deviceId The device ID used to fetch facilities.
     */
    public void loadFacility(final AddFacilityListener listener, String deviceId) {
        if (deviceId == null || deviceId.isEmpty()) {
            listener.onFacilityLoadFailed(new Exception("Device ID not found. Cannot load facility."));
            return;
        }

        repository.loadFacility(deviceId, new FacilityRepository.LoadFacilityCallback() {
            @Override
            public void onSuccess(Facility facility) {
                // Facility found, return it via the listener
                listener.onFacilityLoaded(facility);
            }

            @Override
            public void onFailure(Exception e) {
                // Facility not found, notify listener to display the Toast
                listener.onFacilityLoadFailed(new Exception("Please make a facility to become an organizer."));
            }
        });
    }
}
