//*
package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private ListView eventsListView;
    private FirebaseFirestore db;
    private List<String> eventNamesList;
    private List<String> eventIdsList;
    private static final int CREATE_EVENT_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        eventsListView = view.findViewById(R.id.events_list);
        db = FirebaseFirestore.getInstance();
        eventNamesList = new ArrayList<>();
        eventIdsList = new ArrayList<>();

        loadEvents();

        Button navigateButton = view.findViewById(R.id.edit_or_create_button);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                startActivityForResult(intent, CREATE_EVENT_REQUEST_CODE);
            }
        });

        eventsListView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedEventId = eventIdsList.get(position);
            Intent intent = new Intent(getActivity(), WaitinglistActivity.class);
            intent.putExtra("event_id", selectedEventId);
            startActivity(intent);
        });

        return view;
    }

    /**
     * Loads events from Firestore and populates the eventsListView.
     */
    private void loadEvents() {
        CollectionReference eventsRef = db.collection("Events");
        eventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                eventNamesList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String eventName = document.getString("eventName");
                    String eventId = document.getId();
                    if (eventName != null) {
                        eventNamesList.add(eventName);
                        eventIdsList.add(eventId);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, eventNamesList);
                eventsListView.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "Error loading events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method is called when the activity returns with a result.
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_EVENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Reload events to update the list with the new event
            loadEvents();
        }
    }
}
