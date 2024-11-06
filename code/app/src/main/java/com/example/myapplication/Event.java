package com.example.myapplication;

public class Event {
    private String eventName;
    private String date;
    private String time;
    private String description;
    private int maxAttendees;
    private Integer maxWaitlist; // Nullable
    private boolean geolocationEnabled;
    private String qrCodeLink;

    public Event(String eventName, String date, String time, String description, int maxAttendees, Integer maxWaitlist, boolean geolocationEnabled, String qrCodeLink) {
        this.eventName = eventName;
        this.date = date;
        this.time = time;
        this.description = description;
        this.maxAttendees = maxAttendees;
        this.maxWaitlist = maxWaitlist;
        this.geolocationEnabled = geolocationEnabled;
        this.qrCodeLink = qrCodeLink;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public Integer getMaxWaitlist() {
        return maxWaitlist;
    }

    public void setMaxWaitlist(Integer maxWaitlist) {
        this.maxWaitlist = maxWaitlist;
    }

    public boolean isGeolocationEnabled() {
        return geolocationEnabled;
    }

    public void setGeolocationEnabled(boolean geolocationEnabled) {
        this.geolocationEnabled = geolocationEnabled;
    }

    public String getQrCodeLink() {
        return qrCodeLink;
    }

    public void setQrCodeLink(String qrCodeLink) {
        this.qrCodeLink = qrCodeLink;
    }
}