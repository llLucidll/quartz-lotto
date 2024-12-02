package com.example.myapplication.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.SelectedEventsAdapter;
import com.example.myapplication.Controllers.HomePageController;
import com.example.myapplication.Models.Event;
import com.example.myapplication.R;
import com.example.myapplication.Repositories.HomeRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeView extends Fragment {
    private ListView selectedEventsListView;
    private ListView waitlistEventsListView;

    private SelectedEventsAdapter selectedEventsAdapter;
    private HomePageController waitlistEventsAdapter;
    private List<Event> selectedEvents;
    private List<Event> waitlistEvents;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_page, container, false);

        selectedEventsListView = view.findViewById(R.id.selected_events_list);
        waitlistEventsListView = view.findViewById(R.id.entrant_waitlist);

        selectedEvents = new ArrayList<>();
        waitlistEvents = new ArrayList<>();

        selectedEventsAdapter = new SelectedEventsAdapter(getContext(), selectedEvents);
        selectedEventsListView.setAdapter(selectedEventsAdapter);

        waitlistEventsAdapter = new HomePageController(getContext(), waitlistEvents);
        waitlistEventsListView.setAdapter(waitlistEventsAdapter);

        // Fetch events
        fetchWaitlistEvents();
        fetchSelectedEvents();
        return view;
    }

    private void fetchWaitlistEvents() {
        HomeRepository homeRepository = new HomeRepository(getContext());
        homeRepository.fetchWaitlistEvents(this);
    }

    private void fetchSelectedEvents() {
        HomeRepository homeRepository = new HomeRepository(getContext());
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
