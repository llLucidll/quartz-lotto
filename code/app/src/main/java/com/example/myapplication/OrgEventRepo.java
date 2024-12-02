package com.example.myapplication;

import android.media.metrics.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing events.
 */
public class OrgEventRepo {
    private static List<Event> events = new ArrayList<>();

    /**
     * Adds an event to the repository.
     *
     * @param event The event to add.
     */
    public static void addEvent(Event event) {
        events.add(event);
    }

    /**
     * Retrieves all events from the repository.
     * @return A list of all events.
     */

    public static List<Event> getEvents() {
        return new ArrayList<>(events); // Return a copy to avoid external modification
    }

    /**
     * Clears all events from the repository.
     */

    public static void clearEvents() {
        events.clear(); // Optional method to clear events
    }
}
