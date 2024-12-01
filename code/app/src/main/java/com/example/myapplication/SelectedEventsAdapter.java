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

public class SelectedEventsAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> events;

    public SelectedEventsAdapter(Context context, List<Event> events) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            //inflate the item_event_selected_list layout
            convertView = LayoutInflater.from(context).inflate(R.layout.item_event_selected_list, parent, false);
        }
        //get the current event at the given position
        Event event = events.get(position);

        //find the TextViews and Buttons inside item_event_selected_list
        TextView eventNameTextView = convertView.findViewById(R.id.event_name_text);
        TextView eventDateTimeTextView = convertView.findViewById(R.id.date_text);
        Button confirmButton = convertView.findViewById(R.id.confirm_button);
        Button declineButton = convertView.findViewById(R.id.decline_button);
        LinearLayout buttonLayout = convertView.findViewById(R.id.button_layout);

        if (event != null) {
            eventNameTextView.setText(event.getEventName());
            eventDateTimeTextView.setText(event.getEventDateTime());
        }

        confirmButton.setOnClickListener(view -> {
            // Update status to "confirmed" in Firestore
            HomeRepository homeRepository = new HomeRepository(context);
            homeRepository.updateEventStatus(event.getEventName(), "confirmed");

            // Update UI to show "Registered"
            buttonLayout.removeAllViews();
            TextView registeredText = new TextView(context);
            registeredText.setText("Registered");
            registeredText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            registeredText.setTextSize(16);
            buttonLayout.addView(registeredText);
        });

        // Set OnClickListener for Decline Button
        declineButton.setOnClickListener(view -> {
            // Remove user from the event in Firestore
            HomeRepository homeRepository = new HomeRepository(context);
            homeRepository.removeFromWaitlist(event.getEventName());

            // Remove event from the list and update UI
            events.remove(position);
            notifyDataSetChanged();

            Toast.makeText(context, "You have declined the event: " + event.getEventName(), Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
