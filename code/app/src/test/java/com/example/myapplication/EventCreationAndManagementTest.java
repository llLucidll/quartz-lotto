package com.example.myapplication;

import android.graphics.Bitmap;

import com.example.myapplication.Models.Event;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class EventCreationAndManagementTest {
    private Event event;
    private Facility facility;
    private CreateEventActivity createEventActivity;
    @Before
    public void setUp() {
        facility = new Facility("facilityImageUrl", "location", Collections.singletonList("facilityName"), "facilityId");
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
     * US 02.04.01 As an organizer I want to upload an event poster to provide visual information to entrants
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
