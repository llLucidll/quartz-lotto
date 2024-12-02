package com.example.myapplication;
import static org.junit.Assert.assertEquals;

import com.example.myapplication.Models.Attendee;
import com.example.myapplication.Models.EntrantList;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
/**
 * US 02.05.03 Draw a replacement applicant from the pooling system when a previously selected applicant cancels or rejects the invitation
 * US 02.05.02 Sample a specified number of attendees from the waiting list
 * US 01.05.01 As an entrant I want another chance to be chosen from the waiting list if a selected user declines an invitation to sign up
 */
public class EventDrawTest {
    private EntrantList entrantList;
    private Attendee attendee1, attendee2, attendee3, attendee4;
    private int capacity = 10;
    private int sampleSize = 2;

    @Before
    public void setUp() {
        entrantList = new EntrantList(capacity, sampleSize);
        attendee1 = new Attendee();
        attendee2 = new Attendee();
        attendee3 = new Attendee();
        attendee4 = new Attendee();
        attendee1.setUserName("User1");
        attendee2.setUserName("User2");
        attendee3.setUserName("User3");
        attendee4.setUserName("User4");
        entrantList.setUser(attendee1);
        entrantList.setUser(attendee2);
        entrantList.setUser(attendee3);
        entrantList.setUser(attendee4);
    }
    /*
    US 02.05.02 {Sample a specified number of attendees from the waiting list}
     */
    @Test
    public void testNSample() {
        setUp();
        ArrayList<Attendee> sampledList = entrantList.sampleAttendees(sampleSize);
        assertEquals(sampledList.size(), 2);
    }

    /*
    US 02.05.03 {Draw a replacement applicant from the pooling system when a previously selected applicant cancels or rejects the invitation}
     */
    @Test
    public void testRedraw() {
        setUp();
        //sampleSize is 1 because redraw button only draws one person
        ArrayList<Attendee> sampledList = entrantList.sampleAttendees(1);
        assertEquals(sampledList.size(), 1);
    }

    /*
    Another case of sampling when the sample size is equal to current capacity of the list
     */
    @Test
    public void testSample() {
        setUp();
        ArrayList<Attendee> sampledList = entrantList.sampleAttendees(4);
        assertEquals(sampledList.size(), 4);
    }

}
