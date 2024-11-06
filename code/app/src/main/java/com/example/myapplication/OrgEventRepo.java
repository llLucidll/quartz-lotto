package com.example.myapplication;

import android.media.metrics.Event;

import java.util.ArrayList;
import java.util.List;

public class OrgEventRepo {
    private static List<Event> events = new ArrayList<>();

    public static void addEvent(Event event) {
        events.add(event);
    }

    public static List<Event> getEvents() {
        return new ArrayList<>(events); // Return a copy to avoid external modification
    }

    public static void clearEvents() {
        events.clear(); // Optional method to clear events
    }
}
