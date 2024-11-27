//ayesha
package com.example.myapplication.Models;

public class StorageImage {
    private String name;
    private String path;
    private String url;


    public StorageImage() {}

    public StorageImage(String name, String path, String url) {
        this.name = name;
        this.path = path;
        this.url = url;
    }

    // Getters
    public String getName() { return name; }
    public String getPath() { return path; }
    public String getUrl() { return url; }
}
