package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * Activity displaying detailed information about an event with navigable options.
 */
public class EventDetailsActivity extends BaseActivity {
    private Toolbar toolbar;
    private Button buttonDetails, buttonWaitlist, buttonAttendees, buttonLocations;
    private FrameLayout fragmentContainer;
    private String eventId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Initialize views
        toolbar = findViewById(R.id.detailsToolbar);
        setSupportActionBar(toolbar);

        // Enable the back button in the toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        buttonDetails = findViewById(R.id.buttonDetails);
        buttonWaitlist = findViewById(R.id.buttonWaitlist);
        buttonAttendees = findViewById(R.id.buttonAttendees);
        buttonLocations = findViewById(R.id.buttonLocations);
        fragmentContainer = findViewById(R.id.fragmentContainer);
        progressBar = findViewById(R.id.detailsProgressBar);

        // Retrieve eventId from intent
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Event ID missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set default fragment to DetailsFragment
        loadFragment(DetailsFragment.newInstance(eventId));

        // Set up button listeners
        buttonDetails.setOnClickListener(v -> loadFragment(DetailsFragment.newInstance(eventId)));

        buttonWaitlist.setOnClickListener(v -> {
            loadFragment(WaitlistFragment.newInstance(eventId));
        });

        buttonAttendees.setOnClickListener(v -> {
            loadFragment(AttendeesFragment.newInstance(eventId));
        });

        buttonLocations.setOnClickListener(v -> {
            loadFragment(LocationsFragment.newInstance(eventId));
        });
    }

    /**
     * Loads the specified fragment into the fragment container.
     *
     * @param fragment The fragment to load.
     */
    private void loadFragment(Fragment fragment) {
        // Show progress bar while loading
        progressBar.setVisibility(View.VISIBLE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();

        // Hide progress bar after a short delay to simulate loading
        // In a real application, manage the visibility based on actual loading states
        fragment.getViewLifecycleOwnerLiveData().observe(this, lifecycleOwner -> {
            progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back button in the toolbar
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity and go back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
