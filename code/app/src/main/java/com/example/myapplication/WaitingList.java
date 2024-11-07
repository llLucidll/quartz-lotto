package com.example.myapplication;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/*
Waiting List class.
TODO:
 */
public class WaitingList {
    private List<UserProfile> waitingList;
    private Random random;

    public WaitingList () {
        this.waitingList = new ArrayList<>();
        this.random = new Random();

    }

    /*
    Adds a user to the waiting list.
     */

    public boolean addWaiter (UserProfile user, int capacity) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (!waitingList.contains(user) && waitingList.size() < capacity) {
            waitingList.add(user);
            return true;
        }
        return false;
    }

    /*
    Removes a user from the waiting list
     */
    public boolean removeWaiter (UserProfile user) {
        return waitingList.remove(user);
    }

    /*
    Samples a specified number of users from the waiting list
     */
    public List<UserProfile> sampleAttendees (int numAttendees) {
        if (numAttendees <= 0) {
            throw new IllegalArgumentException("Number of attendees has to be greater than 0");
        }

        int size = getWaitingListSize();
        if (numAttendees > size) {
            numAttendees = size;
        }

        List<UserProfile> shuffledList = new ArrayList<>(waitingList);
        Collections.shuffle(shuffledList, random);
        return shuffledList.subList(0, numAttendees);
        //Shuffles the list and randomly picks n users
    }
    /*
    Returns the size of the waiting list
     */
    public int getWaitingListSize() {
        return waitingList.size();
    }

    public void clearWaitingList() {
        waitingList.clear();
    }

    /*
    Returns a copy of the waiting list
     */
    public List<UserProfile> getAllAttendees() {
        return new ArrayList<>(waitingList);

    }


}
