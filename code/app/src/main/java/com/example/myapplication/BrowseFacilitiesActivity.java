package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Facility;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BrowseFacilitiesActivity extends AppCompatActivity {

    private RecyclerView facilityRecyclerView;
    private FirebaseFirestore db;
    private FacilityAdapterAdmin facilityAdapter;
    private List<Facility> facilityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_facilities);

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
