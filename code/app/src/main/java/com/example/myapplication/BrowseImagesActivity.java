package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.StorageImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class BrowseImagesActivity extends AppCompatActivity {

    private RecyclerView imagesRecyclerView;
    private ImageAdapter imageAdapter;
    private List<StorageImage> imageList;
    private FirebaseStorage storage;
    private ProgressBar progressBar;

    // Define the folders you want to list images from
    private final String[] folders = {"profile_images", "facility_images", "posters"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_images);

        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Browse Images");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize RecyclerView and other UI components
        imagesRecyclerView = findViewById(R.id.images_recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        // Set GridLayoutManager with desired number of columns
        int numberOfColumns = 2; // You can adjust this number as needed
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        imagesRecyclerView.setLayoutManager(gridLayoutManager);

        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageList);
        imagesRecyclerView.setAdapter(imageAdapter);

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();

        // Fetch images from Firebase Storage
        fetchAllImages();
    }

    /**
     * Handles back button press in the toolbar
     * @param item The menu item selected
     * @return True if the action was handled, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fetches all images from the specified folders in Firebase Storage
     * Displays a progress bar while loading
     */
    private void fetchAllImages(){
        progressBar.setVisibility(View.VISIBLE);

        // Use a counter to keep track of asynchronous operations
        final int totalFolders = folders.length;
        final int[] completedFolders = {0};

        for(String folder : folders){
            StorageReference folderRef = storage.getReference().child(folder);
            folderRef.listAll()
                    .addOnSuccessListener(listResult -> {
                        for(StorageReference item : listResult.getItems()){
                            item.getDownloadUrl().addOnSuccessListener(uri -> {
                                StorageImage image = new StorageImage(
                                        item.getName(),
                                        item.getPath(),
                                        uri.toString()
                                );
                                imageList.add(image);
                                imageAdapter.notifyItemInserted(imageList.size() - 1);
                            }).addOnFailureListener(e -> {
                                Toast.makeText(BrowseImagesActivity.this, "Failed to get URL for " + item.getName(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(BrowseImagesActivity.this, "Failed to list folder: " + folder, Toast.LENGTH_SHORT).show();
                    })
                    .addOnCompleteListener(task -> {
                        completedFolders[0]++;
                        if(completedFolders[0] == totalFolders){
                            progressBar.setVisibility(View.GONE);
                            if(imageList.isEmpty()){
                                Toast.makeText(this, "No images found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
