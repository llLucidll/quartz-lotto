// File: com/example/myapplication/LocationsFragment.java
package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * Fragment to display attendee locations on a map.
 */
public class LocationsFragment extends Fragment {

    private static final String TAG = "LocationsFragment";
    private static final String ARG_EVENT_ID = "eventId";

    private String eventId;
    private MapView mapView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static LocationsFragment newInstance(String eventId) {
        LocationsFragment fragment = new LocationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    public LocationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve eventId from arguments
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
            Log.d(TAG, "LocationsFragment created with eventId: " + eventId);
        } else {
            Toast.makeText(getContext(), "Event ID missing.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Event ID is missing from arguments.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locations, container, false);
        mapView = view.findViewById(R.id.map);

        // Initialize the map (assuming you're using OsmDroid or similar)
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
        // Set a default center point (e.g., Edmonton coordinates)
        GeoPoint startPoint = new GeoPoint(53.5461, -113.4938);
        mapView.getController().setCenter(startPoint);

        // Fetch and display attendee locations
        updateMapMarkers();

        return view;
    }

    /**
     * Public method to update map markers. Can be called from other components like Activity or Adapter.
     */
    public void updateMapMarkers() {
        if (eventId == null) {
            Log.e(TAG, "Cannot update map markers. eventId is null.");
            return;
        }

        db.collection("Events").document(eventId)
                .collection("Waitlist")
                .whereEqualTo("status", "waiting") // Ensure only active attendees are fetched
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mapView.getOverlays().clear(); // Clear existing markers

                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    if (documents.isEmpty()) {
                        Log.d(TAG, "No attendees found for eventId: " + eventId);
                        mapView.invalidate(); // Refresh the map
                        return;
                    }

                    for (DocumentSnapshot doc : documents) {
                        Double latitude = doc.getDouble("latitude");
                        Double longitude = doc.getDouble("longitude");
                        String userName = doc.getString("userName");

                        if (latitude != null && longitude != null && userName != null) {
                            GeoPoint point = new GeoPoint(latitude, longitude);
                            Marker marker = new Marker(mapView);
                            marker.setPosition(point);
                            marker.setTitle(userName);
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            mapView.getOverlays().add(marker);
                            Log.d(TAG, "Added marker for user: " + userName + " at (" + latitude + ", " + longitude + ")");
                        }
                    }

                    // Optionally, adjust the map view to encompass all markers
                    adjustMapView(documents);

                    mapView.invalidate(); // Refresh the map
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching locations.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching attendee locations: ", e);
                });
    }

    /**
     * Adjusts the map view to encompass all attendee markers.
     *
     * @param documents The list of attendee documents.
     */
    private void adjustMapView(List<DocumentSnapshot> documents) {
        if (documents.isEmpty()) return;

        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLon = Double.MIN_VALUE;

        for (DocumentSnapshot doc : documents) {
            Double latitude = doc.getDouble("latitude");
            Double longitude = doc.getDouble("longitude");
            if (latitude != null && longitude != null) {
                if (latitude < minLat) minLat = latitude;
                if (latitude > maxLat) maxLat = latitude;
                if (longitude < minLon) minLon = longitude;
                if (longitude > maxLon) maxLon = longitude;
            }
        }

        // Set the map view to the bounding box
        if (minLat < maxLat && minLon < maxLon) {
            BoundingBox boundingBox = new BoundingBox(maxLat, maxLon, minLat, minLon);
            mapView.zoomToBoundingBox(boundingBox, true);
            Log.d(TAG, "Map view adjusted to bounding box.");
        }
    }
}
