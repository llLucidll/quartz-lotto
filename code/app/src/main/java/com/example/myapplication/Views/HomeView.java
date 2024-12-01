package com.example.myapplication.Views;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.BaseActivity;
import com.example.myapplication.Controllers.HomePageController;
import com.example.myapplication.EventAdapter;
import com.example.myapplication.Models.Event;
import com.example.myapplication.Models.Facility;
import com.example.myapplication.R;
import com.example.myapplication.Repositories.HomeRepository;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeView extends AppCompatActivity {
    private ListView selectedEventsListView;
    private ListView waitlistEventsListView;

    private HomePageController selectedEventsAdapter;
    private HomePageController waitlistEventsAdapter;

    private List<Event> selectedEvents;
    private List<Event> waitlistEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        selectedEventsListView = findViewById(R.id.selected_events_list);
        waitlistEventsListView = findViewById(R.id.entrant_waitlist);

        selectedEvents = new ArrayList<>();
        selectedEventsAdapter = new HomePageController(this, selectedEvents);
        selectedEventsListView.setAdapter(selectedEventsAdapter);

        waitlistEvents = new ArrayList<>();
        waitlistEventsAdapter = new HomePageController(this, waitlistEvents);
        waitlistEventsListView.setAdapter(waitlistEventsAdapter);

        // Initialize HomeRepository and fetch waitlist events
        updateWaitlistEvents(waitlistEvents);

    }



    // Method to update waitlist events in the UI
    public void updateWaitlistEvents(List<Event> waitlistEvents) {
        HomeRepository homeRepository = new HomeRepository();
        homeRepository.fetchWaitingDevices(this);
        this.waitlistEvents.clear(); // Clear existing waitlist
        this.waitlistEvents.addAll(waitlistEvents); // Add new events to the waitlist
        waitlistEventsAdapter.notifyDataSetChanged(); // Notify adapter to refresh UI
    }
}
