package com.example.myapplication.Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.myapplication.Repositories.EntrantListRepository;
import com.example.myapplication.Models.User;
public class EntrantList {
    private String listId;
    private List<User> users;
    private int capacity;
    private int sampleSize;

    public EntrantList(int capacity, int sampleSize) {
        this.users = new ArrayList<>();
        this.capacity = capacity;
        this.sampleSize = sampleSize;
    }

    public void setListId() {
        listId = EntrantListRepository.createListId();
    }
    public String getListId() {
        return listId;
    }
    public List<User> getUsers() {
        return users;
    }

    public void setUser(User user) {
        users.add(user);
    }


    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    public int getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }


    public List<User> sampleAttendees(int size) {
        if (users.size() <= size) {
            return users;
        } else {
            Collections.shuffle(users);
            return users.subList(0, size);
        }
    }
}