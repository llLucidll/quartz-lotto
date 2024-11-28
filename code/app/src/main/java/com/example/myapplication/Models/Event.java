package com.example.myapplication.Models;

import com.google.firebase.firestore.PropertyName;

/**
 * Represents an event within the application, including its details and associated facility.
 */
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
    private String organizerId; // Added organizerId field
    private Facility facility; // Associated Facility

    // Default constructor required for Firestore
    public Event() {}

    /**
     * Parameterized constructor to initialize all fields of the Event.
     *
     * @param eventId           Unique identifier for the event.
     * @param eventName         Name of the event.
     * @param drawDate          Date of the event draw.
     * @param eventDateTime     Date and time of the event.
     * @param description       Description of the event.
     * @param maxAttendees      Maximum number of attendees allowed.
     * @param maxWaitlist       Maximum number of users allowed on the waitlist.
     * @param geolocationEnabled Flag indicating if geolocation is enabled for the event.
     * @param qrCodeLink        Link associated with the event's QR code.
     * @param posterUrl         URL to the event's poster image.
     * @param currentAttendees  Current number of confirmed attendees.
     * @param organizerId       UID of the event organizer.
     * @param facility          Facility associated with the event.
     */
    public Event(String eventId, String eventName, String drawDate, String eventDateTime, String description,
                 int maxAttendees, Integer maxWaitlist, boolean geolocationEnabled, String qrCodeLink, String posterUrl,
                 int currentAttendees, String organizerId, Facility facility) {
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
        this.organizerId = organizerId;
        this.facility = facility;
    }

    // Overloaded constructor without Facility
    /**
     * Parameterized constructor to initialize Event without Facility.
     *
     * @param eventId           Unique identifier for the event.
     * @param eventName         Name of the event.
     * @param drawDate          Date of the event draw.
     * @param eventDateTime     Date and time of the event.
     * @param description       Description of the event.
     * @param maxAttendees      Maximum number of attendees allowed.
     * @param maxWaitlist       Maximum number of users allowed on the waitlist.
     * @param geolocationEnabled Flag indicating if geolocation is enabled for the event.
     * @param qrCodeLink        Link associated with the event's QR code.
     * @param posterUrl         URL to the event's poster image.
     * @param currentAttendees  Current number of confirmed attendees.
     * @param organizerId       UID of the event organizer.
     */
    public Event(String eventId, String eventName, String drawDate, String eventDateTime, String description,
                 int maxAttendees, Integer maxWaitlist, boolean geolocationEnabled, String qrCodeLink, String posterUrl,
                 int currentAttendees, String organizerId) {
        this(eventId, eventName, drawDate, eventDateTime, description,
                maxAttendees, maxWaitlist, geolocationEnabled, qrCodeLink, posterUrl,
                currentAttendees, organizerId, null);
    }

    // Simplified constructor
    public Event(String eventId, String eventName) {
        this.eventId = eventId;
        this.eventName = eventName;
    }

    // ------------------------ the normal ones ------------------------

    @PropertyName("eventId")
    public String getEventId() {
        return eventId;
    }

    @PropertyName("eventId")
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @PropertyName("eventName")
    public String getEventName() {
        return eventName;
    }

    @PropertyName("eventName")
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @PropertyName("drawDate")
    public String getDrawDate() {
        return drawDate;
    }

    @PropertyName("drawDate")
    public void setDrawDate(String drawDate) {
        this.drawDate = drawDate;
    }

    @PropertyName("eventDateTime")
    public String getEventDateTime() {
        return eventDateTime;
    }

    @PropertyName("eventDateTime")
    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    @PropertyName("description")
    public String getDescription() {
        return description;
    }

    @PropertyName("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @PropertyName("maxAttendees")
    public int getMaxAttendees() {
        return maxAttendees;
    }

    @PropertyName("maxAttendees")
    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    @PropertyName("maxWaitlist")
    public Integer getMaxWaitlist() {
        return maxWaitlist;
    }

    @PropertyName("maxWaitlist")
    public void setMaxWaitlist(Integer maxWaitlist) {
        this.maxWaitlist = maxWaitlist;
    }

    @PropertyName("geolocationEnabled")
    public boolean isGeolocationEnabled() {
        return geolocationEnabled;
    }

    @PropertyName("geolocationEnabled")
    public void setGeolocationEnabled(boolean geolocationEnabled) {
        this.geolocationEnabled = geolocationEnabled;
    }

    @PropertyName("qrCodeLink")
    public String getQrCodeLink() {
        return qrCodeLink;
    }

    @PropertyName("qrCodeLink")
    public void setQrCodeLink(String qrCodeLink) {
        this.qrCodeLink = qrCodeLink;
    }

    @PropertyName("posterUrl")
    public String getPosterUrl() {
        return posterUrl;
    }

    @PropertyName("posterUrl")
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    @PropertyName("currentAttendees")
    public int getCurrentAttendees() {
        return currentAttendees;
    }

    @PropertyName("currentAttendees")
    public void setCurrentAttendees(int currentAttendees) {
        this.currentAttendees = currentAttendees;
    }

    @PropertyName("organizerId")
    public String getOrganizerId() {
        return organizerId;
    }

    @PropertyName("organizerId")
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    @PropertyName("facility")
    public Facility getFacility() {
        return facility;
    }

    @PropertyName("facility")
    public void setFacility(Facility facility) {
        this.facility = facility;
    }
}
