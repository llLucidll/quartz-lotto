package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Controllers.BrowseImagesController;
import com.example.myapplication.Views.BrowseImagesView;

/**
 * Activity for browsing a list of images.
 * Integrates the view and controller to implement the MVC pattern.
 */
public class BrowseImagesActivity extends AppCompatActivity {

    private BrowseImagesView view;

    /**
     * Initializes the activity, sets up the toolbar, and connects the view and controller.
     *
     * @param savedInstanceState The saved instance state from a previous activity instance, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_images);

        // Initialize toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Browse Images");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Handle system back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(); // Close the activity
            }
        });

        // Initialize UI components
        RecyclerView imagesRecyclerView = findViewById(R.id.images_recycler_view);
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        // Set up view and controller
        BrowseImagesController controller = new BrowseImagesController();
        view = new BrowseImagesView(this, imagesRecyclerView, progressBar, controller, 2);

        // Set toolbar navigation
        view.setToolbar(toolbar, this::finish);

        // Load images from specified folders
        String[] folders = {"profile_images", "facility_images", "posters"};
        view.loadImages(folders);
    }

    /**
     * Handles toolbar back button presses.
     *
     * @param item The selected menu item.
     * @return True if the action was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity when the toolbar back button is pressed
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
