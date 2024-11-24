// com/example/myapplication/controllers/AddFacilityController.java

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
     */
    public void saveFacility(String name, String location, Uri imageUri, final AddFacilityListener listener) {
        if (imageUri != null) {
            // Upload the image first
            repository.uploadImage(imageUri, new FacilityRepository.UploadImageCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    // Proceed to save the facility with the uploaded image URL
                    proceedToSave(name, location, imageUrl, listener);
                }

                @Override
                public void onFailure(Exception e) {
                    // Notify the listener about the image upload failure
                    listener.onImageUploadFailed(e);
                }
            });
        } else {
            // No image to upload, proceed to save the facility without an image URL
            proceedToSave(name, location, null, listener);
        }
    }

    /**
     * Helper method to save the facility details after handling the image upload.
     *
     * @param name       The name of the facility.
     * @param location   The location of the facility.
     * @param imageUrl   The URL of the uploaded image. Can be null if no image is provided.
     * @param listener   The listener to handle callbacks.
     */
    private void proceedToSave(String name, String location, String imageUrl, AddFacilityListener listener) {
        String userId = repository.getCurrentUserId();
        if (userId == null) {
            // User is not authenticated
            listener.onFacilitySaveFailed(new Exception("User not authenticated"));
            return;
        }

        // Create a Facility object using userId as the facility ID
        Facility facility = new Facility(imageUrl, location, name, userId);

        // Save the facility using the repository
        repository.saveFacility(facility, new FacilityRepository.FirestoreCallback() {
            @Override
            public void onSuccess() {
                // Notify the listener about the successful save
                listener.onFacilitySavedSuccessfully();
            }

            @Override
            public void onFailure(Exception e) {
                // Notify the listener about the save failure
                listener.onFacilitySaveFailed(e);
            }
        });
    }

    /**
     * Loads the facility details for the current user.
     *
     * @param listener The listener to handle callbacks.
     */
    public void loadFacility(final AddFacilityListener listener) {
        repository.loadFacility(new FacilityRepository.LoadFacilityCallback() {
            @Override
            public void onSuccess(Facility facility) {
                // Notify the listener with the loaded facility
                listener.onFacilityLoaded(facility);
            }

            @Override
            public void onFailure(Exception e) {
                // Notify the listener about the load failure
                listener.onFacilityLoadFailed(e);
            }
        });
    }
}
