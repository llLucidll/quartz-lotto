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

/**
 * View for browsing events
 */
public class BrowseEventsView {

    private final Context context;
    private final RecyclerView eventRecyclerView;
    private final EventAdapterAdmin eventAdapterAdmin;
    private final BrowseEventsController controller;

    /**
     * Constructor for BrowseEventsView
     * @param context
     * @param recyclerView
     * @param controller
     */
    public BrowseEventsView(Context context, RecyclerView recyclerView, BrowseEventsController controller) {
        this.context = context;
        this.eventRecyclerView = recyclerView;
        this.controller = controller;

        eventRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        eventAdapterAdmin = new EventAdapterAdmin(context, new java.util.ArrayList<>(), this::deleteEvent);
        eventRecyclerView.setAdapter(eventAdapterAdmin);
    }

    /**
     * Sets the toolbar for the view
     * @param toolbar
     * @param onBackPressed
     */
    public void setToolbar(Toolbar toolbar, Runnable onBackPressed) {
        toolbar.setNavigationOnClickListener(v -> onBackPressed.run());
    }

    /**
     * Loads events from the controller
     */
    public void loadEvents() {
        controller.fetchEvents(events -> {
            eventAdapterAdmin.eventList.clear();
            eventAdapterAdmin.eventList.addAll(events);
            eventAdapterAdmin.notifyDataSetChanged();
        }, errorMessage -> Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show());
    }

    /**
     * Deletes an event
     * @param event
     */
    private void deleteEvent(Event event) {
        controller.deleteEvent(event, () -> {
            eventAdapterAdmin.eventList.remove(event);
            eventAdapterAdmin.notifyDataSetChanged();
            Toast.makeText(context, "Event and waitlist deleted.", Toast.LENGTH_SHORT).show();
        }, errorMessage -> Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show());
    }
}
