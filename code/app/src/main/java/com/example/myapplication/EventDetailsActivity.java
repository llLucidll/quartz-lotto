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

        // Set up button click listeners
        buttonDetails.setOnClickListener(v -> loadFragment(DetailsFragment.newInstance(eventId)));
        buttonWaitlist.setOnClickListener(v -> loadFragment(com.example.myapplication.Views.WaitingListView.newInstance(eventId)));
        buttonAttendees.setOnClickListener(v -> loadFragment(AttendeesFragment.newInstance(eventId)));
        buttonLocations.setOnClickListener(v -> loadFragment(LocationsFragment.newInstance(eventId)));

        // Load the default fragment (DetailsFragment)
        loadFragment(DetailsFragment.newInstance(eventId));
    }

    /**
     * Loads the specified fragment into the fragment container.
     *
     * @param fragment The fragment to load.
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }
}
