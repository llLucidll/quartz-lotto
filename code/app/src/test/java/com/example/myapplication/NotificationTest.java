package com.example.myapplication.Models;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for testing notification permissions.
 * Verifies that notifications are sent only to users with notification permissions enabled.
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
