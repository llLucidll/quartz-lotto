package com.example.myapplication.Models;

import com.google.firebase.firestore.PropertyName;

public class Attendee {
    private String id;
    private String name;
    private String status;

    public Attendee() {
    }

    public Attendee(String id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("userName")
    public String getName() {
        return name;
    }

    @PropertyName("userName")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("status")
    public String getStatus() {
        return status;
    }

    @PropertyName("status")
    public void setStatus(String status) {
        this.status = status;
    }
}
