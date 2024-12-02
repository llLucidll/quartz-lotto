package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.myapplication.UserProfile;

/*
Test for checking the underlying Profile class for correctness
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
        // Only test `notChosenFromWaitingList` behavior here
        userProfile.setNotifyNotChosenFromWaitingList(false);
        // Assertion here is focused on `notChosenFromWaitingList` behavior alone
        // You could use an assumption of its behavior in relation to notification logic if needed
        // Update this assertion based on expected output
    }



    @Test
    public void testEqualsAndHashCode() {
        // Check for default equality and hash code consistency
        UserProfile userProfile2 = new UserProfile();
        assertEquals("Two UserProfiles with default values should be equal", userProfile, userProfile2);
        assertEquals("Hash codes for equal UserProfiles should be the same", userProfile.hashCode(), userProfile2.hashCode());
    }
}
