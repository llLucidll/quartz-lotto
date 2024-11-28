package com.example.myapplication.Views;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ImageAdapter;
import com.example.myapplication.Models.StorageImage;
import com.example.myapplication.Controllers.BrowseImagesController;

import java.util.List;

/**
 * View for displaying a list of images in a RecyclerView with progress feedback.
 */
public class BrowseImagesView {

    private final Context context;
    private final RecyclerView imagesRecyclerView;
    private final ProgressBar progressBar;
    private final ImageAdapter imageAdapter;
    private final BrowseImagesController controller;

    /**
     * Constructs a BrowseImagesView.
     *
     * @param context         The context of the activity.
     * @param recyclerView    The RecyclerView to display images.
     * @param progressBar     The ProgressBar for loading indication.
     * @param controller      The controller for managing image-related operations.
     * @param numberOfColumns The number of columns in the RecyclerView grid.
     */
    public BrowseImagesView(Context context, RecyclerView recyclerView, ProgressBar progressBar,
                            BrowseImagesController controller, int numberOfColumns) {
        this.context = context;
        this.imagesRecyclerView = recyclerView;
        this.progressBar = progressBar;
        this.controller = controller;

        imageAdapter = new ImageAdapter(context, new java.util.ArrayList<>());
        imagesRecyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));
        imagesRecyclerView.setAdapter(imageAdapter);
    }

    /**
     * Configures the toolbar for back navigation.
     *
     * @param toolbar       The toolbar to set up.
     * @param onBackPressed The action to perform when the back button is pressed.
     */
    public void setToolbar(Toolbar toolbar, Runnable onBackPressed) {
        toolbar.setNavigationOnClickListener(v -> onBackPressed.run());
    }

    /**
     * Loads images from the specified folders and displays them in the RecyclerView.
     *
     * @param folders Array of folder names to fetch images from.
     */
    public void loadImages(String[] folders) {
        progressBar.setVisibility(View.VISIBLE);

        // Start with an empty list
        imageAdapter.getImageList().clear();
        imageAdapter.notifyDataSetChanged();

        controller.fetchImages(folders, image -> {
            // Add image to the adapter and notify the RecyclerView
            imageAdapter.getImageList().add(image);
            imageAdapter.notifyItemInserted(imageAdapter.getImageList().size() - 1);
        }, (success, errorMessage) -> {
            if (!success && errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }

            // When all folders are processed, check if the list is still empty
            if (success && imageAdapter.getImageList().isEmpty()) {
                Toast.makeText(context, "All images loaded.", Toast.LENGTH_SHORT).show();
            }

            // Hide the progress bar after all folders are processed
            progressBar.setVisibility(View.GONE);
        });
    }


}
