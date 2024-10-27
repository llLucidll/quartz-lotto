package com.example.myapplication;

//for managing notification permissions.

public class NotificationService {

    //Notification Permissions
    public void optOutOfNotifications(UserProfile user) {
        user.setReceiveNotifications(false);
    }

    public void optInToNotifications(UserProfile user) {
        user.setReceiveNotifications(true);
    }

    public void updateChosenFromWaitingListPreference(UserProfile user, boolean preference) {
        user.setChosenFromWaitingList(preference);
    }

    public void updateNotChosenFromWaitingListPreference(UserProfile user, boolean preference) {
        user.setNotChosenFromWaitingList(preference);
    }
}

