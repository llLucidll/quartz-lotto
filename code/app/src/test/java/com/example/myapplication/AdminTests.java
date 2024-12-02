package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

import com.google.firebase.FirebaseApp;

/**
 * US 03.04.01 As an administrator, I want to be able to browse events.
 * US 03.05.01 As an administrator, I want to be able to browse profiles.
 * US 03.06.01 As an administrator, I want to be able to browse images.
 * Also, allows for browsing hashQR data and facilities
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk=32)
public class AdminTests {

    private ActivityController<AdminProfileActivity> activityController;
    private AdminProfileActivity activity;

    @Before
    public void setUp() {
        // Build and create the activity
        Context context = ApplicationProvider.getApplicationContext();
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context);
        }
        activityController = Robolectric.buildActivity(AdminProfileActivity.class)
                .create()
                .start()
                .resume()
                .visible();

        activity = activityController.get();
    }

    /**
     * Helper method to retrieve ShadowActivity for intent verification.
     *
     * @return ShadowActivity of the current activity.
     */
    private ShadowActivity getShadowActivity() {
        return shadowOf(activity);
    }

    /**
     * Test that clicking buttonBrowseUsers starts BrowseUsersActivity.
     */
    @Test
    public void clickingBrowseUsersButton_shouldStartBrowseUsersActivity() {
        // Find the "Browse Users" button
        Button browseUsersButton = activity.findViewById(R.id.button_browse_user_profiles);
        assertNotNull("Browse Users button should not be null", browseUsersButton);

        // Perform click on the "Browse Users" button
        browseUsersButton.performClick();

        // Retrieve the next started activity intent
        Intent expectedIntent = new Intent(activity, BrowseUsersActivity.class);
        ShadowActivity shadowActivity = getShadowActivity();
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        // Verify that the intent was started
        assertNotNull("Intent should have been started", actualIntent);

    }

    /**
     * Test that clicking buttonBrowseEvents starts BrowseEventsActivity.
     */
    @Test
    public void clickingBrowseEventsButton_shouldStartBrowseEventsActivity() {
        // Find the "Browse Events" button
        Button browseEventsButton = activity.findViewById(R.id.button_browse_events);
        assertNotNull("Browse Events button should not be null", browseEventsButton);

        // Perform click on the "Browse Events" button
        browseEventsButton.performClick();

        // Retrieve the next started activity intent
        Intent expectedIntent = new Intent(activity, BrowseEventsActivity.class);
        ShadowActivity shadowActivity = getShadowActivity();
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        // Verify that the intent was started
        assertNotNull("Intent should have been started", actualIntent);

    }

    /**
     * Test that clicking buttonBrowseFacilities starts BrowseFacilitiesActivity.
     */
    @Test
    public void clickingBrowseFacilitiesButton_shouldStartBrowseFacilitiesActivity() {
        // Find the "Browse Facilities" button
        Button browseFacilitiesButton = activity.findViewById(R.id.button_browse_facilities);
        assertNotNull("Browse Facilities button should not be null", browseFacilitiesButton);

        // Perform click on the "Browse Facilities" button
        browseFacilitiesButton.performClick();

        // Retrieve the next started activity intent
        Intent expectedIntent = new Intent(activity, BrowseFacilitiesActivity.class);
        ShadowActivity shadowActivity = getShadowActivity();
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        // Verify that the intent was started
        assertNotNull("Intent should have been started", actualIntent);

    }

    /**
     * Test that clicking buttonBrowseImages starts BrowseImagesActivity.
     */
    @Test
    public void clickingBrowseImagesButton_shouldStartBrowseImagesActivity() {
        // Find the "Browse Images" button
        Button browseImagesButton = activity.findViewById(R.id.button_browse_images);
        assertNotNull("Browse Images button should not be null", browseImagesButton);

        // Perform click on the "Browse Images" button
        browseImagesButton.performClick();

        // Retrieve the next started activity intent
        Intent expectedIntent = new Intent(activity, BrowseImagesActivity.class);
        ShadowActivity shadowActivity = getShadowActivity();
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        // Verify that the intent was started
        assertNotNull("Intent should have been started", actualIntent);

    }

    /**
     * Test that clicking buttonBrowseQR starts ManageQrLinksActivity.
     */
    @Test
    public void clickingBrowseQRButton_shouldStartManageQrLinksActivity() {
        // Find the "Browse QR" button
        Button browseQRButton = activity.findViewById(R.id.button_browse_qrhashdata);
        assertNotNull("Browse QR button should not be null", browseQRButton);

        // Perform click on the "Browse QR" button
        browseQRButton.performClick();

        // Retrieve the next started activity intent
        Intent expectedIntent = new Intent(activity, ManageQrLinksActivity.class);
        ShadowActivity shadowActivity = getShadowActivity();
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        // Verify that the intent was started
        assertNotNull("Intent should have been started", actualIntent);

    }

    /**
     * Test that clicking myEventsButton starts HomeFragment as an activity.
     * Note: In the provided code, HomeFragment is started as an activity which might be a mistake.
     * If HomeFragment is indeed an activity, otherwise adjust accordingly.
     */
    @Test
    public void clickingMyEventsButton_shouldStartHomeFragmentActivity() {
        // Find the "My Events" button
        Button myEventsButton = activity.findViewById(R.id.my_events_button);
        assertNotNull("My Events button should not be null", myEventsButton);

        // Perform click on the "My Events" button
        myEventsButton.performClick();

        // Retrieve the next started activity intent
        Intent expectedIntent = new Intent(activity, HomeFragment.class);
        ShadowActivity shadowActivity = getShadowActivity();
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        // Verify that the intent was started
        assertNotNull("Intent should have been started", actualIntent);

    }
}
