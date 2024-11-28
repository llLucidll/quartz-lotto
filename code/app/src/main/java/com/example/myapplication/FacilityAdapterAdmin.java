package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Facility;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Adapter for displaying a list of facilities in the admin view.
 * Provides functionality to delete facilities directly from Firestore.
 */
public class FacilityAdapterAdmin extends RecyclerView.Adapter<FacilityAdapterAdmin.ViewHolder> {

    private final Context context;
    private final List<Facility> facilityList;
    private final FirebaseFirestore db;

    /**
     * Constructs a FacilityAdapterAdmin.
     *
     * @param context      The context in which the adapter is used.
     * @param facilityList The list of facilities to display.
     */
    public FacilityAdapterAdmin(Context context, List<Facility> facilityList) {
        this.context = context;
        this.facilityList = facilityList;
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Creates a new ViewHolder for a facility item.
     *
     * @param parent   The parent view group.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder for the facility item.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_facility_admin, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds a facility object to the ViewHolder.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the facility in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Facility facility = facilityList.get(position);

        // Set facility details
        holder.nameTextView.setText(facility.getName());
        holder.locationTextView.setText(facility.getLocation());

        // Load facility image using Glide
        if (facility.getImageUrl() != null && !facility.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(facility.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_error_image)
                    .into(holder.facilityImageView);
        } else {
            holder.facilityImageView.setImageResource(R.drawable.ic_placeholder_image);
        }

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            db.collection("Facilities").document(facility.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Facility deleted successfully.", Toast.LENGTH_SHORT).show();
                        facilityList.remove(position); // Remove the deleted item from the list
                        notifyDataSetChanged(); // Notify the adapter of the change
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to delete facility. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    /**
     * Returns the total number of facilities in the list.
     *
     * @return The number of facilities.
     */
    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    /**
     * ViewHolder class for representing a single facility item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, locationTextView;
        ImageView facilityImageView;
        Button deleteButton;

        /**
         * Constructs a ViewHolder for a facility item.
         *
         * @param itemView The view of the item.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.facility_name_text_view);
            locationTextView = itemView.findViewById(R.id.facility_location_text_view);
            facilityImageView = itemView.findViewById(R.id.facility_image_view);
            deleteButton = itemView.findViewById(R.id.delete_facility_button);
        }
    }

    /**
     * Gets the list of facilities.
     *
     * @return The list of facilities.
     */
    public List<Facility> getFacilityList() {
        return facilityList;
    }
}
