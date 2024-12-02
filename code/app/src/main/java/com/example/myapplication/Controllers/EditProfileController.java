// com/example/myapplication/controllers/EditProfileController.java

package com.example.myapplication.Controllers;

import android.net.Uri;

import com.example.myapplication.Models.User;
import com.example.myapplication.Repositories.UserRepository;

/**
 * Controller class for editing the user's profile.
 */
public class EditProfileController {

    private UserRepository repository;

    public EditProfileController() {
        repository = new UserRepository();
    }

    // Listener Interface to communicate with the View
    public interface EditProfileListener {
        void onProfileLoaded(User user);
        void onProfileLoadFailed(Exception e);
        void onProfileSavedSuccessfully();
        void onProfileSaveFailed(Exception e);
        void onImageUploaded(String imageUrl);
        void onImageUploadFailed(Exception e);
        void onImageDeletedSuccessfully();
        void onImageDeleteFailed(Exception e);
    }

    /**
     * Loads the current user's profile.
     *
     * @param listener The listener to handle callbacks.
     */
    public void loadUserProfile(final EditProfileListener listener) {
        repository.loadUser(new UserRepository.LoadUserCallback() {
            @Override
            public void onSuccess(User user) {
                listener.onProfileLoaded(user);
            }

            @Override
            public void onFailure(Exception e) {
                listener.onProfileLoadFailed(e);
            }
        });
    }

    /**
     * Saves the user's profile details.
     *
     * @param user      The User object containing updated details.
     * @param imageUri  The URI of the new profile image. Can be null if no image is updated.
     * @param listener  The listener to handle callbacks.
     */
    public void saveUserProfile(User user, Uri imageUri, final EditProfileListener listener) {
        if (imageUri != null) {
            repository.uploadProfileImage(imageUri, new UserRepository.UploadImageCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    user.setProfileImageUrl(imageUrl);
                    repository.saveUser(user, new UserRepository.FirestoreCallback() {
                        @Override
                        public void onSuccess() {
                            listener.onProfileSavedSuccessfully();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            listener.onProfileSaveFailed(e);
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    listener.onImageUploadFailed(e);
                }
            });
        } else {
            repository.saveUser(user, new UserRepository.FirestoreCallback() {
                @Override
                public void onSuccess() {
                    listener.onProfileSavedSuccessfully();
                }

                @Override
                public void onFailure(Exception e) {
                    listener.onProfileSaveFailed(e);
                }
            });
        }
    }

    /**
     * Deletes the user's profile image.
     *
     * @param listener The listener to handle callbacks.
     */
    public void deleteProfileImage(final EditProfileListener listener) {
        repository.deleteProfileImage(new UserRepository.FirestoreCallback() {
            @Override
            public void onSuccess() {
                listener.onImageDeletedSuccessfully();
            }

            @Override
            public void onFailure(Exception e) {
                listener.onImageDeleteFailed(e);
            }
        });
    }
}
