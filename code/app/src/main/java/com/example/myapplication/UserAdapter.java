package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * UserAdapter handles displaying user profiles and managing user-related actions.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final Context context;
    private final List<User> userList;
    private final FirebaseFirestore db;
    private final String currentUserId;

    /**
     * Constructor for UserAdapter.
     *
     * @param context        The context from the calling activity.
     * @param userList       The list of users to display.
     * @param currentUserId  The UID of the current authenticated user.
     */
    public UserAdapter(Context context, List<User> userList, String currentUserId) {
        this.context = context;
        this.userList = userList != null ? userList : new ArrayList<>();
        this.db = FirebaseFirestore.getInstance();
        this.currentUserId = currentUserId; // Initialize the currentUserId
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        // Display user name and UID
        holder.userNameTextView.setText(user.getName() + " (" + user.getUserID() + ")");

        // Handle delete button click
        holder.deleteUserButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition(); // Get the correct position
            if (adapterPosition != RecyclerView.NO_POSITION) { // Ensure position is valid
                String userIdToDelete = userList.get(adapterPosition).getUserID(); // Dynamic UID
                if (userIdToDelete.equals(currentUserId)) {
                    Toast.makeText(context, "You cannot delete your own profile.", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("users").document(userIdToDelete) // Use dynamic userId
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "User deleted successfully.", Toast.LENGTH_SHORT).show();
                            userList.remove(adapterPosition); // Remove user from the list
                            notifyItemRemoved(adapterPosition); // Notify RecyclerView to update
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to delete user. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * ViewHolder class for user items.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        Button deleteUserButton;

        public ViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name_text_view);
            deleteUserButton = itemView.findViewById(R.id.delete_user_button);
        }
    }
}
