package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ManageNotificationGroupsActivity extends AppCompatActivity {

    private Button buttonWaitingList, buttonSelectedEntrants, buttonCancelledEntrants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_notification_groups);

        buttonWaitingList = findViewById(R.id.buttonWaitingList);
        buttonSelectedEntrants = findViewById(R.id.buttonSelectedEntrants);
        buttonCancelledEntrants = findViewById(R.id.buttonCancelledEntrants);

        buttonWaitingList.setOnClickListener(v -> openGroupEntrantsActivity("waiting"));
        buttonSelectedEntrants.setOnClickListener(v -> openGroupEntrantsActivity("selected"));
        buttonCancelledEntrants.setOnClickListener(v -> openGroupEntrantsActivity("cancelled"));
    }

    private void openGroupEntrantsActivity(String groupType) {
        Intent intent = new Intent(ManageNotificationGroupsActivity.this, GroupEntrantsActivity.class);
        intent.putExtra("groupType", groupType);
        startActivity(intent);
    }
}
