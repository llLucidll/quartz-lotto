package com.example.myapplication;

import android.content.Intent;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 32) // Use the SDK version compatible with your app
public class ManageNotificationGroupsActivityTest {

    private ManageNotificationGroupsActivity activity;
    private Button buttonWaitingList, buttonSelectedEntrants, buttonCancelledEntrants;

    @Before
    public void setUp() {
        // Build the activity
        activity = Robolectric.buildActivity(ManageNotificationGroupsActivity.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();

        // Initialize UI components
        buttonWaitingList = activity.findViewById(R.id.buttonWaitingList);
        buttonSelectedEntrants = activity.findViewById(R.id.buttonSelectedEntrants);
        buttonCancelledEntrants = activity.findViewById(R.id.buttonCancelledEntrants);
    }

    @After
    public void tearDown() {
        activity = null;
    }

    @Test
    public void testClickButtonWaitingList_shouldStartGroupEntrantsActivityWithNotChosen() {
        // Act
        buttonWaitingList.performClick();

        // Assert
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertNotNull("Intent should have been started", startedIntent);
        assertEquals(GroupEntrantsActivity.class.getCanonicalName(), startedIntent.getComponent().getClassName());
        assertEquals("not chosen", startedIntent.getStringExtra("groupType"));
    }

    @Test
    public void testClickButtonSelectedEntrants_shouldStartGroupEntrantsActivityWithSelected() {
        // Act
        buttonSelectedEntrants.performClick();

        // Assert
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertNotNull("Intent should have been started", startedIntent);
        assertEquals(GroupEntrantsActivity.class.getCanonicalName(), startedIntent.getComponent().getClassName());
        assertEquals("selected", startedIntent.getStringExtra("groupType"));
    }

    @Test
    public void testClickButtonCancelledEntrants_shouldStartGroupEntrantsActivityWithCancelled() {
        // Act
        buttonCancelledEntrants.performClick();

        // Assert
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertNotNull("Intent should have been started", startedIntent);
        assertEquals(GroupEntrantsActivity.class.getCanonicalName(), startedIntent.getComponent().getClassName());
        assertEquals("cancelled", startedIntent.getStringExtra("groupType"));
    }

}
