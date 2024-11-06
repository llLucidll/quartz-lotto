package com.example.myapplication;


//User Profile Class.

/*
Used for holding all the preferences, profile picture, fields information for each user profile.
 */
public class UserProfile {
    private boolean receiveNotifications;
    private boolean chosenFromWaitingList;
    private boolean notChosenFromWaitingList;
    //TODO profile picture, name,email dob fields, country region selection, facility preferences?

    public UserProfile() {
        // Default is true
        this.receiveNotifications = true;
        this.chosenFromWaitingList = true;
        this.notChosenFromWaitingList = true;
    }

    // Getters and setters
    public boolean isReceivingNotifications() {
        return receiveNotifications;
    }

    public void setReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }

    public boolean isChosenFromWaitingList() {
        return chosenFromWaitingList;
    }

    public void setChosenFromWaitingList(boolean chosenFromWaitingList) {
        this.chosenFromWaitingList = chosenFromWaitingList;
    }

    public boolean isNotChosenFromWaitingList() {
        return notChosenFromWaitingList;
    }

    public void setNotChosenFromWaitingList(boolean notChosenFromWaitingList) {
        this.notChosenFromWaitingList = notChosenFromWaitingList;
    }
}