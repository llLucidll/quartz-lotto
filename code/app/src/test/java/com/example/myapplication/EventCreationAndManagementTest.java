package com.example.myapplication;

import android.graphics.Bitmap;

import com.example.myapplication.Models.Event;
import com.example.myapplication.Models.Facility;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

/**
 * US 02.04.01 As an organizer I want to upload an event poster to provide visual information to entrants
 * US 02.04.02 As an organizer I want to update an event poster to provide visual information to entrants
 */
public class EventCreationAndManagementTest {
    private Event event;
    private Facility facility;
    private CreateEventActivity createEventActivity;
    @Before
    public void setUp() {
        facility = new Facility("facilityImageUrl", "location", "facilityName", "facilityId");
        event = new Event();

    }

    /*
     * US 02.04.01 As an organizer I want to upload an event poster to provide visual information to entrants
     */
    @Test
    public void uploadEventPosterTest() {
        setUp();
        String posterURL = "myPosterURL";
        event.setPosterUrl(posterURL);
        assert event.getPosterUrl().equals(posterURL);
    }

    /*
     * US 02.04.02 As an organizer I want to update an event poster to provide visual information to entrants
     */
    @Test
    public void updateEventPosterTest() {
        setUp();
        String posterURL = "myPosterURL";
        event.setPosterUrl(posterURL);
        posterURL = "newPosterURL";
        event.setPosterUrl(posterURL);
        assert event.getPosterUrl().equals("newPosterURL");
    }

}