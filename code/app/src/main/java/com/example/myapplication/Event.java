package com.example.myapplication;

import android.content.Context;

import java.util.Date;
import java.util.List;

public class Event {
    private WaitingList waitingList;

    private int id;
    private String name;
    private Date date;
    private String description;
    private int waitingCapacity;
    private int selectedCapacity;

    public Event(int id, String name, String description, int waitingCapacity, int selectedCapacity, Date date) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.waitingCapacity = waitingCapacity;
        this.selectedCapacity = selectedCapacity;
        this.date = date;
        this.waitingList = new WaitingList();
    }

    //Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public WaitingList getWaitingList() {
        return waitingList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    //Methods
    /*
    Adds a user to the waiting list
     */
    public boolean addWaitingUser(UserProfile user, int waitingCapacity) {
        return waitingList.addWaiter(user, waitingCapacity);
    }
    /*
    Removes a user from the waiting list
     */
    public boolean removeWaitingUser(UserProfile user) {
        return waitingList.removeWaiter(user);
    }
    /*
    Samples a specified number of users from the waiting list
     */
    public List<UserProfile> sampleAttendees(int selectedCapacity) {
        return waitingList.sampleAttendees(selectedCapacity);
    }

    public void notifySampledAttendees(List<UserProfile> sampledAttendees, Context context, NotificationService notificationService) {
        for (UserProfile user: sampledAttendees) {
            String title = "Congratulations!";
            String description = "You have been selected from the waiting list";
            NotificationService.sendNotification(user, context, title, description);

        }
    }

}
