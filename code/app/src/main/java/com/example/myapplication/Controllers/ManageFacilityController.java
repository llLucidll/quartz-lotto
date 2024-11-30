package com.example.myapplication.Controllers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.myapplication.Models.Facility;
import com.example.myapplication.Repositories.FacilityRepository;
import com.google.firebase.storage.StorageReference;

public class ManageFacilityController {

    private static final String TAG = "ManageFacilityController";
    private FacilityRepository repository;
    private Context context;

    public ManageFacilityController(Context context) {
        this.context = context;
        repository = new FacilityRepository();
    }

    // Listener Interface to communicate with the View
    public interface ManageFacilitiesListener {
        void onFacilityLoaded(Facility facility);
        void onFacilityLoadFailed(Exception e);
        void onFacilitySavedSuccessfully();
        void onFacilitySaveFailed(Exception e);
        void onFacilityDeletedSuccessfully();
        void onFacilityDeleteFailed(Exception e);
        void onImageDeletedSuccessfully();
        void onImageDeleteFailed(Exception e);
    }

    // Load Facility for the Given Device ID
    public void loadFacility(final ManageFacilitiesListener listener, String deviceId) {
        if (deviceId == null || deviceId.isEmpty()) {
            listener.onFacilityLoadFailed(new Exception("Device ID is not available."));
            return;
        }

        repository.loadFacility(deviceId, new FacilityRepository.LoadFacilityCallback() {
            @Override
            public void onSuccess(Facility facility) {
                Log.d(TAG, "Facility loaded successfully for deviceId: " + deviceId);
                listener.onFacilityLoaded(facility);
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFacilityLoadFailed(e);
            }
        });
    }

    // Save Facility Details
    public void saveFacility(String name, String location, Uri imageUri, final ManageFacilitiesListener listener, String deviceId) {
        if (deviceId == null || deviceId.isEmpty()) {
            listener.onFacilitySaveFailed(new Exception("Device ID is not available."));
            return;
        }

        if (imageUri != null) {
            repository.uploadImage(imageUri, deviceId, new FacilityRepository.UploadImageCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    Facility facility = new Facility(imageUrl, location, name, deviceId);
                    repository.saveFacility(facility, deviceId, new FacilityRepository.FirestoreCallback() {
                        @Override
                        public void onSuccess() {
                            listener.onFacilitySavedSuccessfully();
                            Log.d(TAG, "Facility saved successfully for deviceId: " + deviceId);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            listener.onFacilitySaveFailed(e);
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    listener.onImageDeleteFailed(e);
                }
            });
        } else {
            Facility facility = new Facility(null, location, name, deviceId);
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
    }

    // Delete Facility and Its Image
    public void deleteFacility(String imageUrl, final ManageFacilitiesListener listener, String deviceId) {
        if (deviceId == null || deviceId.isEmpty()) {
            listener.onFacilityDeleteFailed(new Exception("Device ID is not available."));
            return;
        }

        repository.deleteFacility(deviceId, new FacilityRepository.FirestoreCallback() {
            @Override
            public void onSuccess() {
                // After successful deletion from Firestore, delete the image if exists
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    StorageReference imageRef = repository.getStorageReference(imageUrl);
                    if (imageRef != null) {
                        imageRef.delete()
                                .addOnSuccessListener(aVoid -> {
                                    listener.onImageDeletedSuccessfully();
                                    listener.onFacilityDeletedSuccessfully();
                                })
                                .addOnFailureListener(e -> listener.onImageDeleteFailed(e));
                    } else {
                        listener.onFacilityDeletedSuccessfully();
                    }
                } else {
                    listener.onFacilityDeletedSuccessfully();
                }
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFacilityDeleteFailed(e);
            }
        });
    }
}
