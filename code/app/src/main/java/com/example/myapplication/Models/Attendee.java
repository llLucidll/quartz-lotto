package com.example.myapplication.Models;

/**
 * Attendee class represents an attendee who has signed up for an event.
 */
public class Attendee {
    private String userId;
    private String userName;
    private String userEmail;
    private String status;
    private Double latitude;
    private Double longitude;

    /**
     * Default constructor for Attendee.
     */
    public Attendee() {
    }

    /**
     * Constructor for Attendee.
     * @param userId Attendee's unique identifier
     * @param userName Attendee's name
     * @param userEmail Attendee's email
     * @param status Status of the attendee (e.g., "waiting", "cancelled", "confirmed")
     * @param latitude Latitude coordinate of the user's location
     * @param longitude Longitude coordinate of the user's location
     */
    public Attendee(String userId, String userName, String userEmail, String status, Double latitude, Double longitude) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    // Getters and Setters

    /**
     * Getters for Attendee Id
     * @return Attendee's unique identifier
     */
    public String getUserId() { return userId; }

    /**
     * Setters for Attendee Id.
     * @param userId Attendee's unique identifier
     */
    public void setUserId(String userId) { this.userId = userId; }

    /**
     * Getters for Attendee Name
     * @return Attendee's name
     */
    public String getUserName() { return userName; }
    /**
     * Setters for Attendee Name.
     * @param userName Attendee's name
     */
    public void setUserName(String userName) { this.userName = userName; }

    /**
     * Getter for Attendee Email
     * @return Attendee's email
     */
    public String getUserEmail() { return userEmail; }
    /**
     * Setter for Attendee Email.
     * @param userEmail Attendee's email
     */
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    /**
     * Getter for Attendee Status
     * @return Status of the attendee
     */
    public String getStatus() { return status; }
    /**
     * Setter for Attendee Status.
     * @param status Status of the attendee
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Getter for Attendee Latitude
     * @return Latitude coordinate of the user's location
     */
    public Double getLatitude() { return latitude; }
    /**
     * Setter for Attendee Latitude.
     * @param latitude Latitude coordinate of the user's location
     */
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    /**
     * Getter for Attendee Longitude
     * @return Longitude coordinate of the user's location
     */
    public Double getLongitude() { return longitude; }
    /**
     * Setter for Attendee Longitude.
     * @param longitude Longitude coordinate of the user's location
     */
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
