package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GroupEntrantsActivity extends AppCompatActivity {

    private TextView textViewEntrants;
    private Button buttonSendNotification;
    private String groupType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_entrants);

        groupType = getIntent().getStringExtra("groupType");

        textViewEntrants = findViewById(R.id.textViewEntrants);
        buttonSendNotification = findViewById(R.id.buttonSendNotification);

        loadEntrantsList(groupType);

        buttonSendNotification.setOnClickListener(v -> sendNotificationToGroup());
    }

    private void loadEntrantsList(String groupType) {
        if (groupType.equals("waiting")) {
            textViewEntrants.setText("Waiting List Entrants:\n- Entrant 1\n- Entrant 2");
        } else if (groupType.equals("selected")) {
            textViewEntrants.setText("Selected Entrants:\n- Entrant 3\n- Entrant 4");
        } else if (groupType.equals("cancelled")) {
            textViewEntrants.setText("Cancelled Entrants:\n- Entrant 5\n- Entrant 6");
        }
    }

    private void sendNotificationToGroup() {
        Toast.makeText(this, "Notification sent to " + groupType + " entrants", Toast.LENGTH_SHORT).show();
    }
}
