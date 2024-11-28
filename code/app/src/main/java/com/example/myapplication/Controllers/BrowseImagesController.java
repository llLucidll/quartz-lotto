package com.example.myapplication.Controllers;

import com.example.myapplication.Models.StorageImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Controller for managing image-related operations in Firebase Storage.
 * Handles fetching image metadata and URLs from specified folders.
 */
public class BrowseImagesController {

    private final FirebaseStorage storage;

    /**
     * Constructs a BrowseImagesController and initializes Firebase Storage.
     */
    public BrowseImagesController() {
        this.storage = FirebaseStorage.getInstance();
    }

    /**
     * Fetches all images from the specified folders in Firebase Storage.
     *
     * @param folders       Array of folder names to fetch images from.
     * @param onImageFetched Callback invoked when a new image is fetched. Provides the StorageImage object.
     * @param onComplete     Callback invoked when all folders have been processed. Indicates success or failure.
     */
    public void fetchImages(String[] folders, Consumer<StorageImage> onImageFetched, BiConsumer<Boolean, String> onComplete) {
        final int totalFolders = folders.length;
        final int[] completedFolders = {0};

        for (String folder : folders) {
            StorageReference folderRef = storage.getReference().child(folder);
            folderRef.listAll()
                    .addOnSuccessListener(listResult -> {
                        for (StorageReference item : listResult.getItems()) {
                            item.getDownloadUrl().addOnSuccessListener(uri -> {
                                StorageImage image = new StorageImage(
                                        item.getName(),
                                        item.getPath(),
                                        uri.toString()
                                );
                                onImageFetched.accept(image);
                            }).addOnFailureListener(e -> onComplete.accept(false, "Failed to get URL for " + item.getName()));
                        }
                    })
                    .addOnFailureListener(e -> onComplete.accept(false, "Failed to list folder: " + folder))
                    .addOnCompleteListener(task -> {
                        completedFolders[0]++;
                        if (completedFolders[0] == totalFolders) {
                            onComplete.accept(true, null);
                        }
                    });
        }
    }
}
