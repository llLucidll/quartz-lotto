package com.example.myapplication.Views;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Event;
import com.example.myapplication.EventAdapterAdmin;
import com.example.myapplication.Controllers.BrowseEventsController;

import java.util.List;

public class BrowseEventsView {

    private final Context context;
    private final RecyclerView eventRecyclerView;
    private final EventAdapterAdmin eventAdapterAdmin;
    private final BrowseEventsController controller;

    public BrowseEventsView(Context context, RecyclerView recyclerView, BrowseEventsController controller) {
        this.context = context;
        this.eventRecyclerView = recyclerView;
        this.controller = controller;

        eventRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        eventAdapterAdmin = new EventAdapterAdmin(context, new java.util.ArrayList<>(), this::deleteEvent);
        eventRecyclerView.setAdapter(eventAdapterAdmin);
    }

    public void setToolbar(Toolbar toolbar, Runnable onBackPressed) {
        toolbar.setNavigationOnClickListener(v -> onBackPressed.run());
    }

    public void loadEvents() {
        controller.fetchEvents(events -> {
            eventAdapterAdmin.eventList.clear();
            eventAdapterAdmin.eventList.addAll(events);
            eventAdapterAdmin.notifyDataSetChanged();
        }, errorMessage -> Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show());
    }

    private void deleteEvent(Event event) {
        controller.deleteEvent(event, () -> {
            eventAdapterAdmin.eventList.remove(event);
            eventAdapterAdmin.notifyDataSetChanged();
            Toast.makeText(context, "Event and waitlist deleted.", Toast.LENGTH_SHORT).show();
        }, errorMessage -> Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show());
    }
}
