package com.example.myapplication.Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.myapplication.Repositories.EntrantListRepository;
import com.example.myapplication.Models.User;

/*
Model class used by Entrant List for each event.
 */
public class EntrantList {
    private List<Attendee> attendees;
    private int capacity;
    private int sampleSize;

    public EntrantList(int capacity, int sampleSize) {
        this.attendees = new ArrayList<>();
        this.capacity = capacity;
        this.sampleSize = sampleSize;
    }

    public List<Attendee> getUsers() {
        return attendees;
    }

    public void setUser(Attendee user) {
        attendees.add(user);
    }


    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    public int getSampleSize() {
        return sampleSize;
    }

    /*
    gets the maxAttendees from the database for the particular event.
     */
    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    /*
    Samples a random subset of attendees
     */
    public ArrayList<Attendee> sampleAttendees(int size) {
        if (attendees.size() <= size) {
            return (ArrayList<Attendee>) attendees;
        } else {
            Collections.shuffle(attendees);
            return (ArrayList<Attendee>) attendees.subList(0, size);
        }
    }
}