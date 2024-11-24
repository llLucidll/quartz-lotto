//package com.example.myapplication;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.ArrayList;
//import java.util.List;
//public class ManageFacilitiesActivity extends AppCompatActivity {
//
//    private static final String TAG = "ManageFacilitiesActivity";
//    private static final String FACILITY_COLLECTION = "Facilities";
//
//    private RecyclerView facilitiesRecyclerView;
//    private FacilitiesAdapter facilitiesAdapter;
//    private List<Facility> facilitiesList = new ArrayList<>();
//    private Button addFacilityButton;
//
//    private FirebaseFirestore db;
//    private FirebaseAuth auth;
//    private String userId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_manage_facilities);
//
//
//        db = FirebaseFirestore.getInstance();
//        auth = FirebaseAuth.getInstance();
//        performAnonymousSignIn();
//
//
//        facilitiesRecyclerView = findViewById(R.id.facilities_recycler_view);
//        addFacilityButton = findViewById(R.id.add_facility_button);
//
//        facilitiesAdapter = new FacilitiesAdapter(this, facilitiesList);
//        facilitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        facilitiesRecyclerView.setAdapter(facilitiesAdapter);
//
//
//        addFacilityButton.setOnClickListener(v -> {
//            Intent intent = new Intent(ManageFacilitiesActivity.this, AddFacilityActivity.class);
//            startActivity(intent);
//        });
//    }
//
//    private void performAnonymousSignIn() {
//        FirebaseUser currentUser = auth.getCurrentUser();
//        if (currentUser == null) {
//
//            auth.signInAnonymously()
//                    .addOnCompleteListener(this, task -> {
//                        if (task.isSuccessful()) {
//                            FirebaseUser user = auth.getCurrentUser();
//                            if (user != null) {
//                                userId = user.getUid();
//                                Log.d(TAG, "Anonymous sign-in successful. User ID: " + userId);
//                                loadFacilities();
//                            }
//                        } else {
//                            Log.w(TAG, "Anonymous sign-in failed.", task.getException());
//                            Toast.makeText(ManageFacilitiesActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    });
//        } else {
//
//            userId = currentUser.getUid();
//            Log.d(TAG, "User already signed in. User ID: " + userId);
//            loadFacilities();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if (userId != null) {
//            loadFacilities();
//        }
//    }
//
//    private void loadFacilities() {
//        if (userId == null) {
//            Log.e(TAG, "User ID is null. Cannot load facilities.");
//            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        CollectionReference facilitiesRef = db.collection(FACILITY_COLLECTION);
//        facilitiesRef.whereEqualTo("organizerId", userId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        facilitiesList.clear();
//                        QuerySnapshot querySnapshot = task.getResult();
//                        if (querySnapshot != null) {
//                            for (QueryDocumentSnapshot document : querySnapshot) {
//                                Facility facility = document.toObject(Facility.class);
//                                facility.setId(document.getId());
//                                facilitiesList.add(facility);
//                            }
//                            facilitiesAdapter.notifyDataSetChanged();
//                            Log.d(TAG, "Loaded " + facilitiesList.size() + " facilities.");
//                        }
//                    } else {
//                        Toast.makeText(ManageFacilitiesActivity.this, "Error getting facilities.", Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "Error getting facilities: ", task.getException());
//                    }
//                });
//    }
//
//    public void deleteFacility(Facility facility) {
//        if (facility == null) {
//            Toast.makeText(this, "Error: Facility not found", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String facilityId = facility.getId();
//        String imageUrl = (facility.getImageUrls() != null && !facility.getImageUrls().isEmpty()) ? facility.getImageUrls().get(0) : null;
//
//        // delete the facility document from Firestore
//        db.collection(FACILITY_COLLECTION).document(facilityId)
//                .delete()
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "Facility deleted successfully", Toast.LENGTH_SHORT).show();
//
//                    //if there is an image URL, delete it from Storage as well
//                    if (imageUrl != null) {
//                        deleteImageFromStorage(imageUrl);
//                    }
//
//                    //remove from local list and notify adapter
//                    facilitiesList.remove(facility);
//                    facilitiesAdapter.notifyDataSetChanged();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Error deleting facility", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "Error deleting facility: ", e);
//                });
//    }
//
//    private void deleteImageFromStorage(String imageUrl) {
//        if (imageUrl == null || imageUrl.isEmpty()) {
//            Log.w(TAG, "Image URL is null or empty. Skipping deletion from Storage.");
//            return;
//        }
//
//        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
//        imageRef.delete()
//                .addOnSuccessListener(aVoid -> {
//                    Log.d(TAG, "Image deleted from Storage successfully.");
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Error deleting image from Storage", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "Error deleting image from Storage: ", e);
//                });
//    }
//
//    public void confirmDeleteFacility(Facility facility) {
//        new AlertDialog.Builder(this)
//                .setTitle("Delete Facility")
//                .setMessage("Are you sure you want to delete this facility and its image?")
//                .setPositiveButton("Yes", (dialog, which) -> deleteFacility(facility))
//                .setNegativeButton("No", null)
//                .show();
//    }
//}
