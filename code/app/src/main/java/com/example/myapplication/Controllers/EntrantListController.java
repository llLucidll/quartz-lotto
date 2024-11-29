package com.example.myapplication.Controllers;

import android.util.Log;

import com.example.myapplication.AttendeesFragment;
import com.example.myapplication.Repositories.EntrantListRepository;
import com.example.myapplication.Views.WaitingListView;
import com.example.myapplication.Views.AttendeeListView;

import java.util.ArrayList;
import java.util.HashMap;
import com.example.myapplication.Models.Attendee;
import com.example.myapplication.Models.EntrantList;

/*
Used to handle parsing of data before passing to View for EntrantList
 */
public class EntrantListController {
    private EntrantListRepository repository;
    private HashMap<String, String> repoList;
    private WaitingListView waitlistFragment;
    private AttendeesFragment attendeeFragment;
    private EntrantList entrantList;

    //Constructor
    public EntrantListController() {
        this.repository = new EntrantListRepository();
    }
    public interface AttendeesCallback {
        void onComplete(ArrayList<Attendee> attendees, Exception e);
    }

    public void fetchEntrantList(String eventId, String status, EntrantListRepository.FetchEntrantListCallback callback) {
        // Directly retrieve the HashMap from the repository
        repository.getEntrantlist(eventId, status, new EntrantListRepository.FirestoreCallback() {
            @Override
            public void onSuccess(ArrayList<Attendee> data) {
                Log.d("EntrantListController", "Data received in Controller: " + data);
                callback.onFetchEntrantListSuccess(data);
            }

            @Override
            public void onFailure(Exception e) {
                // Log or handle the failure appropriately
                System.err.println("Error fetching entrant list: " + e.getMessage());
            }
        });
    }

    /**
     * Used to sample the entrants for the draw by using the method from the EntrantList model class
     * @param entrants
     * @return attendees
     */
    public void sampleEntrants(String eventId, ArrayList<Attendee> entrants, AttendeesCallback callback) {

        repository.getAttendeeListSize(eventId, (result, e) -> {
            if (e != null) {
                Log.e("EntrantListController", "Error getting attendee list size", e);
                callback.onComplete(null, e);
            } else {
                int sampleSize = result.intValue();;
                ArrayList<Attendee> attendees = entrantList.sampleAttendees(sampleSize);
                callback.onComplete(attendees, null);
            }
        });

    }

}