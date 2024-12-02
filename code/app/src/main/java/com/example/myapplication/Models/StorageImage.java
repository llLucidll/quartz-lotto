//ayesha
package com.example.myapplication.Models;

/**
 * StorageImage class represents an image stored in Firebase Storage.
 */
public class StorageImage {
    private String name;
    private String path;
    private String url;

    /**
     * Default constructor required for Firebase deserialization.
     */
    public StorageImage() {}

    /**
     *
     * @param name The name of the image.
     * @param path The path of the image.
     * @param url The URL of the image.
     */
    public StorageImage(String name, String path, String url) {
        this.name = name;
        this.path = path;
        this.url = url;
    }

    // Getters

    /**
     * Retrieves the name of the image.
     * @return The name of the image.
     */
    public String getName() { return name; }

    /**
     * Retrieves the path of the image.
     * @return The path of the image.
     */
    public String getPath() { return path; }

    /**
     * Retrieves the URL of the image.
     * @return The URL of the image.
     */
    public String getUrl() { return url; }
}
