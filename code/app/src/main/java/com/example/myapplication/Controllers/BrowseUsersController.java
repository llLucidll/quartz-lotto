package com.example.myapplication.Controllers;

import com.example.myapplication.Models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller for managing user data.
 * Handles Firestore operations related to users.
 */
public class BrowseUsersController {

    private final FirebaseFirestore db;

    /**
     * Constructs a BrowseUsersController and initializes Firestore.
     */
    public BrowseUsersController() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Fetches user data from the "users" collection in Firestore.
     *
     * @param onSuccess Callback invoked with a list of User objects on successful fetch.
     * @param onFailure Callback invoked with an error message on failure.
     */
    public void fetchUsers(Consumer<List<User>> onSuccess, Consumer<String> onFailure) {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String userID = document.getId();
                        String name = document.getString("name");
                        String profileImageUrl = document.getString("profileImageUrl");
                        String email = document.getString("email");
                        String dob = document.getString("dob");
                        String phone = document.getString("phone");
                        String country = document.getString("country");
                        Boolean isAdmin = document.getBoolean("isAdmin");

                        users.add(new User(userID, name, profileImageUrl, email, dob, phone, country, isAdmin != null && isAdmin));
                    }
                    onSuccess.accept(users);
                })
                .addOnFailureListener(e -> onFailure.accept("Failed to fetch users: " + e.getMessage()));
    }
}
