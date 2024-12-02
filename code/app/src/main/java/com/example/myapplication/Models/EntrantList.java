package com.example.myapplication.Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.myapplication.Repositories.EntrantListRepository;
import com.example.myapplication.Models.User;

/**
 * Model class used by Entrant List for each event.
 */
public class EntrantList {
    private List<Attendee> attendees;
    private int capacity;
    private int sampleSize;

    /**
     * Constructor for EntrantList.
     * @param capacity The maximum number of attendees allowed.
     * @param sampleSize The number of attendees to be sampled.
     */
    public EntrantList(int capacity, int sampleSize) {
        this.attendees = new ArrayList<>();
        this.capacity = capacity;
        this.sampleSize = sampleSize;
    }

    /**
     * Getter for attendees.
     * @return List of attendees.
     */
    public List<Attendee> getUsers() {
        return attendees;
    }

    /**
     * Setter for attendees.
     * @param user Attendee to be added.
     */
    public void setUser(Attendee user) {
        attendees.add(user);
    }


    /**
     * Getter for capacity.
     * @return Maximum number of attendees allowed.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Setter for capacity.
     * @param capacity Maximum number of attendees allowed.
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    /**
     * Getter for sampleSize.
     * @return size to draw from the list of attendees.
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Setter for sampleSize.
     * @param sampleSize size to draw from the list of attendees.
     */
    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    /**
     * Samples a random subset of attendees. Method here only for testing using underlying model class.
     * @param size The number of attendees to be sampled.
     */
    public ArrayList<Attendee> sampleAttendees(int size) {
        if (attendees.size() <= size) {
            return (ArrayList<Attendee>) attendees;
        } else {
            Collections.shuffle(attendees);
            List<Attendee> sample = attendees.subList(0, size);
            ArrayList<Attendee> sampledList = new ArrayList<>(sample);
            return sampledList;
        }
    }
}