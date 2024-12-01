package com.example.myapplication.Views;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.SelectedEventsAdapter;
import com.example.myapplication.Controllers.HomePageController;
import com.example.myapplication.Models.Event;
import com.example.myapplication.R;
import com.example.myapplication.Repositories.HomeRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeView extends AppCompatActivity {
    private ListView selectedEventsListView;
    private ListView waitlistEventsListView;

    private SelectedEventsAdapter selectedEventsAdapter;
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
        waitlistEvents = new ArrayList<>();

        selectedEventsAdapter = new SelectedEventsAdapter(this, selectedEvents);
        selectedEventsListView.setAdapter(selectedEventsAdapter);

        waitlistEventsAdapter = new HomePageController(this, waitlistEvents);
        waitlistEventsListView.setAdapter(waitlistEventsAdapter);

        // Fetch events
        fetchWaitlistEvents();
        fetchSelectedEvents();
    }

    private void fetchWaitlistEvents() {
        HomeRepository homeRepository = new HomeRepository(this);
        homeRepository.fetchWaitlistEvents(this);
    }

    private void fetchSelectedEvents() {
        HomeRepository homeRepository = new HomeRepository(this);
        homeRepository.fetchSelectedEvents(this);
    }

    /**
     * Updates the waitlist events in the UI.
     */
    public void updateWaitlistEvents(List<Event> fetchedWaitlistEvents) {
        this.waitlistEvents.clear();
        this.waitlistEvents.addAll(fetchedWaitlistEvents);
        waitlistEventsAdapter.notifyDataSetChanged();
    }

    /**
     * Updates the selected events in the UI.
     */
    public void updateSelectedEvents(List<Event> fetchedSelectedEvents) {
        this.selectedEvents.clear();
        this.selectedEvents.addAll(fetchedSelectedEvents);
        selectedEventsAdapter.notifyDataSetChanged();
    }
}
