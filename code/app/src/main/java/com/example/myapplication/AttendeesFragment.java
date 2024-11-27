package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Attendee;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class AttendeesFragment extends Fragment {

    private static final String TAG = "AttendeesFragment";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AttendeesAdapter attendeesAdapter;
    private List<Attendee> attendeeList = new ArrayList<>();
    private FirebaseFirestore db;
    private String eventId;

    public AttendeesFragment() {
    }

    public static AttendeesFragment newInstance(String eventId) {
        AttendeesFragment fragment = new AttendeesFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
        }
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendees, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.attendeesRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        if (progressBar == null) {
            Log.e(TAG, "ProgressBar is null!");
        }

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        attendeesAdapter = new AttendeesAdapter(attendeeList, db, eventId, getContext());
        recyclerView.setAdapter(attendeesAdapter);

        // Fetch attendees
        fetchAttendees();

        return view;
    }

    private void fetchAttendees() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        db.collection("Events")
                .document(eventId)
                .collection("Attendees")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching attendees", e);
                        Toast.makeText(getContext(), "Error loading attendees.", Toast.LENGTH_SHORT).show();
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        return;
                    }

                    if (snapshots != null) {
                        attendeeList.clear();
                        for (DocumentSnapshot document : snapshots) {
                            Attendee attendee = document.toObject(Attendee.class);
                            if (attendee != null) {
                                attendee.setId(document.getId());
                                attendeeList.add(attendee);
                            }
                        }
                        attendeesAdapter.notifyDataSetChanged();
                    }

                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                });
    }
}
