package com.example.myapplication;

import com.example.myapplication.Models.EntrantList;
import com.example.myapplication.Models.Event;
import com.example.myapplication.Models.Facility;
import com.example.myapplication.Models.Attendee;

import org.junit.Before;
import org.junit.Test;

public class EventRegistrationTest {
    private Event event;
    private Attendee attendee1, attendee2;
    private EntrantList entrantList;
    @Before
    public void setUp() {
        event = new Event("eventId1", "event1", "02/02/25", "02/03/25", "description", 5, 10, true, "qrCodeLink", "posterUrl",
        0, "orgId", new Facility("facilityImageUrl", "location", "facilityName", "facilityId"));
        attendee1 = new Attendee();
        attendee1.setUserId("userId1");
        attendee1.setUserName("userName1");
        attendee1.setUserEmail("userEmail1");
        attendee1.setStatus("waiting");
        attendee2 = new Attendee();
        attendee2.setUserId("userId2");
        attendee2.setUserName("userName2");
        attendee2.setUserEmail("userEmail2");
        attendee2.setStatus("waiting");
        entrantList = new EntrantList(event.getMaxAttendees() + event.getMaxWaitlist(), event.getMaxAttendees());
        entrantList.setUser(attendee1);
        entrantList.setUser(attendee2);
    }

    /*
     * US 01.05.03 As an entrant I want to be able to decline
     */
    @Test
    public void declineInvitationTest() {
        setUp();
        attendee1.setStatus("cancelled");
        for (Attendee attendee : entrantList.getUsers()) {
            if (attendee.getStatus().equals("cancelled")) {
                entrantList.getUsers().remove(attendee);
            }
        }
        assert entrantList.getUsers().size() == 1;
    }

    /*
     * US 01.05.02 As an entrant I want to be able to accept the invitation
     * to register/sign up when chosen to participate in an event
     */
    @Test
    public void acceptInvitationTest() {
        setUp();
        EntrantList confirmedAttendees = new EntrantList(event.getMaxAttendees() + event.getMaxWaitlist(), event.getMaxAttendees());
        attendee1.setStatus("confirmed");
        for (Attendee attendee : entrantList.getUsers()) {
            if (attendee.getStatus().equals("confirmed")) {
                confirmedAttendees.setUser(attendee);
            }
        }
        assert confirmedAttendees.getUsers().size() == 1;
    }

    /*
     * US 01.06.01 As an entrant I want to view event details within the app by scanning the promotional QR code
     */
    @Test
    public void viewEventDetailsTest() {
        setUp();
        String details = "";
        String qrCodeLink = "qrCodeLink";
        if (event.getQrCodeLink().equals(qrCodeLink)) {
            details = event.getDescription();
        }
        assert details.equals("description");
    }

    /*
     * US 01.06.02 As an entrant I want to be able to be sign up for an event by scanning the QR code
     */
    @Test
    public void signUpForEventTest() {
        setUp();
        Attendee attendee3 = new Attendee();
        String qrCodeLink = "qrCodeLink";
        if (event.getQrCodeLink().equals(qrCodeLink)) {
            attendee3.setStatus("waiting");
            entrantList.setUser(attendee3);
        }
        assert entrantList.getUsers().size() == 3;
    }
}
