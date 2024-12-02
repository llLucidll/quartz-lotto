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

/**
 * Adapter for displaying and managing a list of images in a RecyclerView.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final Context context;
    private final List<StorageImage> imageList;
    private final FirebaseStorage storage;

    /**
     * Constructs an ImageAdapter.
     *
     * @param context   The context of the calling activity or fragment.
     * @param imageList The list of images to display.
     */
    public ImageAdapter(Context context, List<StorageImage> imageList) {
        this.context = context;
        this.imageList = imageList;
        this.storage = FirebaseStorage.getInstance();
    }

    /**
     * Creates a new ImageViewHolder.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false); // Updated layout
        return new ImageViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */

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

    /**
     * Deletes an image from Firebase Storage and updates the RecyclerView.
     *
     * @param image    The image to delete.
     * @param position The position of the image in the list.
     */
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

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return
     */

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    /**
     * Returns the list of images managed by the adapter.
     *
     * @return The list of images.
     */
    public List<StorageImage> getImageList() {
        return imageList;
    }

    /**
     * ViewHolder class for managing the views of a single image item.
     */
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView imageName;
        ImageButton deleteButton;

        /**
         * Constructs an ImageViewHolder.
         *
         * @param itemView The view of the image item.
         */
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            imageName = itemView.findViewById(R.id.image_name);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
