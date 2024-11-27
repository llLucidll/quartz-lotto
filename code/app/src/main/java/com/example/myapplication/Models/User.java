package com.example.myapplication.Models;

import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a user in the application, encompassing both general profile information
 * and event-specific attendance details.
 */
public class User {
    // General User Information
    private String userID;
    private String name;
    private String profileImageUrl;
    private String email;
    private String dob; // Date of Birth
    private String phone;
    private String country;

    private boolean isAdmin;
    private boolean notificationsPerm;
    private boolean isOrganizer;

    private Map<String, EventAttendance> eventsAttending; // Key: eventId, Value: attendance details
    private List<Event> events; // List of events the user is associated with

    // Default empty constructor for Firebase deserialization
    public User() {
        this.eventsAttending = new HashMap<>();
    }

    /**
     * Constructs a User with general profile information.
     *
     * @param userID           Unique identifier for the user.
     * @param name             Full name of the user.
     * @param profileImageUrl  URL to the user's profile image.
     * @param email            Email address of the user.
     * @param dob              Date of birth of the user.
     * @param phone            Phone number of the user.
     * @param country          Country of residence of the user.
     * @param isAdmin          Indicates if the user has admin privileges.
     */
    public User(String userID, String name, String profileImageUrl, String email, String dob, String phone, String country, boolean isAdmin) {
        this.userID = userID;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.dob = dob;
        this.phone = phone;
        this.country = country;
        this.isAdmin = isAdmin;
        this.notificationsPerm = false;
        this.isOrganizer = false;
        this.eventsAttending = new HashMap<>();
        this.events = null;
    }

    // ------------------------ the normal other getters and setters ------------------------

    @PropertyName("userID")
    public String getUserID() {
        return userID;
    }

    @PropertyName("userID")
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("profileImageUrl")
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    @PropertyName("profileImageUrl")
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @PropertyName("email")
    public String getEmail() {
        return email;
    }

    @PropertyName("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("dob")
    public String getDob() {
        return dob;
    }

    @PropertyName("dob")
    public void setDob(String dob) {
        this.dob = dob;
    }

    @PropertyName("phone")
    public String getPhone() {
        return phone;
    }

    @PropertyName("phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @PropertyName("country")
    public String getCountry() {
        return country;
    }

    @PropertyName("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @PropertyName("isAdmin")
    public boolean isAdmin() {
        return isAdmin;
    }

    @PropertyName("isAdmin")
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @PropertyName("notificationsPerm")
    public boolean isNotificationsPerm() {
        return notificationsPerm;
    }

    @PropertyName("notificationsPerm")
    public void setNotificationsPerm(boolean notificationsPerm) {
        this.notificationsPerm = notificationsPerm;
    }

    @PropertyName("isOrganizer")
    public boolean isOrganizer() {
        return isOrganizer;
    }

    @PropertyName("isOrganizer")
    public void setOrganizer(boolean organizer) {
        isOrganizer = organizer;
    }

    @PropertyName("events")
    public List<Event> getEvents() {
        return events;
    }

    @PropertyName("events")
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    // ------------------------ Event Attendance Getters and Setters ------------------------

    /**
     * Retrieves the map of events the user is attending.
     *
     * @return Map with eventId as key and EventAttendance as value.
     */
    @PropertyName("eventsAttending")
    public Map<String, EventAttendance> getEventsAttending() {
        return eventsAttending;
    }

    /**
     * Sets the map of events the user is attending.
     *
     * @param eventsAttending Map with eventId as key and EventAttendance as value.
     */
    @PropertyName("eventsAttending")
    public void setEventsAttending(Map<String, EventAttendance> eventsAttending) {
        this.eventsAttending = eventsAttending;
    }

    /**
     * Adds or updates an event attendance entry for the user.
     *
     * @param eventId   Unique identifier for the event.
     * @param attendance EventAttendance object containing status and location.
     */
    public void addOrUpdateEventAttendance(String eventId, EventAttendance attendance) {
        if (this.eventsAttending == null) {
            this.eventsAttending = new HashMap<>();
        }
        this.eventsAttending.put(eventId, attendance);
    }

    /**
     * Removes an event attendance entry from the user.
     *
     * @param eventId Unique identifier for the event to be removed.
     */
    public void removeEventAttendance(String eventId) {
        if (this.eventsAttending != null) {
            this.eventsAttending.remove(eventId);
        }
    }

    // ------------------------ Inner Class: EventAttendance ------------------------

    /**
     * Represents the attendance details of a user for a specific event.
     */
    public static class EventAttendance {
        private String status; // e.g., "waiting", "cancelled", "confirmed"
        private Double latitude;
        private Double longitude;

        // Default constructor for Firebase deserialization
        public EventAttendance() {
        }

        /**
         * Constructs an EventAttendance object with status and location.
         *
         * @param status    Status of the attendance (e.g., "waiting").
         * @param latitude  Latitude coordinate of the user's location.
         * @param longitude Longitude coordinate of the user's location.
         */
        public EventAttendance(String status, Double latitude, Double longitude) {
            this.status = status;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @PropertyName("status")
        public String getStatus() {
            return status;
        }

        @PropertyName("status")
        public void setStatus(String status) {
            this.status = status;
        }

        @PropertyName("latitude")
        public Double getLatitude() {
            return latitude;
        }

        @PropertyName("latitude")
        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        @PropertyName("longitude")
        public Double getLongitude() {
            return longitude;
        }

        @PropertyName("longitude")
        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
    }
}
