//
//package com.example.myapplication;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//
//import java.util.List;public class FacilitiesAdapter extends RecyclerView.Adapter<FacilitiesAdapter.FacilityViewHolder> {
//
//    private final Context context;
//    private final List<Facility> facilities;
//
//    public FacilitiesAdapter(Context context, List<Facility> facilities) {
//        this.context = context;
//        this.facilities = facilities;
//    }
//
//    @NonNull
//    @Override
//    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_facility, parent, false);
//        return new FacilityViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
//        Facility facility = facilities.get(position);
//        holder.nameTextView.setText(facility.getName());
//        holder.locationTextView.setText(facility.getLocation());
//
//        if (facility.getImageUrls() != null && !facility.getImageUrls().isEmpty()) {
//            String imageUrl = facility.getImageUrls().get(0);
//            if (imageUrl != null) {
//                Glide.with(context)
//                        .load(imageUrl)
//                        .placeholder(R.drawable.ic_placeholder_image)
//                        .error(R.drawable.ic_error_image)
//                        .into(holder.facilityImageView);
//            } else {
//
//                holder.facilityImageView.setImageResource(R.drawable.ic_placeholder_image);
//            }
//        } else {
//            holder.facilityImageView.setImageResource(R.drawable.ic_placeholder_image);
//        }
//
//        holder.editButton.setOnClickListener(v -> {
//            Intent intent = new Intent(context, AddFacilityActivity.class);
//            intent.putExtra("facilityId", facility.getId());
//            context.startActivity(intent);
//        });
//
//
//        holder.deleteButton.setOnClickListener(v -> {
//            if (context instanceof ManageFacilitiesActivity) {
//                ((ManageFacilitiesActivity) context).confirmDeleteFacility(facility);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return facilities.size();
//    }
//
//    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
//        TextView nameTextView, locationTextView;
//        ImageView facilityImageView;
//        ImageButton editButton, deleteButton;
//
//        public FacilityViewHolder(@NonNull View itemView) {
//            super(itemView);
//            nameTextView = itemView.findViewById(R.id.facility_name);
//            locationTextView = itemView.findViewById(R.id.facility_location);
//            facilityImageView = itemView.findViewById(R.id.facilityImageView);
//            editButton = itemView.findViewById(R.id.edit_facility_button);
//            deleteButton = itemView.findViewById(R.id.delete_facility_button);
//        }
//    }
//}
