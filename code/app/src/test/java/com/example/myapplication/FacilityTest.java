package com.example.myapplication;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import com.example.myapplication.Models.Facility;

/**
 * Unit tests for the Facility class.
 * US 02.01.03 As an organizer, I want to create and manage my facility profile.
 */
public class FacilityTest {

    private Facility facility;

    @Before
    public void setUp() {
        facility = new Facility(
                "https://example.com/image.jpg",
                "123 Main St",
                "Community Center",
                "1"
        );
    }

    @Test
    public void testImageUrl() {
        assertEquals("https://example.com/image.jpg", facility.getImageUrl());
        facility.setImageUrl("https://example.com/new-image.jpg");
        assertEquals("https://example.com/new-image.jpg", facility.getImageUrl());
    }

    @Test
    public void testLocation() {
        assertEquals("123 Main St", facility.getLocation());
        facility.setLocation("456 Elm St");
        assertEquals("456 Elm St", facility.getLocation());
    }

    @Test
    public void testName() {
        assertEquals("Community Center", facility.getName());
        facility.setName("Sports Complex");
        assertEquals("Sports Complex", facility.getName());
    }

    @Test
    public void testId() {
        assertEquals("1", facility.getId());
        facility.setId("2");
        assertEquals("2", facility.getId());
    }
}
