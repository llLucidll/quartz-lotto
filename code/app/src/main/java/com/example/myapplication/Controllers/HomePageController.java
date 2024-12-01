package com.example.myapplication.Controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_event_waitlist, parent, false);
        }

        //get the current event at the given position
        Event event = events.get(position);

        //find the TextViews inside item_event_waitlist
        TextView eventNameTextView = convertView.findViewById(R.id.event_name_text);
        TextView eventDateTimeTextView = convertView.findViewById(R.id.date_text);
        Button leaveWaitlistButton = convertView.findViewById(R.id.leave_button);

        if (event != null) {
            eventNameTextView.setText(event.getEventName());
            eventDateTimeTextView.setText(event.getEventDateTime());
        }

        leaveWaitlistButton.setOnClickListener(view -> {
            //remove the event from the waitlist list
            events.remove(position);
            notifyDataSetChanged();

            //create an instance of HomeRepository and then call it to remove the deviceid from firebase
            HomeRepository homeRepository = new HomeRepository(context);
            homeRepository.removeFromWaitlist(event.getEventName());

            Toast.makeText(context, "You have left the event: " + event.getEventName(), Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
