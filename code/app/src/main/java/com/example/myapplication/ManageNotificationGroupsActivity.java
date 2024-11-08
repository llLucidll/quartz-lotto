package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity to manage different notification groups.
 * Provides options to view and manage users in different notification groups.
 */
public class ManageNotificationGroupsActivity extends AppCompatActivity {

    private Button buttonWaitingList, buttonSelectedEntrants, buttonCancelledEntrants;

    /**
     * Initializes the activity, setting up the layout and button click listeners.
     * Each button corresponds to a different notification group.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_notification_groups);

        buttonWaitingList = findViewById(R.id.buttonWaitingList);
        buttonSelectedEntrants = findViewById(R.id.buttonSelectedEntrants);
        buttonCancelledEntrants = findViewById(R.id.buttonCancelledEntrants);

        // Set up click listeners for each button to open the appropriate group entrants activity
        buttonWaitingList.setOnClickListener(v -> openGroupEntrantsActivity("not chosen"));
        buttonSelectedEntrants.setOnClickListener(v -> openGroupEntrantsActivity("selected"));
        buttonCancelledEntrants.setOnClickListener(v -> openGroupEntrantsActivity("cancelled"));
    }

    /**
     * Opens the GroupEntrantsActivity with the specified group type.
     *
     * @param groupType The type of group (e.g., "not chosen", "selected", "cancelled") to be displayed in the GroupEntrantsActivity.
     */
    private void openGroupEntrantsActivity(String groupType) {
        Intent intent = new Intent(ManageNotificationGroupsActivity.this, GroupEntrantsActivity.class);
        intent.putExtra("groupType", groupType);
        startActivity(intent);
    }
}
