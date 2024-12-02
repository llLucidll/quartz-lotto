package com.example.myapplication.Models;

/**
 * Facility class represents a facility associated with an event.
 */
public class Facility {
    private String ImageUrl;
    private String location;
    private String name;
    private String id;

    /**
     * @param imageUrl The URL of the facility's image.
     * @param location The location of the facility.
     * @param name The name of the facility.
     * @param id The unique identifier for the facility.
     */
    public Facility(String imageUrl, String location, String name, String id) {
        this.ImageUrl = imageUrl;
        this.location = location;
        this.name = name;
        this.id = id;
    }

    public String getImageUrl() {
        return ImageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.ImageUrl = imageUrl;
    }


    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
