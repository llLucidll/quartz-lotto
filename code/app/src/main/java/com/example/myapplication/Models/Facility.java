package com.example.myapplication.Models;

public class Facility {
    private String ImageUrl;
    private String location;
    private String name;
    private String id;

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
