package com.example.myapplication.Controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Models.Event;
import com.example.myapplication.R;
import com.example.myapplication.Repositories.HomeRepository;
import com.example.myapplication.Views.HomeView;

import java.util.List;

public class HomePageController extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> events;

    public HomePageController(Context context, List<Event> events) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // Inflate the item_waitlist_event layout
            convertView = LayoutInflater.from(context).inflate(R.layout.item_event_waitlist, parent, false);
        }

        // Get the current event at the given position
        Event event = events.get(position);

        // Find the TextViews inside item_waitlist_event
        TextView eventNameTextView = convertView.findViewById(R.id.event_name_text);
        TextView eventDateTimeTextView = convertView.findViewById(R.id.date_text);
        ListView waitlistEvent = convertView.findViewById(R.id.entrant_waitlist);
        Button leaveWaitlistButton = convertView.findViewById(R.id.leave_button);


        if (event != null) {
            eventNameTextView.setText(event.getEventName());
            eventDateTimeTextView.setText(event.getEventDateTime());
        }


        leaveWaitlistButton.setOnClickListener(view -> {

            events.remove(position); // Remove the event at the current position
            notifyDataSetChanged();  // Notify the adapter to update the list view

            // Now call HomeRepository to remove the device ID from Firebase
            HomeRepository.removeFromWaitlist(event.getEventName());

            Toast.makeText(context, "You have left the event: " + event.getEventName(), Toast.LENGTH_SHORT).show();
        });
        return convertView;
    }


//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.item_event_selected_list, parent, false);
//        }
//
//        TextView eventName = convertView.findViewById(R.id.event_name_text);
//        TextView eventDate = convertView.findViewById(R.id.date_text);
//        Button confirmButton = convertView.findViewById(R.id.confirm_button);
//        Button declineButton = convertView.findViewById(R.id.decline_button);
//        LinearLayout buttonLayout = convertView.findViewById(R.id.button_layout);
//
//        Event event = events.get(position); // Get the event for this position
//
//        eventName.setText(event.getEventName());
//        eventDate.setText(event.getEventDateTime());
//
//        // Set onClickListener for Confirm Button
//        confirmButton.setOnClickListener(v -> {
//            // Replace buttons with "Registered" text after confirming the event
//            buttonLayout.removeAllViews(); // Clear the buttons
//            TextView registeredText = new TextView(context);
//            registeredText.setText("Registered");
//            registeredText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
//            registeredText.setTextSize(16);
//            buttonLayout.addView(registeredText); // Add the "Registered" text
//        });
//
//        // Set onClickListener for Decline Button (delete event)
//        declineButton.setOnClickListener(v -> {
//            // Remove the event from the list and notify the adapter
//            events.remove(position); // Remove the event at the current position
//            notifyDataSetChanged(); // Notify the adapter to update the view
//        });
//
//        return convertView;

}
