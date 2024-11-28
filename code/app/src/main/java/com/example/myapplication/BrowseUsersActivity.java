package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Controllers.BrowseUsersController;
import com.example.myapplication.Views.BrowseUsersView;

/**
 * Activity for browsing a list of users.
 * Integrates the view and controller to implement the MVC pattern.
 */
public class BrowseUsersActivity extends BaseActivity {

    private BrowseUsersView view;

    /**
     * Initializes the activity, sets up the toolbar, and connects the view and controller.
     *
     * @param savedInstanceState The saved instance state from a previous activity instance, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_users);

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

        // Retrieve current user ID from BaseActivity
        String currentUserId = retrieveDeviceId();
        if (currentUserId == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.user_recycler_view);

        // Set up the view and controller
        BrowseUsersController controller = new BrowseUsersController();
        view = new BrowseUsersView(this, recyclerView, controller, currentUserId);

        // Set toolbar navigation
        view.setToolbar(toolbar, this::finish);

        // Load users
        view.loadUsers();
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
