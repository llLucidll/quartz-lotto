package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {
    private CheckBox receiveNotifications;
    private CheckBox chosenFromWaitingList;
    private CheckBox notChosenFromWaitingList;
    private Button savePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        receiveNotifications = findViewById(R.id.checkbox_receive_notifications);
        chosenFromWaitingList = findViewById(R.id.checkbox_chosen_from_waiting_list);
        notChosenFromWaitingList = findViewById(R.id.checkbox_not_chosen_from_waiting_list);
        savePreferences = findViewById(R.id.button_save_preferences);

        //Save button
        savePreferences.setOnClickListener(v -> {
            boolean receive = receiveNotifications.isChecked();
            boolean chosen = chosenFromWaitingList.isChecked();
            boolean notChosen = notChosenFromWaitingList.isChecked();

            //Updating UserProfile permissions with checkbox values on save click.
            UserProfile user = getUserProfile(); // Assume this retrieves the current user profile
            if (receive) {
                NotificationService.optInToNotifications(this, user);
            } else {
                NotificationService.optOutOfNotifications(user);
            }

            // Save Confirmation toast.
            Toast.makeText(this, "Preferences saved", Toast.LENGTH_SHORT).show();
        });
    }

    // Example method to get the current user profile TODO
    private UserProfile getUserProfile() {
        // Dummy implementation to demonstrate fetching the user profile TODO
        return new UserProfile();
    }
}

