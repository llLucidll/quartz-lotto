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
    private Integer currentWaitlist;
    private boolean geolocationEnabled;
    private String qrCodeLink;
    private String posterUrl;
    private int currentAttendees;
    private String organizerId; // Added organizerId field
    private Facility facility; // Associated Facility

    /**
     * Default constructor for Event.
     */
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

    /**
     * Simplified constructor
     * @param eventId Unique identifier for the event.
     * @param eventName Name of the event.
     */
    public Event(String eventId, String eventName) {
        this.eventId = eventId;
        this.eventName = eventName;
    }

    /**
     * Getter for EventId
     * @return Unique identifier for the event.
     */
    @PropertyName("eventId")
    public String getEventId() {
        return eventId;
    }

    /**
     * Setter for EventId
     * @param eventId Unique identifier for the event.
     */
    @PropertyName("eventId")
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Getter for EventName
     * @return Name of the event.
     */
    @PropertyName("eventName")
    public String getEventName() {
        return eventName;
    }

    /**
     * Setter for EventName
     * @param eventName Name of the event.
     */
    @PropertyName("eventName")
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Getter for draw date
     * @return Date of the event draw.
     */
    @PropertyName("drawDate")
    public String getDrawDate() {
        return drawDate;
    }

    /**
     * Setter for draw date
     * @param drawDate Date of the event draw.
     */
    @PropertyName("drawDate")
    public void setDrawDate(String drawDate) {
        this.drawDate = drawDate;
    }

    /**
     * Getter for event date time
     * @return Date and time of the event.
     */
    @PropertyName("eventDateTime")
    public String getEventDateTime() {
        return eventDateTime;
    }

    /**
     * Setter for event date time
     * @param eventDateTime Date and time of the event.
     */
    @PropertyName("eventDateTime")
    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    /**
     * Getter for description
     * @return Description of the event.
     */
    @PropertyName("description")
    public String getDescription() {
        return description;
    }

    /**
     * Setter for description
     * @param description Description of the event.
     */
    @PropertyName("description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for max attendees
     * @return Maximum number of attendees allowed.
     */
    @PropertyName("maxAttendees")
    public int getMaxAttendees() {
        return maxAttendees;
    }

    /**
     * Setter for max attendees
     * @param maxAttendees Maximum number of attendees allowed.
     */
    @PropertyName("maxAttendees")
    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    /**
     * Getter for max waitlist
     * @return Maximum number of users allowed on the waitlist.
     */
    @PropertyName("maxWaitlist")
    public Integer getMaxWaitlist() {
        return maxWaitlist;
    }

    /**
     * Setter for max waitlist
     * @param maxWaitlist Maximum number of users allowed on the waitlist.
     */
    @PropertyName("maxWaitlist")
    public void setMaxWaitlist(Integer maxWaitlist) {
        this.maxWaitlist = maxWaitlist;
    }

    /**
     * Getter for current waitlist
     * @return Current number of users on the waitlist.
     */
    @PropertyName("currentWaitlist")
    public Integer getCurrentWaitlist() {
        return currentWaitlist;
    }

    /**
     * Setter for current waitlist
     * @param currentWaitlist Current number of users on the waitlist.
     */
    @PropertyName("currentWaitlist")
    public void setCurrentWaitlist(Integer currentWaitlist) {
        this.currentWaitlist = currentWaitlist;
    }

    /**
     * Getter for geolocationEnabled
     * @return Flag indicating if geolocation is enabled for the event.
     */
    @PropertyName("geolocationEnabled")
    public boolean isGeolocationEnabled() {
        return geolocationEnabled;
    }

    /**
     * Setter for geolocationEnabled
     * @param geolocationEnabled Flag indicating if geolocation is enabled for the event.
     */
    @PropertyName("geolocationEnabled")
    public void setGeolocationEnabled(boolean geolocationEnabled) {
        this.geolocationEnabled = geolocationEnabled;
    }

    /**
     * Getter for qrCodeLink
     * @return Link associated with the event's QR code.
     */
    @PropertyName("qrCodeLink")
    public String getQrCodeLink() {
        return qrCodeLink;
    }

    /**
     * Setter for qrCodeLink
     * @param qrCodeLink Link associated with the event's QR code.
     */
    @PropertyName("qrCodeLink")
    public void setQrCodeLink(String qrCodeLink) {
        this.qrCodeLink = qrCodeLink;
    }

    /**
     * Getter for posterUrl
     * @return URL to the event's poster image.
     */
    @PropertyName("posterUrl")
    public String getPosterUrl() {
        return posterUrl;
    }

    /**
     * Setter for posterUrl
     * @param posterUrl URL to the event's poster image.
     */
    @PropertyName("posterUrl")
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    /**
     * Getter for currentAttendees
     * @return Current number of confirmed attendees.
     */
    @PropertyName("currentAttendees")
    public int getCurrentAttendees() {
        return currentAttendees;
    }

    /**
     * Setter for currentAttendees
     * @param currentAttendees Current number of confirmed attendees.
     */
    @PropertyName("currentAttendees")
    public void setCurrentAttendees(int currentAttendees) {
        this.currentAttendees = currentAttendees;
    }

    /**
     * Getter for organizerId
     * @return UID of the event organizer.
     */
    @PropertyName("organizerId")
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * Setter for organizerId
     * @param organizerId UID of the event organizer.
     */
    @PropertyName("organizerId")
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    /**
     * Getter for facility
     * @return Facility associated with the event.
     */
    @PropertyName("facility")
    public Facility getFacility() {
        return facility;
    }

    /**
     * Setter for facility
     * @param facility Facility associated with the event.
     */
    @PropertyName("facility")
    public void setFacility(Facility facility) {
        this.facility = facility;
    }
}
