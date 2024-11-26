package com.example.myapplication;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Attendee;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AttendeesAdapter extends RecyclerView.Adapter<AttendeesAdapter.AttendeeViewHolder> {

    private final List<Attendee> attendees;
    private final FirebaseFirestore db;
    private final String eventId;
    private final Context context;

    public AttendeesAdapter(List<Attendee> attendees, FirebaseFirestore db, String eventId, Context context) {
        this.attendees = attendees;
        this.db = db;
        this.eventId = eventId;
        this.context = context;
    }

    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee, parent, false);
        return new AttendeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
        Attendee attendee = attendees.get(position);

        holder.nameTextView.setText(attendee.getName());
        holder.statusTextView.setText(attendee.getStatus()); // Display the status directly

        // Handle delete functionality
        holder.deleteButton.setOnClickListener(v -> confirmDeletion(attendee, position));
    }

    @Override
    public int getItemCount() {
        return attendees.size();
    }

    private void confirmDeletion(Attendee attendee, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Attendee")
                .setMessage("Are you sure you want to delete this attendee?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.collection("Events")
                            .document(eventId)
                            .collection("Attendees")
                            .document(attendee.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                attendees.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Attendee deleted.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete attendee.", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("No", null)
                .show();
    }

    public static class AttendeeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, statusTextView;
        Button deleteButton;

        public AttendeeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.attendeeName);
            statusTextView = itemView.findViewById(R.id.attendeeStatus);
            deleteButton = itemView.findViewById(R.id.cancelButton);
        }
    }
}
