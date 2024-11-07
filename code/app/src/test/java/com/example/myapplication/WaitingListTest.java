package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class WaitingListTest {

    private WaitingList waitingList;
    private UserProfile user1;
    private UserProfile user2;
    private UserProfile user3;

    // Setting up test data before each test
    @Before
    public void setUp() {
        waitingList = new WaitingList();

        // Initialize some users
        user1 = new UserProfile(1, "John Doe");
        user2 = new UserProfile(2, "Jane Smith");
        user3 = new UserProfile(3, "Bob Johnson");
    }

    @Test
    public void testAddWaiter() {
        // Testing successful addition
        assertTrue(waitingList.addWaiter(user1, 5));
        assertTrue(waitingList.addWaiter(user2, 5));

        // Testing addition when capacity is full
        assertTrue(waitingList.addWaiter(user3, 5));

        // Attempt to add a user when the waiting list is at capacity
        assertFalse(waitingList.addWaiter(new UserProfile(4, "Alice"), 3));
    }

    @Test
    public void testRemoveWaiter() {
        // Add user and then remove them
        waitingList.addWaiter(user1, 5);
        assertTrue(waitingList.removeWaiter(user1));

        // Try to remove a user who is not on the list
        assertFalse(waitingList.removeWaiter(user2));
    }

    @Test
    public void testSampleAttendees() {
        // Add users to the waiting list
        waitingList.addWaiter(user1, 5);
        waitingList.addWaiter(user2, 5);
        waitingList.addWaiter(user3, 5);

        // Sample 2 attendees
        List<UserProfile> sampledAttendees = waitingList.sampleAttendees(2);

        // Assert that 2 attendees were sampled
        assertEquals(2, sampledAttendees.size());

        // Ensure that the sampled attendees are from the waiting list
        assertTrue(waitingList.getAllAttendees().containsAll(sampledAttendees));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSampleAttendeesInvalidNumber() {
        // Try to sample with invalid number (<=0)
        waitingList.sampleAttendees(0);
    }

    @Test
    public void testGetWaitingListSize() {
        // Check size of the list
        waitingList.addWaiter(user1, 5);
        waitingList.addWaiter(user2, 5);

        assertEquals(2, waitingList.getWaitingListSize());

        // Add a third user and check again
        waitingList.addWaiter(user3, 5);
        assertEquals(3, waitingList.getWaitingListSize());
    }

    @Test
    public void testGetAllAttendees() {
        // Add users
        waitingList.addWaiter(user1, 5);
        waitingList.addWaiter(user2, 5);

        // Get all attendees and verify
        List<UserProfile> allAttendees = waitingList.getAllAttendees();
        assertEquals(2, allAttendees.size());
        assertTrue(allAttendees.contains(user1));
        assertTrue(allAttendees.contains(user2));
    }

    @Test
    public void testClearWaitingList() {
        // Add users
        waitingList.addWaiter(user1, 5);
        waitingList.addWaiter(user2, 5);

        // Clear the list
        waitingList.clearWaitingList();
        assertEquals(0, waitingList.getWaitingListSize());
    }
}
