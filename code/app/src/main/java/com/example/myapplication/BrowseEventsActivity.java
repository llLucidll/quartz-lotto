package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Controllers.BrowseEventsController;
import com.example.myapplication.Views.BrowseEventsView;

/**
 * Activity for browsing and managing events.
 * This activity fetches event data from Firestore and displays it in a RecyclerView.
 * Implements back navigation using toolbar and system back button.
 */
public class BrowseEventsActivity extends AppCompatActivity {

    private BrowseEventsView view;

    /**
     * Called when the activity is created.
     * Sets up the toolbar, RecyclerView, and initializes the view and controller.
     *
     * @param savedInstanceState The saved instance state from a previous activity instance, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);

        // Set up toolbar with back button
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

        // Initialize the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.event_recycler_view);

        // Create the controller and view
        BrowseEventsController controller = new BrowseEventsController();
        view = new BrowseEventsView(this, recyclerView, controller);

        // Set up toolbar with navigation callback
        view.setToolbar(toolbar, this::finish);

        // Load events from the database
        view.loadEvents();
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
