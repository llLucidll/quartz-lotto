package com.example.myapplication.Controllers;

import android.util.Log;

import com.example.myapplication.AttendeesFragment;
import com.example.myapplication.Repositories.EntrantListRepository;
import com.example.myapplication.Views.WaitingListView;
import com.example.myapplication.Views.AttendeeListView;

import java.util.ArrayList;
import java.util.HashMap;

/*
Used to handle parsing of data before passing to View for EntrantList
 */
public class EntrantListController {
    private EntrantListRepository repository;
    private HashMap<String, String> repoList;
    private WaitingListView waitlistFragment;
    private AttendeesFragment attendeeFragment;

    //Constructor
    public EntrantListController() {
        this.repository = new EntrantListRepository();
    }

    public void fetchEntrantList(String eventId, String status, EntrantListRepository.FetchEntrantListCallback callback) {
        // Directly retrieve the HashMap from the repository
         repository.getEntrantlist(eventId, status, new EntrantListRepository.FirestoreCallback() {
            @Override
            public void onSuccess(HashMap<String, String> data) {
                Log.d("EntrantListController", "Data received in Controller: " + data);
                ArrayList<String> waitingList = new ArrayList<>(data.values());
                Log.d("EntrantListController", "Fetched Entrant List in Controller: " + waitingList);
                callback.onFetchEntrantListSuccess(waitingList);
            }

            @Override
            public void onFailure(Exception e) {
                // Log or handle the failure appropriately
                System.err.println("Error fetching entrant list: " + e.getMessage());
            }
        });
    }

}
