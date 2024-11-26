package com.example.myapplication.Models;

public class Event {
    private String eventId;
    private String eventName;
    private String drawDate;
    private String eventDateTime;
    private String description;
    private int maxAttendees;
    private Integer maxWaitlist; // Nullable
    private boolean geolocationEnabled;
    private String qrCodeLink;
    private String posterUrl;
    private int currentAttendees;

    // Default constructor required for Firestore
    public Event() {}

    // Parameterized constructor
    public Event(String eventId, String eventName, String drawDate, String eventDateTime, String description,
                 int maxAttendees, Integer maxWaitlist, boolean geolocationEnabled, String qrCodeLink, String posterUrl, int currentAttendees) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.drawDate = drawDate;
        this.eventDateTime = eventDateTime;
        this.description = description;
        this.maxAttendees = maxAttendees;
        this.maxWaitlist = maxWaitlist;
        this.geolocationEnabled = geolocationEnabled;
        this.qrCodeLink = qrCodeLink;
        this.posterUrl = posterUrl;
        this.currentAttendees = currentAttendees;
    }

    // Getters and Setters

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDrawDate() {
        return drawDate;
    }

    public void setDrawDate(String drawDate) {
        this.drawDate = drawDate;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
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

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public int getCurrentAttendees() {
        return currentAttendees;
    }

    public void setCurrentAttendees(int currentAttendees) {
        this.currentAttendees = currentAttendees;
    }
}
