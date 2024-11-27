// File: com/example/myapplication/HomeFragment.java
package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * HomeFragment displays the list of events created by the organizer.
 */
public class HomeFragment extends Fragment {

    private RecyclerView eventsListView;
    private FirebaseFirestore db;
    private List<Event> eventList;
    private EventAdapter adapter;
    private ListenerRegistration listenerRegistration;
    private static final int CREATE_EVENT_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        eventsListView = view.findViewById(R.id.events_list);
        eventsListView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();

        // Retrieve the current user's UID from BaseActivity
        Activity activity = getActivity();
        if (activity == null || !(activity instanceof BaseActivity)) {
            Toast.makeText(getContext(), "Error retrieving user information.", Toast.LENGTH_SHORT).show();
            return view;
        }

        String currentUserId = ((BaseActivity) activity).retrieveDeviceId();
        if (currentUserId == null) {
            Toast.makeText(getContext(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            activity.finish(); // Close the activity if user is not authenticated
            return view;
        }

        // Initialize EventAdapter with the event list and currentUserId
        adapter = new EventAdapter(getContext(), eventList, currentUserId);
        eventsListView.setAdapter(adapter);

        // Load events initially and listen for real-time updates
        setupRealtimeUpdates(currentUserId);

        // Set up button for navigation
        Button navigateButton = view.findViewById(R.id.edit_or_create_button);
        navigateButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateEventActivity.class);
            startActivityForResult(intent, CREATE_EVENT_REQUEST_CODE);
        });

        return view;
    }

    /**
     * Sets up a real-time listener for the organizer's events.
     *
     * @param organizerId The UID of the current organizer.
     */
    private void setupRealtimeUpdates(String organizerId) {
        CollectionReference eventsRef = db.collection("Events");

        // Listen for changes in real-time where organizerId matches currentUserId
        listenerRegistration = eventsRef.whereEqualTo("organizerId", organizerId)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Error listening to updates: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("HomeFragment", "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        // Clear the list and repopulate
                        eventList.clear();

                        for (QueryDocumentSnapshot document : snapshots) {
                            String eventId = document.getId();
                            String eventName = document.getString("eventName");
                            String drawDate = document.getString("drawDate");
                            String eventDateTime = document.getString("eventDateTime");
                            String description = document.getString("description");
                            String posterUrl = document.getString("posterUrl");
                            Long maxAttendeesLong = document.getLong("maxAttendees");
                            Long currentAttendeesLong = document.getLong("currentAttendees");
                            Integer maxWaitlist = document.getLong("maxWaitlist") != null ?
                                    document.getLong("maxWaitlist").intValue() : null;
                            Boolean geolocationEnabled = document.getBoolean("geolocationEnabled");
                            String qrCodeLink = document.getString("qrCodeLink");
                            String eventOrganizerId = document.getString("organizerId");

                            // Handle potential null values
                            int maxAttendees = maxAttendeesLong != null ? maxAttendeesLong.intValue() : 0;
                            int currentAttendees = currentAttendeesLong != null ? currentAttendeesLong.intValue() : 0;
                            boolean geoEnabled = geolocationEnabled != null && geolocationEnabled;

                            // Create Event object using the overloaded constructor
                            Event event = new Event(eventId, eventName, drawDate, eventDateTime, description,
                                    maxAttendees, maxWaitlist, geoEnabled, qrCodeLink, posterUrl, currentAttendees, eventOrganizerId);

                            eventList.add(event);
                        }


                        adapter.notifyDataSetChanged(); // Update the RecyclerView with new data
                        Log.d("HomeFragment", "Events updated. Total events: " + eventList.size());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) {
            listenerRegistration.remove(); // Stop listening to database updates
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_EVENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}
