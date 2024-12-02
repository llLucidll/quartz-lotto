package com.example.myapplication;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.example.myapplication.Models.Facility;

/**
 * Unit test for browsing facilities.
 * Tests adding, retrieving, and removing facilities from a list.
 * US 03.07.01 As an administrator I want to remove facilities that violate app policy
 */
public class BrowseFacilityTest {

    private List<Facility> facilityList;

    /**
     * Sets up a mock list of facilities before each test.
     */
    @Before
    public void setUp() {
        // Initialize mock facilities
        facilityList = new ArrayList<>();
        facilityList.add(new Facility("https://example.com/image1.jpg", "Location 1", "Facility 1", "1"));
        facilityList.add(new Facility("https://example.com/image2.jpg", "Location 2", "Facility 2", "2"));
        facilityList.add(new Facility("https://example.com/image3.jpg", "Location 3", "Facility 3", "3"));
    }

    /**
     * Tests browsing facilities by verifying the facility list's size
     * and the details of each facility.
     */
    @Test
    public void testBrowseFacilities() {
        // Check the size of the facility list
        assertEquals(3, facilityList.size());

        // Validate the first facility's details
        Facility facility1 = facilityList.get(0);
        assertEquals("https://example.com/image1.jpg", facility1.getImageUrl());
        assertEquals("Location 1", facility1.getLocation());
        assertEquals("Facility 1", facility1.getName());
        assertEquals("1", facility1.getId());

        // Validate the second facility's details
        Facility facility2 = facilityList.get(1);
        assertEquals("https://example.com/image2.jpg", facility2.getImageUrl());
        assertEquals("Location 2", facility2.getLocation());
        assertEquals("Facility 2", facility2.getName());
        assertEquals("2", facility2.getId());

        // Validate the third facility's details
        Facility facility3 = facilityList.get(2);
        assertEquals("https://example.com/image3.jpg", facility3.getImageUrl());
        assertEquals("Location 3", facility3.getLocation());
        assertEquals("Facility 3", facility3.getName());
        assertEquals("3", facility3.getId());
    }

    /**
     * Tests adding a new facility to the facility list
     * and verifies that it is added correctly.
     */
    @Test
    public void testAddFacility() {
        // Add a new facility to the list
        Facility newFacility = new Facility("https://example.com/image4.jpg", "Location 4", "Facility 4", "4");
        facilityList.add(newFacility);

        // Verify the new size of the list
        assertEquals(4, facilityList.size());

        // Validate the newly added facility's details
        Facility addedFacility = facilityList.get(3);
        assertEquals("https://example.com/image4.jpg", addedFacility.getImageUrl());
        assertEquals("Location 4", addedFacility.getLocation());
        assertEquals("Facility 4", addedFacility.getName());
        assertEquals("4", addedFacility.getId());
    }

    /**
     * Tests removing a facility from the facility list
     * and ensures the list updates correctly.
     */
    @Test
    public void testRemoveFacility() {
        // Remove the second facility from the list
        facilityList.remove(1);

        // Verify the new size of the list
        assertEquals(2, facilityList.size());

        // Validate that the remaining second facility has the correct details
        Facility remainingFacility = facilityList.get(1); // Check the new second facility
        assertEquals("https://example.com/image3.jpg", remainingFacility.getImageUrl()); // Correct field: ImageUrl
        assertEquals("Location 3", remainingFacility.getLocation());
        assertEquals("Facility 3", remainingFacility.getName());
        assertEquals("3", remainingFacility.getId());
    }

}

