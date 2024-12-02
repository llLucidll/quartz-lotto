package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

import com.example.myapplication.Views.WaitingListView;
import com.google.firebase.FirebaseApp;

/**
 * US 02.01.01 As an organizer I want to create a new event and generate a unique promotional QR code that links to the event description and event poster in the app
 * US 02.01.02 As an organizer I want to store the generated QR code in my database
 * US 02.06.01 As an organizer I want to view a list of all chosen entrants who are invited to apply
 * US 02.06.02 As an organizer I want to see a list of all the cancelled entrants
 * US 02.06.03 As an organizer I want to see a final list of entrants who enrolled for the event
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk=32)
public class EventDetailsActivityTest {

    private static final String TEST_EVENT_ID = "test_event_123";

    private ActivityController<EventDetailsActivity> activityController;
    private EventDetailsActivity activity;

    @Before
    public void setUp() {
        // Create an intent with the required "eventId" extra
        Intent intent = new Intent();
        intent.putExtra("eventId", TEST_EVENT_ID);
        Context context = ApplicationProvider.getApplicationContext();
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context);
        }

        // Build and create the activity
        activityController = Robolectric.buildActivity(EventDetailsActivity.class, intent)
                .create()
                .start()
                .resume()
                .visible();

        activity = activityController.get();
    }

    /**
     * Helper method to retrieve the currently displayed fragment.
     *
     * @return The currently active fragment in the fragment container.
     */
    private Fragment getCurrentFragment() {
        return activity.getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);
    }

    @Test
    public void clickingWaitlistButton_shouldLoadWaitlistFragment() {
        // Find the "Waitlist" button
        Button waitlistButton = activity.findViewById(R.id.buttonWaitlist);
        assertNotNull("Waitlist button should not be null", waitlistButton);

        // Perform click on the "Waitlist" button
        waitlistButton.performClick();

        // Retrieve the currently displayed fragment
        Fragment currentFragment = getCurrentFragment();
        assertNotNull("Current fragment should not be null after clicking Waitlist", currentFragment);

    }

    @Test
    public void clickingAttendeesButton_shouldLoadAttendeesFragment() {
        // Find the "Attendees" button
        Button attendeesButton = activity.findViewById(R.id.buttonAttendees);
        assertNotNull("Attendees button should not be null", attendeesButton);

        // Perform click on the "Attendees" button
        attendeesButton.performClick();

        // Retrieve the currently displayed fragment
        Fragment currentFragment = getCurrentFragment();
        assertNotNull("Current fragment should not be null after clicking Attendees", currentFragment);
    }

    @Test
    public void clickingLocationsButton_shouldLoadLocationsFragment() {
        // Find the "Locations" button
        Button locationsButton = activity.findViewById(R.id.buttonLocations);
        assertNotNull("Locations button should not be null", locationsButton);

        // Perform click on the "Locations" button
        locationsButton.performClick();

        // Retrieve the currently displayed fragment
        Fragment currentFragment = getCurrentFragment();
        assertNotNull("Current fragment should not be null after clicking Locations", currentFragment);

    }

    @Test
    public void clickingDetailsButton_shouldLoadDetailsFragment() {
        // Optionally test the default fragment or the "Details" button
        Button detailsButton = activity.findViewById(R.id.buttonDetails);
        assertNotNull("Details button should not be null", detailsButton);

        // Perform click on the "Details" button
        detailsButton.performClick();

        // Retrieve the currently displayed fragment
        Fragment currentFragment = getCurrentFragment();
        assertNotNull("Current fragment should not be null after clicking Details", currentFragment);

        // Verify that the current fragment is an instance of DetailsFragment
        assertTrue("Current fragment should be an instance of DetailsFragment",
                currentFragment instanceof DetailsFragment);
    }

    @Test
    public void activity_shouldLoadDefaultDetailsFragment_onCreate() {
        // Retrieve the currently displayed fragment
        Fragment currentFragment = getCurrentFragment();
        assertNotNull("Current fragment should not be null on activity creation", currentFragment);

        // Verify that the default fragment is an instance of DetailsFragment
        assertTrue("Default fragment should be an instance of DetailsFragment",
                currentFragment instanceof DetailsFragment);
    }

    @Test
    public void activity_shouldFinish_ifEventIdIsMissing() {
        // Build the activity without the "eventId" extra
        Intent intent = new Intent(); // No extras
        ActivityController<EventDetailsActivity> controller = Robolectric.buildActivity(EventDetailsActivity.class, intent)
                .create()
                .start()
                .resume()
                .visible();

        EventDetailsActivity activityWithoutEventId = controller.get();

        // Since eventId is missing, the activity should finish
        assertTrue("Activity should be finishing due to missing eventId",
                activityWithoutEventId.isFinishing());
    }
}
