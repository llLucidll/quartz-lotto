package com.example.myapplication.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.SelectedEventsAdapter;
import com.example.myapplication.Controllers.HomePageController;
import com.example.myapplication.Models.Event;
import com.example.myapplication.R;
import com.example.myapplication.Repositories.HomeRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment representing the home page of the application.
 */
public class HomeView extends Fragment {
    private ListView selectedEventsListView;
    private ListView waitlistEventsListView;

    private SelectedEventsAdapter selectedEventsAdapter;
    private HomePageController waitlistEventsAdapter;
    private List<Event> selectedEvents;
    private List<Event> waitlistEvents;
    private Map<String, String> userStatuses; // Map to store user statuses

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_page, container, false);

        selectedEventsListView = view.findViewById(R.id.selected_events_list);
        waitlistEventsListView = view.findViewById(R.id.entrant_waitlist);

        selectedEvents = new ArrayList<>();
        waitlistEvents = new ArrayList<>();
        userStatuses = new HashMap<>(); // Initialize the map

        selectedEventsAdapter = new SelectedEventsAdapter(getContext(), selectedEvents, userStatuses);
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
     * Updates the selected events and user statuses in the UI.
     *
     * @param fetchedSelectedEvents List of events where the user is "selected" or "confirmed".
     * @param fetchedUserStatuses   Map of eventId to user status.
     */
    public void updateSelectedEvents(List<Event> fetchedSelectedEvents, Map<String, String> fetchedUserStatuses) {
        this.selectedEvents.clear();
        this.selectedEvents.addAll(fetchedSelectedEvents);

        // Update the userStatuses map
        this.userStatuses.clear();
        this.userStatuses.putAll(fetchedUserStatuses);

        selectedEventsAdapter.notifyDataSetChanged();
    }
}
