package com.example.myapplication.Controllers;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Models.Event;
import com.example.myapplication.R;
import com.example.myapplication.Repositories.HomeRepository;

import java.util.List;

public class HomePageController extends BaseAdapter {
    private Context context;
    private List<Event> selectedEvents;
    private boolean isSelectedList;
    private HomeRepository homeRepository;


    public HomePageController(Context context, List<Event> events, boolean isSelectedList) {
        this.context = context;
        this.selectedEvents = events;
        this.isSelectedList = isSelectedList;
        this.homeRepository = new HomeRepository();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        super.registerDataSetObserver(dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        super.unregisterDataSetObserver(dataSetObserver);
    }

    @Override
    public int getCount() {
        return selectedEvents.size();
    }

    @Override
    public Object getItem(int position) {
        return selectedEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(selectedEvents.get(position).getEventId());
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // Confirm an event: Update status to 'confirmed'
    public void onConfirmEvent(Event event) {
        homeRepository.setEventStatusToConfirmed(event.getEventId());
        Toast.makeText(context, "Event Confirmed", Toast.LENGTH_SHORT).show();
    }

    // Decline an event: Update status to 'cancelled'
    public void onDeclineEvent(Event event) {
        homeRepository.setEventStatusToCancelled(event.getEventId());
        Toast.makeText(context, "Event Declined", Toast.LENGTH_SHORT).show();
    }

    // Select an event: Update status to 'waiting'
    public void onSelectEvent(Event event) {
        homeRepository.setEventStatusToWaiting(event.getEventId());
        Toast.makeText(context, "Event Added to Waitlist", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Reuse the view if it exists, otherwise inflate a new one
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_event_selected_list, parent, false);
        }

        // Bind the data to the views
        TextView eventName = convertView.findViewById(R.id.event_name_text);
        TextView eventDate = convertView.findViewById(R.id.date_text);
        Button confirmButton = convertView.findViewById(R.id.confirm_button);
        Button declineButton = convertView.findViewById(R.id.decline_button);
        LinearLayout buttonLayout = convertView.findViewById(R.id.button_layout);
        Event event = selectedEvents.get(position); // Get the event for this position

        // Populate event name and date
        eventName.setText(event.getEventName());
        eventDate.setText(event.getEventDateTime());

        // Set onClickListener for Confirm Button
        confirmButton.setOnClickListener(v -> {
            // Replace buttons with "Registered" text after confirming the event
            buttonLayout.removeAllViews(); // Clear the buttons
            TextView registeredText = new TextView(context);
            registeredText.setText("Registered");
            registeredText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            registeredText.setTextSize(16);
            buttonLayout.addView(registeredText); // Add the "Registered" text
        });

        // Set onClickListener for Decline Button (delete event)
        declineButton.setOnClickListener(v -> {
            // Remove the event from the list and notify the adapter
            selectedEvents.remove(position); // Remove the event at the current position
            notifyDataSetChanged(); // Notify the adapter to update the view
        });

        // Set tag for views to manage changes more effectively
        convertView.setTag(position);

        return convertView;
    }

    @Override
    public int getItemViewType(int i) {
        return 0; // Only one view type in this adapter
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Only one view type
    }

    @Override
    public boolean isEmpty() {
        return selectedEvents.isEmpty(); // Check if the list is empty
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true; // All items are enabled by default
    }

    @Override
    public boolean isEnabled(int i) {
        return true; // All items are enabled
    }
}
