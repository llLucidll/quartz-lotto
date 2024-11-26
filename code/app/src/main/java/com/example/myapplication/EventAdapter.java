package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Adapter for displaying events in a RecyclerView.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private final List<Event> eventList;
    private final Context context;
    private final String currentUserId;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Constructor for EventAdapter.
     *
     * @param context        The context from the calling activity or fragment
     * @param eventList      The list of events to display
     * @param currentUserId  The UID of the current authenticated organizer
     */
    public EventAdapter(Context context, List<Event> eventList, String currentUserId) {
        this.context = context;
        this.eventList = eventList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.eventNameTextView.setText(event.getEventName());
        holder.eventDateTextView.setText("Date & Time: " + event.getDrawDate() + " " + event.getEventDateTime());
        holder.eventDescriptionTextView.setText(event.getDescription());

        if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
            Glide.with(context)
                    .load(event.getPosterUrl())
                    .placeholder(R.drawable.ic_placeholder_image) // Ensure you have a placeholder image
                    .into(holder.posterImageView);
        } else {
            holder.posterImageView.setImageResource(R.drawable.ic_placeholder_image);
        }

        // Set click listener to navigate to EventDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("eventId", event.getEventId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder class for event items.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView, eventDateTextView, eventDescriptionTextView;
        ImageView posterImageView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            eventDateTextView = itemView.findViewById(R.id.eventDateTextView);
            eventDescriptionTextView = itemView.findViewById(R.id.eventDescriptionTextView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
        }
    }
}
