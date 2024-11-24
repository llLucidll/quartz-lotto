package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for browsing a list of users.
 * This activity fetches user data from Firestore and displays it in a RecyclerView.
 */
public class BrowseUsersActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private FirebaseFirestore db;
    private UserAdapter userAdapter;
    private List<User> userList;

    /**
     * Initializes the activity, sets up the RecyclerView, and fetches user profiles from Firestore.
     *
     * @param savedInstanceState The saved instance state from a previous activity instance, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_users);

        // Initialize Firestore and RecyclerView
        db = FirebaseFirestore.getInstance();
        userRecyclerView = findViewById(R.id.user_recycler_view);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList);
        userRecyclerView.setAdapter(userAdapter);

        // Fetch user profiles
        fetchUserProfiles();
    }

    /**
     * Fetches user data from the "users" collection in Firestore.
     * On success, the data is displayed in the RecyclerView.
     * On failure, a toast message is shown to indicate the error.
     */
    private void fetchUserProfiles() {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String userID = document.getId();
                        String name = document.getString("name");
                        String profileImageUrl = document.getString("profileImageUrl");
                        String email = document.getString("email");
                        String dob = document.getString("dob");
                        String phone = document.getString("phone");
                        String country = document.getString("country");
                        boolean isAdmin = document.getBoolean("isAdmin") != null && document.getBoolean("isAdmin");

                        // Create User object
                        User user = new User(userID, name, profileImageUrl, email, dob, phone, country, isAdmin);
                        userList.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(BrowseUsersActivity.this, "Failed to fetch users.", Toast.LENGTH_SHORT).show());
    }
}
