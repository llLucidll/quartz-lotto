package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Facility;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for browsing a list of facilities.
 * This activity fetches facility data from Firestore and displays it in a RecyclerView.
 */
public class BrowseFacilitiesActivity extends AppCompatActivity {

    private RecyclerView facilityRecyclerView;
    private FirebaseFirestore db;
    private FacilityAdapterAdmin facilityAdapter;
    private List<Facility> facilityList;

    /**
     * Initializes the activity, sets up the RecyclerView, and fetches facilities from Firestore.
     *
     * @param savedInstanceState The saved instance state from a previous activity instance, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_facilities);

        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Firestore and RecyclerView
        db = FirebaseFirestore.getInstance();
        facilityRecyclerView = findViewById(R.id.facility_recycler_view);
        facilityRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        facilityList = new ArrayList<>();
        facilityAdapter = new FacilityAdapterAdmin(this, facilityList);
        facilityRecyclerView.setAdapter(facilityAdapter);

        // Fetch facilities
        fetchFacilities();
    }

    /**
     * Handles back button press in the toolbar.
     *
     * @param item The menu item selected.
     * @return True if the action was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Navigate back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fetches facility data from the "Facilities" collection in Firestore.
     * On success, the data is displayed in the RecyclerView.
     * On failure, a toast message is shown to indicate the error.
     */
    private void fetchFacilities() {
        db.collection("Facilities")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    facilityList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String name = document.getString("name");
                        String location = document.getString("location");
                        String imageUrl = document.getString("ImageUrl");

                        // Create Facility object
                        Facility facility = new Facility(imageUrl, location, name, id);
                        facilityList.add(facility);
                    }
                    facilityAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(BrowseFacilitiesActivity.this, "Failed to fetch facilities.", Toast.LENGTH_SHORT).show());
    }
}
