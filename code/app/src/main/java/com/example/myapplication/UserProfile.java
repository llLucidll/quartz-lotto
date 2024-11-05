package com.example.myapplication;


//User Profile Class.

import android.content.Context;

/*
Used for holding all the preferences, profile picture, fields information for each user profile.
 */
public class UserProfile {
    private boolean receiveNotifications;
    private boolean chosenFromWaitingList;
    private boolean notChosenFromWaitingList;
    private UserProfile user;
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
        //notification preference
    }

    public void setReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }


    //TODO MAKE THIS A LIST IMPLEMENTATION AS USERS CAN JOIN MULTIPLE WAITING LISTS.
    public boolean isChosenFromWaitingList() {
        return chosenFromWaitingList;
        //Status of user updated to true when selected from waiting list.
        //TODO CAN CHANGE!
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