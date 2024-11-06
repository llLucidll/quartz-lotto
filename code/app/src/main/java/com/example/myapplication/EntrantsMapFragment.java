// EntrantsMapFragment.java
package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.firestore.*;

import java.util.List;
import java.util.Map;

public class EntrantsMapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "EntrantsMapFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrants_map, container, false);

        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Optional: Customize the map
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Fetch entrants' locations
        fetchEntrantsLocations();
    }

    private void fetchEntrantsLocations() {
        String eventId = "EVENT12345"; // Replace with dynamic event ID if applicable

        db.collection("entrants")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc : documents) {
                        Map<String, Object> location = (Map<String, Object>) doc.get("location");
                        if (location != null) {
                            double latitude = (double) location.get("latitude");
                            double longitude = (double) location.get("longitude");

                            LatLng entrantLatLng = new LatLng(latitude, longitude);
                            googleMap.addMarker(new MarkerOptions()
                                    .position(entrantLatLng)
                                    .title("Entrant ID: " + doc.getString("entrantId")));
                        }
                    }

                    // Optionally move the camera to the first entrant's location
                    if (!documents.isEmpty()) {
                        Map<String, Object> firstLocation = (Map<String, Object>) documents.get(0).get("location");
                        if (firstLocation != null) {
                            double lat = (double) firstLocation.get("latitude");
                            double lon = (double) firstLocation.get("longitude");
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 10));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching entrants' locations", e);
                });
    }

    // MapView lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
