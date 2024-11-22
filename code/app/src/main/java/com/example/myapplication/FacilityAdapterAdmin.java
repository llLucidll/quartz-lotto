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

public class FacilityAdapterAdmin extends RecyclerView.Adapter<FacilityAdapterAdmin.ViewHolder> {

    private final Context context;
    private final List<Facility> facilityList;
    private final FirebaseFirestore db;

    public FacilityAdapterAdmin(Context context, List<Facility> facilityList) {
        this.context = context;
        this.facilityList = facilityList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_facility_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Facility facility = facilityList.get(position);

        // Set facility details
        holder.nameTextView.setText(facility.getName());
        holder.locationTextView.setText(facility.getLocation());

        // Load facility image
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
                        facilityList.remove(position); // Remove item locally
                        notifyDataSetChanged(); // Refresh the entire list to prevent index issues
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to delete facility. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }


    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, locationTextView;
        ImageView facilityImageView;
        Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.facility_name_text_view);
            locationTextView = itemView.findViewById(R.id.facility_location_text_view);
            facilityImageView = itemView.findViewById(R.id.facility_image_view);
            deleteButton = itemView.findViewById(R.id.delete_facility_button);
        }
    }
}
