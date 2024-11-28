package com.example.myapplication.Controllers;

import com.example.myapplication.Models.Facility;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller for managing facilities.
 * Handles Firestore operations such as fetching facility data.
 */
public class BrowseFacilitiesController {

    private final FirebaseFirestore db;

    /**
     * Constructs a BrowseFacilitiesController and initializes Firestore.
     */
    public BrowseFacilitiesController() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Fetches facility data from the "Facilities" collection in Firestore.
     *
     * @param onSuccess Callback invoked when facilities are successfully fetched.
     *                  Provides a list of Facility objects.
     * @param onFailure Callback invoked when fetching fails.
     *                  Provides an error message.
     */
    public void fetchFacilities(Consumer<List<Facility>> onSuccess, Consumer<String> onFailure) {
        db.collection("Facilities")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Facility> facilities = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String name = document.getString("name");
                        String location = document.getString("location");
                        String imageUrl = document.getString("ImageUrl");

                        facilities.add(new Facility(imageUrl, location, name, id));
                    }
                    onSuccess.accept(facilities);
                })
                .addOnFailureListener(e -> onFailure.accept("Failed to fetch facilities: " + e.getMessage()));
    }
}
