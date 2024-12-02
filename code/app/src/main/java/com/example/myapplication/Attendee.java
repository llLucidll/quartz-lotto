package com.example.myapplication;

/**
 * Attendee represents an attendee in an event.
 */
public class Attendee {
    private String userID;
    private String name;
    private String status;

    public Attendee() {}

    public Attendee(String userID, String name, String status) {
        this.userID = userID;
        this.name = name;
        this.status = status;
    }

    public Attendee(String name, String status) {
        this.name = name;
        this.status = status;
    }

    /**
     * Getter and setter methods for the Attendee class.
     * @return userID, name, status
     */
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
