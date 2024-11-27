package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.*;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment displaying the locations of event attendees.
 */
public class LocationsFragment extends Fragment {
    private static final String ARG_EVENT_ID = "eventId";
    private static final String TAG = "LocationsFragment";

    private String eventId;
    private MapView mapView;
    private FirebaseFirestore db;
    private List<GeoPoint> attendeeLocations = new ArrayList<>();

    public LocationsFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param eventId The unique ID of the event.
     * @return A new instance of fragment LocationsFragment.
     */
    public static LocationsFragment newInstance(String eventId) {
        LocationsFragment fragment = new LocationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve eventId from arguments
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
            Log.d(TAG, "Received eventId in LocationsFragment: " + eventId);
        } else {
            Toast.makeText(getContext(), "Event ID missing.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "getArguments() returned null in LocationsFragment");
            // Optionally, handle the error
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Configure Osmdroid
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locations, container, false);

        // Configure the MapView
        mapView = view.findViewById(R.id.map);
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Fetch attendee locations
        fetchAttendeeLocations();

        return view;
    }

    /**
     * Fetches attendee locations from Firestore and adds markers to the map.
     */
    private void fetchAttendeeLocations() {
        if (eventId == null) {
            Toast.makeText(getContext(), "Event ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference attendeesRef = db.collection("Events")
                .document(eventId)
                .collection("Attendees");

        attendeesRef.get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                mapView.getOverlays().clear(); // Clear existing markers
                attendeeLocations.clear();

                for (QueryDocumentSnapshot document : querySnapshot) {
                    Double latitude = document.getDouble("latitude");
                    Double longitude = document.getDouble("longitude");
                    String userName = document.getString("userName");

                    if (latitude != null && longitude != null) {
                        addAttendeeMarker(latitude, longitude, userName);
                    } else {
                        Log.w(TAG, "Attendee " + userName + " has no location data.");
                    }
                }
                // Adjust the map view to include all markers
                centerMap();
                mapView.invalidate(); // Refresh the map
            } else {
                Toast.makeText(getContext(), "No attendees with location data.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to fetch attendee locations.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error fetching attendees", e);
        });
    }

    /**
     * Adds a marker for an attendee on the map.
     *
     * @param latitude  Latitude of the attendee.
     * @param longitude Longitude of the attendee.
     * @param userName  Name of the attendee.
     */
    private void addAttendeeMarker(double latitude, double longitude, String userName) {
        GeoPoint point = new GeoPoint(latitude, longitude);
        attendeeLocations.add(point);

        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle(userName); // Display user's name when marker is tapped
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);
    }

    /**
     * Centers the map to include all attendee markers.
     */
    private void centerMap() {
        if (attendeeLocations.isEmpty()) return;

        // Create a bounding box around the attendee locations
        BoundingBox boundingBox = BoundingBox.fromGeoPoints(attendeeLocations);
        mapView.zoomToBoundingBox(boundingBox, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume(); // This is required to resume map rendering
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause(); // This is required to pause map rendering
    }
}
