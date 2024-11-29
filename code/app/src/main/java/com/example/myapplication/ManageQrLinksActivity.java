package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ManageQrLinksActivity extends AppCompatActivity {

    private static final String TAG = "ManageQrLinksActivity";

    private RecyclerView recyclerView;
    private QRLinkAdapter adapter;
    private FirebaseFirestore db;
    private List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_qr_links);

        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewQrLinks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QRLinkAdapter(eventList, this::deleteQrHash);
        recyclerView.setAdapter(adapter);

        // Load events with QR data
        loadEventsWithQrHash();
    }

    private void loadEventsWithQrHash() {
        db.collection("Events")
                .whereNotEqualTo("qrCodeLink", null)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        if (event != null) {
                            event.setEventId(document.getId());
                            eventList.add(event);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load events.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading events: ", e);
                });
    }

    private void deleteQrHash(String eventId) {
        db.collection("Events").document(eventId)
                .update("qrCodeLink", null)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "QR Hash deleted successfully.", Toast.LENGTH_SHORT).show();
                    loadEventsWithQrHash();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete QR Hash.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error deleting QR Hash: ", e);
                });
    }
}
