//*
package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView eventsListView;
    private FirebaseFirestore db;
    private List<String> eventNamesList;
    private List<String> eventIdsList;
    private EventAdapter adapter;
    private ListenerRegistration listenerRegistration;
    private static final int CREATE_EVENT_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        eventsListView = view.findViewById(R.id.events_list);
        eventsListView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        eventNamesList = new ArrayList<>();
        eventIdsList = new ArrayList<>();

        adapter = new EventAdapter(getContext(), eventNamesList, eventIdsList);
        eventsListView.setAdapter(adapter);

        // Load events initially and listen for real-time updates
        setupRealtimeUpdates();

        // Set up button for navigation
        Button navigateButton = view.findViewById(R.id.edit_or_create_button);
        navigateButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateEventActivity.class);
            startActivityForResult(intent, CREATE_EVENT_REQUEST_CODE);
        });

        return view;
    }

    /**
     * Sets up a real-time listener for the "Events" collection.
     */
    private void setupRealtimeUpdates() {
        CollectionReference eventsRef = db.collection("Events");

        // Listen for changes in real-time
        listenerRegistration = eventsRef.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Toast.makeText(getContext(), "Error listening to updates", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshots != null) {
                // Clear lists and repopulate with all documents from the collection
                eventNamesList.clear();
                eventIdsList.clear();

                for (QueryDocumentSnapshot document : snapshots) {
                    String eventName = document.getString("eventName");
                    String eventId = document.getId();
                    if (eventName != null) {
                        eventNamesList.add(eventName);
                        eventIdsList.add(eventId);
                    }
                }
                adapter.notifyDataSetChanged(); // Update the RecyclerView with the new data
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
            // No need to manually reload as we're listening in real-time
        }
    }
}
