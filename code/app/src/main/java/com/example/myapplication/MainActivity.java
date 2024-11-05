package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        NotificationUtils.createNotificationChannel(this); //Creating channel for notifications


        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up button to open OrganizerProfileActivity
        Button buttonOpenProfile = findViewById(R.id.buttonOpenProfile);
        buttonOpenProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrganizerProfileActivity.class);
            startActivity(intent);
        });

        //Test button to open NotificationActivity
        //TODO REMOVE AND MOVE TO AYESHAS USER PROFILE PAGE
        Button buttonNotif = findViewById(R.id.TEST);
        buttonNotif.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
    }
}
