package com.example.myapplication.Models;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for browsing and deleting events.
 * Tests adding, retrieving, and removing events from a list.
 */
public class BrowseEventTest {

    private List<Event> eventList;

    /**
     * Sets up a mock list of events before each test.
     */
    @Before
    public void setUp() {
        // Initialize mock events
        eventList = new ArrayList<>();
        eventList.add(new Event("1", "Event 1"));
        eventList.add(new Event("2", "Event 2"));
        eventList.add(new Event("3", "Event 3"));
    }

    /**
     * Tests browsing events by verifying the event list's size
     * and the details of each event.
     */
    @Test
    public void testBrowseEvents() {
        // Check the size of the event list
        assertEquals(3, eventList.size());

        // Validate the first event's details
        Event event1 = eventList.get(0);
        assertEquals("1", event1.getEventId());
        assertEquals("Event 1", event1.getEventName());

        // Validate the second event's details
        Event event2 = eventList.get(1);
        assertEquals("2", event2.getEventId());
        assertEquals("Event 2", event2.getEventName());

        // Validate the third event's details
        Event event3 = eventList.get(2);
        assertEquals("3", event3.getEventId());
        assertEquals("Event 3", event3.getEventName());
    }

    /**
     * Tests adding a new event to the event list
     * and verifies that it is added correctly.
     */
    @Test
    public void testAddEvent() {
        // Add a new event to the list
        Event newEvent = new Event("4", "Event 4");
        eventList.add(newEvent);

        // Verify the new size of the list
        assertEquals(4, eventList.size());

        // Validate the newly added event's details
        Event addedEvent = eventList.get(3);
        assertEquals("4", addedEvent.getEventId());
        assertEquals("Event 4", addedEvent.getEventName());
    }

    /**
     * Tests removing an event from the event list
     * and ensures the list updates correctly.
     */
    @Test
    public void testRemoveEvent() {
        // Remove the second event from the list
        Event removedEvent = eventList.remove(1);

        // Verify the new size of the list
        assertEquals(2, eventList.size());

        // Validate the removed event's details
        assertEquals("2", removedEvent.getEventId());
        assertEquals("Event 2", removedEvent.getEventName());

        // Validate that the remaining second event has the correct details
        Event remainingEvent = eventList.get(1); // Check the new second event
        assertEquals("3", remainingEvent.getEventId());
        assertEquals("Event 3", remainingEvent.getEventName());
    }
}
