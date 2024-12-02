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
 * UserAdapter handles displaying user profiles and managing user-related actions. (ADMIN USE)
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
        this.currentUserId = currentUserId;
    }

    /**
     * Creates a new ViewHolder for the user list item.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the data for a user item to the ViewHolder.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        // Display user name and UID
        holder.userNameTextView.setText(user.getName() + " (" + user.getUserID() + ")");

        // Handle delete button click
        holder.deleteUserButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                String userIdToDelete = userList.get(adapterPosition).getUserID();
                if (userIdToDelete.equals(currentUserId)) {
                    Toast.makeText(context, "You cannot delete your own profile.", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("users").document(userIdToDelete)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "User deleted successfully.", Toast.LENGTH_SHORT).show();
                            userList.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to delete user. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    /**
     * Returns the total number of users in the list.
     *
     * @return The total number of users.
     */

    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * Returns the list of users managed by the adapter.
     *
     * @return The list of users.
     */
    public List<User> getUserList() {
        return userList;
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
