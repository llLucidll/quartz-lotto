package com.example.myapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * Fragment displaying the details of an event.
 */
public class DetailsFragment extends Fragment {
    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;
    private FirebaseFirestore db;
    private TextView eventNameTextView, dateTextView, timeTextView, descriptionTextView, maxAttendeesTextView, maxWaitlistTextView, geolocationTextView;
    private ImageView posterImageView, qrCodeImageView;
    private ProgressBar progressBar;

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param eventId The unique ID of the event.
     * @return A new instance of fragment DetailsFragment.
     */
    public static DetailsFragment newInstance(String eventId) {
        DetailsFragment fragment = new DetailsFragment();
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
        } else {
            Toast.makeText(getContext(), "Event ID missing.", Toast.LENGTH_SHORT).show();
            // Optionally, navigate back or show an error
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        // Initialize views
        eventNameTextView = view.findViewById(R.id.detailsEventNameTextView);
        dateTextView = view.findViewById(R.id.detailsDateTextView);
        timeTextView = view.findViewById(R.id.detailsTimeTextView);
        descriptionTextView = view.findViewById(R.id.detailsDescriptionTextView);
        maxAttendeesTextView = view.findViewById(R.id.detailsMaxAttendeesTextView);
        maxWaitlistTextView = view.findViewById(R.id.detailsMaxWaitlistTextView);
        geolocationTextView = view.findViewById(R.id.detailsGeolocationTextView);
        posterImageView = view.findViewById(R.id.detailsPosterImageView);
        qrCodeImageView = view.findViewById(R.id.detailsQrCodeImageView);
        progressBar = view.findViewById(R.id.detailsProgressBar);

        // Fetch and display event details
        fetchEventDetails();

        return view;
    }

    /**
     * Fetches event details from Firestore and displays them.
     */
    private void fetchEventDetails() {
        if (eventId == null) {
            Toast.makeText(getContext(), "Event ID missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        db.collection("Events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String eventName = documentSnapshot.getString("eventName");
                        String drawDate = documentSnapshot.getString("drawDate");
                        String eventDateTime = documentSnapshot.getString("eventDateTime");
                        String description = documentSnapshot.getString("description");
                        String posterUrl = documentSnapshot.getString("posterUrl");
                        Long maxAttendeesLong = documentSnapshot.getLong("maxAttendees");
                        Long currentAttendeesLong = documentSnapshot.getLong("currentAttendees");
                        Long maxWaitlistLong = documentSnapshot.getLong("maxWaitlist");
                        Boolean geolocationEnabled = documentSnapshot.getBoolean("geolocationEnabled");
                        String qrCodeLink = documentSnapshot.getString("qrCodeLink");

                        int maxAttendees = maxAttendeesLong != null ? maxAttendeesLong.intValue() : 0;
                        String maxWaitlistStr = (maxWaitlistLong != null) ? String.valueOf(maxWaitlistLong.intValue()) : "N/A";
                        String geolocationStr = (geolocationEnabled != null && geolocationEnabled) ? "Enabled" : "Disabled";

                        // Update UI
                        eventNameTextView.setText(eventName);
                        dateTextView.setText("Date: " + drawDate);
                        timeTextView.setText("Time: " + eventDateTime);
                        descriptionTextView.setText(description);
                        maxAttendeesTextView.setText("Max Attendees: " + maxAttendees);
                        maxWaitlistTextView.setText("Max Waitlist: " + maxWaitlistStr);
                        geolocationTextView.setText("Geolocation: " + geolocationStr);

                        if (posterUrl != null && !posterUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(posterUrl)
                                    .placeholder(R.drawable.ic_placeholder_image)
                                    .into(posterImageView);
                        } else {
                            posterImageView.setImageResource(R.drawable.ic_placeholder_image);
                        }

                        if (qrCodeLink != null && !qrCodeLink.isEmpty()) {
                            generateQRCode(qrCodeLink);
                        } else {
                            qrCodeImageView.setVisibility(View.GONE);
                        }

                    } else {
                        Toast.makeText(getContext(), "Event not found.", Toast.LENGTH_SHORT).show();
                        // Optionally, navigate back or show an error
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching event details.", Toast.LENGTH_SHORT).show();
                    Log.e("DetailsFragment", "Error fetching event", e);
                })
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                });
    }

    /**
     * Generates a QR code for the event and displays it in the ImageView.
     *
     * @param qrCodeLink The link to encode in the QR code.
     */
    private void generateQRCode(String qrCodeLink) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrCodeLink, BarcodeFormat.QR_CODE, 300, 300);
            qrCodeImageView.setImageBitmap(bitmap);
            qrCodeImageView.setVisibility(View.VISIBLE); // Make the QR code visible
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error generating QR code", Toast.LENGTH_SHORT).show();
            qrCodeImageView.setVisibility(View.GONE);
        }
    }
}
