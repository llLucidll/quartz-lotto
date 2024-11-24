package com.example.myapplication.Models;

/*
SuperClass for all three types of users for the application
 */

import com.example.myapplication.Facility;

import java.util.HashMap;
import java.util.List;

public class User {
    private String userID;
    private String name;
    private String profileImageUrl;
    private String email;
    private String dob;
    private String phone;
    private String country;


    private boolean isAdmin;

    private HashMap<EntrantList, String> waitingListStatus;
    private boolean notificationsPerm;

    private boolean isOrganizer;
    private Facility facility;
    private List<Event> events;

    //Default empty constructor for firebase
    public User() {}


    public User(String userID, String name, String profileImageUrl, String email, String dob, String phone, String country, boolean isAdmin) {
        this.userID = userID;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.dob = dob;
        this.phone = phone;
        this.country = country;
        this.isAdmin = isAdmin;
        this.isOrganizer = false;
        this.facility = null;
        this.events = null;
    }


    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }


    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }


    public String getDob() {
        return dob;
    }
    public void setDob(String dob) {
        this.dob = dob;
    }


    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }


    public boolean getIsAdmin() {
        return isAdmin;
    }
    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }


    public HashMap<EntrantList, String> getWaitingListStatus() {
        return waitingListStatus;
    }
    public void setWaitingListStatus(HashMap<EntrantList, String> waitingListStatus) {
        this.waitingListStatus = waitingListStatus;
    }


    public boolean getNotificationsPerm() {
        return notificationsPerm;
    }
    public void setNotificationPerm(boolean notificationsPerm) {
        notificationsPerm = true;
    }


    public boolean getIsOrganizer() {
        return isOrganizer;
    }
    public void setIsOrganizer() {
        this.isOrganizer = facility != null;
    }


    public Facility getFacility() {
        return facility;
    }
    public void setFacility(Facility facility) {
        this.facility = facility;
    }


    public List<Event> getEvents() {
        return events;
    }
    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
