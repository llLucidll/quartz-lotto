package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//Activity which hosts the User Profile page.
/*
Profile page has:
    Checkboxes for notifications
    Profile Picture editing
    Notification preferences.
    fields for name, email, dob
    country/region selection.
 */

//Can alternatively move notification checkboxes into a separate page for aesthetic purposes.
public class UserProfileActivity extends AppCompatActivity {
    private CheckBox receiveNotifications;
    private CheckBox chosenFromWaitingList;
    private CheckBox notChosenFromWaitingList;
    private Button savePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_page); //TODO replace with profile_page.xml

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
            user.setReceiveNotifications(receive);

            //receive notification if not chosen/not chosen
            user.setChosenFromWaitingList(chosen);
            user.setNotChosenFromWaitingList(notChosen);

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
