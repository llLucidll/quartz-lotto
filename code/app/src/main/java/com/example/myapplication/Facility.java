package com.example.myapplication;

import java.util.List;

public class Facility {
    private String id;
    private String name;
    private String location;
    private List<String> imageUrls;
    private String organizerId;

    public Facility() {}

    public Facility(String name, String location, List<String> imageUrls, String organizerId) {
        this.name = name;
        this.location = location;
        this.imageUrls = imageUrls;
        this.organizerId = organizerId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }
}
