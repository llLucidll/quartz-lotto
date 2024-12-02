package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.myapplication.UserProfile;

/**
 * Unit test for the UserProfile class.
 * US 01.01.01 As an entrant, I want to join the waiting list for a specific event
 * US 01.01.02 As an entrant, I want to leave the waiting list for a specific event
 * US 01.04.03 As an entrant I want to opt out of receiving notifications from organizers and admin
 * US 01.02.01 As an entrant, I want to provide my personal information such as name, email and optional phone number in the app
 * US 01.07.01 As an entrant, I want to be identified by my device, so that I don't have to use a username and password
 */
public class UserProfileTest {
    private UserProfile userProfile;

    @Before
    public void setUp() {
        userProfile = new UserProfile();
    }

    @Test
    public void testConstructorDefaults() {
        // Test default values for fields in the constructor
        assertTrue("Default notifications setting should be true", userProfile.isReceivingNotifications());
        assertTrue("Default chosen from waiting list should be true", userProfile.isChosenFromWaitingList());
    }

    @Test
    public void testGetId() {
        // Test the getId method to verify that ID retrieval works as expected
        assertEquals("Default ID should be 0 or uninitialized", 0, userProfile.getId());
    }

    @Test
    public void testSetReceiveNotifications() {
        // Toggle the receive notifications setting
        userProfile.setReceiveNotifications(false);
        assertFalse("Receive notifications should be false after setting it to false", userProfile.isReceivingNotifications());

        userProfile.setReceiveNotifications(true);
        assertTrue("Receive notifications should be true after setting it to true", userProfile.isReceivingNotifications());
    }

    @Test
    public void testSetNotifyChosenFromWaitingList() {
        // Toggle the chosen from waiting list setting
        userProfile.setNotifyChosenFromWaitingList(false);
        assertFalse("Chosen from waiting list should be false after setting it to false", userProfile.isChosenFromWaitingList());

        userProfile.setNotifyChosenFromWaitingList(true);
        assertTrue("Chosen from waiting list should be true after setting it to true", userProfile.isChosenFromWaitingList());
    }

    @Test
    public void testSetNotifyNotChosenFromWaitingList() {
        userProfile.setNotifyNotChosenFromWaitingList(false);
        assert(userProfile.isChosenFromWaitingList());
    }



    @Test
    public void testEqualsAndHashCode() {
        // Check for default equality and hash code consistency
        UserProfile userProfile2 = new UserProfile();
        assertEquals("Two UserProfiles with default values should be equal", userProfile, userProfile2);
        assertEquals("Hash codes for equal UserProfiles should be the same", userProfile.hashCode(), userProfile2.hashCode());
    }
}
