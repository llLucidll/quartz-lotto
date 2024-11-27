package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Models.StorageImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<StorageImage> imageList;
    private FirebaseStorage storage;

    public ImageAdapter(Context context, List<StorageImage> imageList) {
        this.context = context;
        this.imageList = imageList;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false); // Updated layout
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        StorageImage image = imageList.get(position);
        holder.imageName.setText(image.getName());

        // Load image using Glide
        Glide.with(context)
                .load(image.getUrl())
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.ic_error_image))
                .into(holder.imageView);

        // Set delete button functionality
        holder.deleteButton.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteImage(image, position))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void deleteImage(StorageImage image, int position) {
        StorageReference imageRef = storage.getReferenceFromUrl(image.getUrl());

        imageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    imageList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Image deleted successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    // ViewHolder class
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView imageName;
        ImageButton deleteButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            imageName = itemView.findViewById(R.id.image_name);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
