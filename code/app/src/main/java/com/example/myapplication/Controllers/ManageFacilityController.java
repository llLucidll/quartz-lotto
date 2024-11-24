// com/example/myapplication/controllers/ManageFacilitiesController.java

package com.example.myapplication.Controllers;

import android.net.Uri;

import com.example.myapplication.Models.Facility;
import com.example.myapplication.Repositories.FacilityRepository;
import com.google.firebase.storage.StorageReference;

public class ManageFacilityController {

    private FacilityRepository repository;

    public ManageFacilityController() {
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

    // Load Facility for the Current User
    public void loadFacility(final ManageFacilitiesListener listener) {
        repository.loadFacility(new FacilityRepository.LoadFacilityCallback() {
            @Override
            public void onSuccess(Facility facility) {
                listener.onFacilityLoaded(facility);
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFacilityLoadFailed(e);
            }
        });
    }

    // Save Facility Details
    public void saveFacility(String name, String location, Uri imageUri, final ManageFacilitiesListener listener) {
        if (imageUri != null) {
            repository.uploadImage(imageUri, new FacilityRepository.UploadImageCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    Facility facility = new Facility(imageUrl, location, name, repository.getCurrentUserId());
                    repository.saveFacility(facility, new FacilityRepository.FirestoreCallback() {
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

                @Override
                public void onFailure(Exception e) {
                    listener.onImageDeleteFailed(e); // Reusing the failure callback
                }
            });
        } else {
            Facility facility = new Facility(null, location, name, repository.getCurrentUserId());
            repository.saveFacility(facility, new FacilityRepository.FirestoreCallback() {
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
    public void deleteFacility(String imageUrl, final ManageFacilitiesListener listener) {
        repository.deleteFacility(new FacilityRepository.FirestoreCallback() {
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
