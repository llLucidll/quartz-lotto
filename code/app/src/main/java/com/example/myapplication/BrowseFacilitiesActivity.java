package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Controllers.BrowseFacilitiesController;
import com.example.myapplication.Views.BrowseFacilitiesView;

/**
 * Activity for browsing a list of facilities.
 * Integrates the view and controller to implement the MVC pattern.
 */
public class BrowseFacilitiesActivity extends AppCompatActivity {

    private BrowseFacilitiesView view;

    /**
     * Initializes the activity, sets up the toolbar, and connects the view and controller.
     *
     * @param savedInstanceState The saved instance state from a previous activity instance, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_facilities);

        // Initialize toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Handle system back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(); // Close the activity
            }
        });

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.facility_recycler_view);

        // Set up view and controller
        BrowseFacilitiesController controller = new BrowseFacilitiesController();
        view = new BrowseFacilitiesView(this, recyclerView, controller);

        // Set toolbar navigation
        view.setToolbar(toolbar, this::finish);

        // Load facilities
        view.loadFacilities();
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
