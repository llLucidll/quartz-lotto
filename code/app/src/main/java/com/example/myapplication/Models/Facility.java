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

    /**
     * Default constructor required for Firebase deserialization.
     * @return The URL of the facility's image.
     */

    public String getImageUrl() {
        return ImageUrl;
    }
    /**
     * Sets the URL of the facility's image.
     * @param imageUrl The URL of the facility's image.
     */

    public void setImageUrl(String imageUrl) {
        this.ImageUrl = imageUrl;
    }

    /**
     * Retrieves the location of the facility.
     * @return The location of the facility.
     */


    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the facility.
     * @param location The location of the facility.
     */

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Retrieves the name of the facility.
     * @return The name of the facility.
     */


    public String getName() {
        return name;
    }

    /**
     * Sets the name of the facility.
     * @param name The name of the facility.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the unique identifier for the facility.
     * @return The unique identifier for the facility.
     */


    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the facility.
     * @param id The unique identifier for the facility.
     */
    public void setId(String id) {
        this.id = id;
    }
}
