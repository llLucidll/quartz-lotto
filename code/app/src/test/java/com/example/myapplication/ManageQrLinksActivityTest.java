package com.example.myapplication;

import static org.junit.Assert.*;

import com.example.myapplication.Models.Event;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for managing QR links within events.
 * Tests adding events with QR links, retrieving QR links, and deleting QR links.
 * US 01.06.01 As an entrant I want to view event details within the app by scanning the promotional QR code
 * US 01.06.02 As an entrant I want to be able to be sign up for an event by scanning the QR code
 * US 02.01.01 As an organizer I want to create a new event and generate a unique promotional QR code that links to the event description and event poster in the app
 * US 02.01.02 As an organizer I want to store the generated QR code in my database
 */
public class ManageQrLinksActivityTest {

    private List<Event> eventList;

    /**
     * Sets up a mock list of events before each test.
     */
    @Before
    public void setUp() {
        // Initialize mock events
        eventList = new ArrayList<>();
        eventList.add(new Event("1", "Event 1", "2024-01-01", "10:00 AM", "Description 1",
                100, 10, true, "qrLink1", "posterUrl1", 50, "organizer1", null));
        eventList.add(new Event("2", "Event 2", "2024-02-01", "11:00 AM", "Description 2",
                200, 20, false, "qrLink2", "posterUrl2", 150, "organizer2", null));
        eventList.add(new Event("3", "Event 3", "2024-03-01", "12:00 PM", "Description 3",
                300, 30, true, null, "posterUrl3", 250, "organizer3", null)); // Event without QR link
    }

    /**
     * Tests browsing events by verifying the event list's size
     * and the details of each event with a QR link.
     */
    @Test
    public void testBrowseEventsWithQrLinks() {
        // Filter events that have QR links
        List<Event> eventsWithQrLinks = new ArrayList<>();
        for (Event event : eventList) {
            if (event.getQrCodeLink() != null) {
                eventsWithQrLinks.add(event);
            }
        }

        // Check the size of the filtered list
        assertEquals(2, eventsWithQrLinks.size());

        // Validate the first event's details
        Event event1 = eventsWithQrLinks.get(0);
        assertEquals("1", event1.getEventId());
        assertEquals("Event 1", event1.getEventName());
        assertEquals("qrLink1", event1.getQrCodeLink());

        // Validate the second event's details
        Event event2 = eventsWithQrLinks.get(1);
        assertEquals("2", event2.getEventId());
        assertEquals("Event 2", event2.getEventName());
        assertEquals("qrLink2", event2.getQrCodeLink());
    }

    /**
     * Tests deleting a QR link from an event
     * and ensures the QR link is removed correctly.
     */
    @Test
    public void testDeleteQrLink() {
        // Find the event with ID "1"
        Event eventToDeleteQrLink = null;
        for (Event event : eventList) {
            if ("1".equals(event.getEventId())) {
                eventToDeleteQrLink = event;
                break;
            }
        }

        assertNotNull("Event should not be null", eventToDeleteQrLink);
        assertNotNull("QR link should not be null before deletion", eventToDeleteQrLink.getQrCodeLink());

        // Delete the QR link
        eventToDeleteQrLink.setQrCodeLink(null);

        // Verify that the QR link is deleted
        assertNull("QR link should be null after deletion", eventToDeleteQrLink.getQrCodeLink());
    }

    /**
     * Tests that events without QR links are not included when browsing QR links.
     */
    @Test
    public void testEventExclusionAfterQrLinkDeletion() {
        // Delete QR link from event "1"
        for (Event event : eventList) {
            if ("1".equals(event.getEventId())) {
                event.setQrCodeLink(null);
                break;
            }
        }

        // Filter events with QR links
        List<Event> eventsWithQrLinks = new ArrayList<>();
        for (Event event : eventList) {
            if (event.getQrCodeLink() != null) {
                eventsWithQrLinks.add(event);
            }
        }

        // Check that only one event remains with a QR link
        assertEquals(1, eventsWithQrLinks.size());

        // Validate the remaining event's details
        Event remainingEvent = eventsWithQrLinks.get(0);
        assertEquals("2", remainingEvent.getEventId());
        assertEquals("Event 2", remainingEvent.getEventName());
        assertEquals("qrLink2", remainingEvent.getQrCodeLink());
    }

    /**
     * Tests adding a new QR link to an event
     * and verifies that it is added correctly.
     */
    @Test
    public void testAddQrLink() {
        // Find the event without a QR link (eventId "3")
        Event eventWithoutQrLink = null;
        for (Event event : eventList) {
            if ("3".equals(event.getEventId())) {
                eventWithoutQrLink = event;
                break;
            }
        }

        assertNotNull("Event should not be null", eventWithoutQrLink);
        assertNull("QR link should be null before adding", eventWithoutQrLink.getQrCodeLink());

        // Add a new QR link to the event
        eventWithoutQrLink.setQrCodeLink("qrLink3");

        // Verify that the QR link is added
        assertEquals("qrLink3", eventWithoutQrLink.getQrCodeLink());
    }
}
