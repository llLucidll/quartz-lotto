package com.example.myapplication;

import static org.junit.Assert.*;

import com.example.myapplication.Models.User;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for testing notification permissions.
 * Verifies that notifications are sent only to users with notification permissions enabled.
 * US 01.04.01 As an entrant I want to receive notification when chosen from the waiting list (when I "win" the lottery)
 * US 01.04.02 As an entrant I want to receive notification of not chosen on the app (when I "lose" the lottery)
 * US 01.04.03 As an entrant I want to opt out of receiving notifications from organizers and admin
 * US 02.05.01 As an organizer I want to send a notification to chosen entrants to sign up for events.
 * US 02.06.04 As an organizer I want to cancel entrants that did not sign up for the event
 * US 02.07.01 As an organizer I want to send notifications to all entrants on the waiting list
 * US 02.07.02 As an organizer I want to send notifications to all selected entrants
 * US 02.07.03 As an organizer I want to send a notification to all cancelled entrants
 */
public class NotificationTest {

    private List<User> userList;

    /**
     * Sets up a mock list of users before each test.
     */
    @Before
    public void setUp() {
        // Initialize mock users
        userList = new ArrayList<>();
        userList.add(new User("1", "Alice", null, "alice@example.com", "01/01/1990", "1234567890", "USA", false));
        userList.add(new User("2", "Bob", null, "bob@example.com", "02/02/1985", "9876543210", "Canada", false));
        userList.add(new User("3", "Charlie", null, "charlie@example.com", "03/03/1995", "1122334455", "UK", false));

        // Set notification permissions
        userList.get(0).setNotificationsPerm(true);  // Alice has notifications enabled
        userList.get(1).setNotificationsPerm(false); // Bob has notifications disabled
        userList.get(2).setNotificationsPerm(true);  // Charlie has notifications enabled
    }

    /**
     * Tests sending notifications only to users with notification permissions enabled.
     */
    @Test
    public void testSendNotifications() {
        List<String> sentNotifications = new ArrayList<>();

        for (User user : userList) {
            if (user.isNotificationsPerm()) {
                sentNotifications.add(user.getName());
            }
        }

        // Verify notifications sent to the correct users
        assertEquals(2, sentNotifications.size());
        assertTrue(sentNotifications.contains("Alice"));
        assertTrue(sentNotifications.contains("Charlie"));
        assertFalse(sentNotifications.contains("Bob"));
    }

    /**
     * Tests ensuring no notifications are sent when all users have notifications disabled.
     */
    @Test
    public void testNoNotificationsSent() {
        // Disable notifications for all users
        for (User user : userList) {
            user.setNotificationsPerm(false);
        }

        List<String> sentNotifications = new ArrayList<>();

        for (User user : userList) {
            if (user.isNotificationsPerm()) {
                sentNotifications.add(user.getName());
            }
        }

        // Verify no notifications were sent
        assertEquals(0, sentNotifications.size());
    }

    /**
     * Tests adding a new user and verifying notification logic.
     */
    @Test
    public void testAddUserAndSendNotification() {
        // Add a new user with notifications enabled
        User newUser = new User("4", "David", null, "david@example.com", "04/04/2000", "5566778899", "Australia", false);
        newUser.setNotificationsPerm(true); // Enable notifications for David
        userList.add(newUser);

        List<String> sentNotifications = new ArrayList<>();

        for (User user : userList) {
            if (user.isNotificationsPerm()) {
                sentNotifications.add(user.getName());
            }
        }

        // Verify notifications sent to the correct users including the new user
        assertEquals(3, sentNotifications.size());
        assertTrue(sentNotifications.contains("Alice"));
        assertTrue(sentNotifications.contains("Charlie"));
        assertTrue(sentNotifications.contains("David"));
    }
}
