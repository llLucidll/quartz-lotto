package com.example.myapplication.Models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit test for browsing users using the User model.
 */
public class BrowseUsersTest {

    /**
     * Tests the creation and retrieval of a list of users.
     */
    @Test
    public void testBrowseUsers() {
        // Create a list of users
        List<User> users = new ArrayList<>();
        users.add(new User("1", "John Doe", "https://example.com/john.jpg", "john@example.com", "01/01/1990", "1234567890", "USA", true));
        users.add(new User("2", "Jane Smith", "https://example.com/jane.jpg", "jane@example.com", "02/02/1985", "0987654321", "Canada", false));
        users.add(new User("3", "Bob Brown", null, "bob@example.com", "03/03/1995", "5678901234", "UK", false));

        // Validate the size of the list
        assertEquals(3, users.size());

        // Validate the first user
        User user1 = users.get(0);
        assertEquals("1", user1.getUserID());
        assertEquals("John Doe", user1.getName());
        assertEquals("https://example.com/john.jpg", user1.getProfileImageUrl());
        assertEquals("john@example.com", user1.getEmail());
        assertEquals("01/01/1990", user1.getDob());
        assertEquals("1234567890", user1.getPhone());
        assertEquals("USA", user1.getCountry());
        assertTrue(user1.isAdmin());

        // Validate the second user
        User user2 = users.get(1);
        assertEquals("2", user2.getUserID());
        assertEquals("Jane Smith", user2.getName());
        assertEquals("https://example.com/jane.jpg", user2.getProfileImageUrl());
        assertEquals("jane@example.com", user2.getEmail());
        assertEquals("02/02/1985", user2.getDob());
        assertEquals("0987654321", user2.getPhone());
        assertEquals("Canada", user2.getCountry());
        assertFalse(user2.isAdmin());

        // Validate the third user
        User user3 = users.get(2);
        assertEquals("3", user3.getUserID());
        assertEquals("Bob Brown", user3.getName());
        assertNull(user3.getProfileImageUrl());
        assertEquals("bob@example.com", user3.getEmail());
        assertEquals("03/03/1995", user3.getDob());
        assertEquals("5678901234", user3.getPhone());
        assertEquals("UK", user3.getCountry());
        assertFalse(user3.isAdmin());
    }
}
