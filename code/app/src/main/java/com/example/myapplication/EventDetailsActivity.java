// File: com/example/myapplication/EventDetailsActivity.java
package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * Activity to display event details and handle fragment switching with buttons.
 */
public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";
    private String eventId;
    private ProgressBar progressBar;

    // Buttons
    private Button buttonDetails;
    private Button buttonWaitlist;
    private Button buttonAttendees;
    private Button buttonLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details); // Your XML layout file

        // Retrieve eventId from intent
        eventId = "H8CEhmiH2BfiUXXDvcJa";//getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Event ID missing.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "eventId is null in onCreate");
            finish();
            return;
        }
        Log.d(TAG, "Retrieved eventId: " + eventId);

        // Initialize views
        progressBar = findViewById(R.id.detailsProgressBar);
        buttonDetails = findViewById(R.id.buttonDetails);
        buttonWaitlist = findViewById(R.id.buttonWaitlist);
        buttonAttendees = findViewById(R.id.buttonAttendees);
        buttonLocations = findViewById(R.id.buttonLocations);

<<<<<<< HEAD
        // Set up button click listeners
        buttonDetails.setOnClickListener(v -> loadFragment(DetailsFragment.newInstance(eventId)));
        buttonWaitlist.setOnClickListener(v -> loadFragment(com.example.myapplication.Views.WaitingListView.newInstance(eventId)));
        buttonAttendees.setOnClickListener(v -> loadFragment(AttendeesFragment.newInstance(eventId)));
        buttonLocations.setOnClickListener(v -> loadFragment(LocationsFragment.newInstance(eventId)));
=======
        // Set up button click listeners with tags
        buttonDetails.setOnClickListener(v -> loadFragment(DetailsFragment.newInstance(eventId), "DetailsFragmentTag"));
        buttonWaitlist.setOnClickListener(v -> loadFragment(WaitlistFragment.newInstance(eventId), "WaitlistFragmentTag"));
        buttonAttendees.setOnClickListener(v -> loadFragment(AttendeesFragment.newInstance(eventId), "AttendeesFragmentTag"));
        buttonLocations.setOnClickListener(v -> loadFragment(LocationsFragment.newInstance(eventId), "LocationsFragmentTag"));
>>>>>>> 57fdcd469e803140d9d05008b45cd63bf26c0b01

        // Load the default fragment (DetailsFragment)
        loadFragment(DetailsFragment.newInstance(eventId), "DetailsFragmentTag");
    }

    /**
     * Loads the specified fragment into the fragment container with a unique tag.
     *
     * @param fragment The fragment to load.
     * @param tag      The unique tag for the fragment.
     */
    private void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment, tag);
        transaction.commit();
    }

    /**
     * Refreshes the attendees list and updates the map.
     * This method is called after an attendee is canceled.
     */
    public void refreshAttendees() {
        // Refresh the AttendeesFragment
        AttendeesFragment attendeesFragment = (AttendeesFragment) getSupportFragmentManager()
                .findFragmentByTag("AttendeesFragmentTag");
        if (attendeesFragment != null) {
            attendeesFragment.fetchAttendees();
        } else {
            Log.e(TAG, "AttendeesFragment not found. Cannot refresh attendees.");
        }

        // Refresh the LocationsFragment
        LocationsFragment locationsFragment = (LocationsFragment) getSupportFragmentManager()
                .findFragmentByTag("LocationsFragmentTag");
        if (locationsFragment != null) {
            locationsFragment.updateMapMarkers();
        } else {
            Log.e(TAG, "LocationsFragment not found. Cannot update map markers.");
        }
    }
}
