package com.example.myapplication;


//User Profile Class.

import android.content.Context;

import java.util.Objects;

/*
Used for holding all the preferences, profile picture, fields information for each user profile.
 */
public class UserProfile {
    private int id;
    private String name;
    private boolean receiveNotifications;
    private boolean chosenFromWaitingList;
    private boolean notChosenFromWaitingList;

    private UserProfile user;

    public UserProfile() {
        // Default is true
        this.id = id;
        this.name = name;
        this.receiveNotifications = true;
        this.chosenFromWaitingList = true;
        this.notChosenFromWaitingList = true;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserProfile that = (UserProfile) o;

        return id == that.id;
    }

    //For comparision between users and equality checks.
    public int hashCode() {
        return Objects.hash(id);
    }

    // Getters and setters
    public boolean isReceivingNotifications() {
        return receiveNotifications;
        //notification preference
    }

    public void setReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }


    public boolean isChosenFromWaitingList() {
        return chosenFromWaitingList;
        //Status of user updated to true when selected from waiting list.
    }

    public void setNotifyChosenFromWaitingList(boolean chosenFromWaitingList) {
        this.chosenFromWaitingList = chosenFromWaitingList;
        //True = wants notification when selected from waiting list.
    }


    public void setNotifyNotChosenFromWaitingList(boolean notChosenFromWaitingList) {
        this.notChosenFromWaitingList = notChosenFromWaitingList;
        //True = wants notification when not selected from waiting list.
    }

}