package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.myapplication.Models.Event;
import com.example.myapplication.R;
import com.example.myapplication.Repositories.HomeRepository;

import java.util.List;
import java.util.Map;

public class SelectedEventsAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> events;
    private Map<String, String> userStatuses; // Map to store user statuses keyed by eventId

    public SelectedEventsAdapter(Context context, List<Event> events, Map<String, String> userStatuses) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
        this.userStatuses = userStatuses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ViewHolder pattern can be implemented here for better performance (optional)

        if (convertView == null) {
            // Inflate the item_event_selected_list layout
            convertView = LayoutInflater.from(context).inflate(R.layout.item_event_selected_list, parent, false);
        }

        // Get the current event at the given position
        Event event = events.get(position);

        // Find the TextViews and Buttons inside item_event_selected_list
        TextView eventNameTextView = convertView.findViewById(R.id.event_name_text);
        TextView eventDateTimeTextView = convertView.findViewById(R.id.date_text);
        Button confirmButton = convertView.findViewById(R.id.confirm_button);
        Button declineButton = convertView.findViewById(R.id.decline_button);
        TextView registeredTextView = convertView.findViewById(R.id.registered_text_view); // Registered TextView

        if (event != null) {
            eventNameTextView.setText(event.getEventName());
            eventDateTimeTextView.setText(event.getEventDateTime());
        }

        // Get the user's status for this event
        String status = userStatuses.get(event.getEventId());

        if ("confirmed".equals(status)) {
            // User has already confirmed, show "Registered"
            confirmButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
            registeredTextView.setVisibility(View.VISIBLE);
        } else {
            // User has not confirmed, show Confirm and Decline buttons
            confirmButton.setVisibility(View.VISIBLE);
            declineButton.setVisibility(View.VISIBLE);
            registeredTextView.setVisibility(View.GONE);

            // Set OnClickListener for Confirm Button
            confirmButton.setOnClickListener(view -> {
                // Update status to "confirmed" in Firestore
                HomeRepository homeRepository = new HomeRepository(context);
                homeRepository.updateEventStatus(event.getEventId(), "confirmed");

                // Update status locally
                userStatuses.put(event.getEventId(), "confirmed");

                // Refresh the UI
                notifyDataSetChanged();
            });

            // Set OnClickListener for Decline Button
            declineButton.setOnClickListener(view -> {
                // Remove user from the event in Firestore
                HomeRepository homeRepository = new HomeRepository(context);
                homeRepository.removeFromWaitlist(event.getEventId());

                // Remove event from the list and update UI
                events.remove(position);
                notifyDataSetChanged();

                Toast.makeText(context, "You have declined the event: " + event.getEventName(), Toast.LENGTH_SHORT).show();
            });
        }

        return convertView;
    }
}
