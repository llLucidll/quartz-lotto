package com.example.myapplication.Views;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Facility;
import com.example.myapplication.Controllers.BrowseFacilitiesController;
import com.example.myapplication.FacilityAdapterAdmin;

import java.util.ArrayList;
import java.util.List;

/**
 * View for displaying a list of facilities in a RecyclerView.
 * Manages UI components and interactions.
 */
public class BrowseFacilitiesView {

    private final Context context;
    private final RecyclerView facilityRecyclerView;
    private final FacilityAdapterAdmin facilityAdapter;
    private final BrowseFacilitiesController controller;

    /**
     * Constructs a BrowseFacilitiesView.
     *
     * @param context  The context of the activity.
     * @param recyclerView The RecyclerView to display facilities.
     * @param controller The controller for fetching and managing facilities.
     */
    public BrowseFacilitiesView(Context context, RecyclerView recyclerView, BrowseFacilitiesController controller) {
        this.context = context;
        this.facilityRecyclerView = recyclerView;
        this.controller = controller;

        facilityAdapter = new FacilityAdapterAdmin(context, new ArrayList<>());
        facilityRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        facilityRecyclerView.setAdapter(facilityAdapter);
    }

    /**
     * Configures the toolbar for back navigation.
     *
     * @param toolbar The toolbar to set up.
     * @param onBackPressed The action to perform when the back button is pressed.
     */
    public void setToolbar(Toolbar toolbar, Runnable onBackPressed) {
        toolbar.setNavigationOnClickListener(v -> onBackPressed.run());
    }

    /**
     * Fetches facilities from Firestore and displays them in the RecyclerView.
     */
    public void loadFacilities() {
        controller.fetchFacilities(facilities -> {
            facilityAdapter.getFacilityList().clear();
            facilityAdapter.getFacilityList().addAll(facilities);
            facilityAdapter.notifyDataSetChanged();
        }, errorMessage -> Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show());
    }
}
