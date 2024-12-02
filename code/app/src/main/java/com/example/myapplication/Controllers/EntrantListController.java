package com.example.myapplication.Controllers;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.AttendeesFragment;
import com.example.myapplication.Repositories.EntrantListRepository;
import com.example.myapplication.Views.WaitingListView;

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

    /*
    Method that connects to the draw button on WaitlistFragment
     */
    public void drawAttendees(String eventId, boolean redraw, Context context) {
        if (redraw) {
            int size = 1;
            repository.sampleAttendees(eventId, size, context);
            Log.d("EntrantListController", "AttendeeSize" + size);
        } else {
            repository.getAttendeeListSize(eventId, new EntrantListRepository.Callback<Long>() {
                @Override
                public void onComplete(Long result, Exception e) {
                    if (e != null) {
                        Log.e("EntrantListController", "Error in getting attendee size" + result);
                    }
                    int size = result.intValue();
                    Log.d("EntrantListController", "Got attendee size" + size);
                    repository.sampleAttendees(eventId, size, context);

                }
            });

        }
    }

}