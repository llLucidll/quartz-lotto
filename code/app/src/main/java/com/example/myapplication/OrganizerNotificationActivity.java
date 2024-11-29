package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for organizers to view their events.
 */
public class OrganizerNotificationActivity extends BaseActivity {

    private static final String TAG = "OrganizerNotification";

    private RecyclerView recyclerView;
    private OrganizerEventAdapter eventAdapter;
    private FirebaseFirestore db;
    private String deviceId; // Use deviceId for organizer identification

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_notification);

        recyclerView = findViewById(R.id.recycler_view_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        deviceId = retrieveDeviceId(); // Retrieve the organizer's deviceId

        eventAdapter = new OrganizerEventAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(eventAdapter);

        fetchOrganizedEvents();
    }

    /**
     * Fetches events organized by the current user.
     */
    private void fetchOrganizedEvents() {
        db.collection("Events")
                .whereEqualTo("organizerId", deviceId) // Match organizerId with deviceId
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> events = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        if (event != null) {
                            event.setEventId(document.getId());
                            events.add(event);
                        }
                    }
                    eventAdapter.updateEvents(events);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch events", e);
                    Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Adapter class for the RecyclerView.
     */
    private class OrganizerEventAdapter extends RecyclerView.Adapter<OrganizerEventAdapter.EventViewHolder> {

        private List<Event> events;
        private Context context;

        public OrganizerEventAdapter(List<Event> events, Context context) {
            this.events = events;
            this.context = context;
        }

        public void updateEvents(List<Event> newEvents) {
            this.events = newEvents;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public OrganizerEventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_event_notif, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrganizerEventAdapter.EventViewHolder holder, int position) {
            Event event = events.get(position);
            holder.bind(event);
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        /**
         * ViewHolder class for event items.
         */
        class EventViewHolder extends RecyclerView.ViewHolder {

            private TextView eventTitleTextView;
            private TextView eventDateTextView;

            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                eventTitleTextView = itemView.findViewById(R.id.text_view_event_title);
                eventDateTextView = itemView.findViewById(R.id.text_view_event_date);
            }

            public void bind(Event event) {
                eventTitleTextView.setText(event.getEventName());
                eventDateTextView.setText(event.getEventDateTime() != null ? event.getEventDateTime() : "No date specified");

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, EventWaitlistActivity.class);
                    intent.putExtra("eventId", event.getEventId());
                    intent.putExtra("eventName", event.getEventName());
                    context.startActivity(intent);
                });
            }
        }
    }
}
