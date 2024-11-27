package com.example.myapplication.Models;

public class Attendee {
    private String userId;
    private String userName;
    private String userEmail;
    private String status;
    private Double latitude;
    private Double longitude;

    public Attendee() {
    }

    public Attendee(String userId, String userName, String userEmail, String status, Double latitude, Double longitude) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
