// File: com/example/myapplication/AttendeesAdapter.java
package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Attendee;
import com.google.firebase.firestore.*;

import java.util.List;

/**
 * RecyclerView Adapter to display attendees.
 */
public class AttendeesAdapter extends RecyclerView.Adapter<AttendeesAdapter.AttendeeViewHolder> {

    private List<Attendee> attendeeList;
    private Context context;
    private String status; //"waiting", "selected", "confirmed" or "cancelled"
    private String eventId;   // Dynamic eventId
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AttendeesAdapter(List<Attendee> attendeeList, Context context, String status, String eventId) {
        this.attendeeList = attendeeList;
        this.context = context;
        this.status = status;
        this.eventId = eventId;
    }

    /**
     * Sets the userType and updates the adapter.
     *
     * @param status The status of the user for the event ("waiting", "selected", "confirmed" or "cancelled").
     */
    public void setStatus(String status) {
        this.status = status;
    }

    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendee, parent, false);
        return new AttendeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
        Attendee attendee = attendeeList.get(position);

        holder.nameTextView.setText(attendee.getUserName());
        holder.emailTextView.setText(attendee.getUserEmail());
        holder.statusTextView.setText(attendee.getStatus());


        // Handle Cancel button visibility and functionality
        if ("selected".equalsIgnoreCase(status)) {
            holder.cancelButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setOnClickListener(v -> {
                // Confirm cancellation with the admin
                new AlertDialog.Builder(context)
                        .setTitle("Cancel Attendee")
                        .setMessage("Are you sure you want to cancel this attendee?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            cancelAttendee(attendee, holder.getAdapterPosition());
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        } else {
            holder.cancelButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return attendeeList.size();
    }

    /**
     * ViewHolder class for AttendeesAdapter.
     */
    public static class AttendeeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView, statusTextView;
        ImageView profileImageView;
        Button cancelButton;

        public AttendeeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_attendee_name);
            emailTextView = itemView.findViewById(R.id.text_attendee_email);
            statusTextView = itemView.findViewById(R.id.text_attendee_status);
            profileImageView = itemView.findViewById(R.id.image_attendee_profile);
            cancelButton = itemView.findViewById(R.id.button_cancel_attendee);
        }
    }

    /**
     * Cancels an attendee by deleting their document from Attendees collection,
     * updating their status in Users collection, and decrementing currentAttendees.
     *
     * @param attendee The attendee to cancel.
     * @param position The position of the attendee in the list.
     */
    private void cancelAttendee(Attendee attendee, int position) {
        if (eventId == null) {
            Toast.makeText(context, "Event ID is missing.", Toast.LENGTH_SHORT).show();
            Log.e("AttendeesAdapter", "Event ID is null. Cannot cancel attendee.");
            return;
        }

        String attendeeId = attendee.getUserId();
        if (attendeeId == null) {
            Toast.makeText(context, "Attendee ID is missing.", Toast.LENGTH_SHORT).show();
            Log.e("AttendeesAdapter", "Attendee ID is null. Cannot cancel attendee.");
            return;
        }

        DocumentReference eventRef = db.collection("Events").document(eventId);
        DocumentReference attendeeRef = eventRef.collection("Waitlist").document(attendeeId);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            // Get currentAttendees
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            if (!eventSnapshot.exists()) {
                throw new FirebaseFirestoreException("Event does not exist.",
                        FirebaseFirestoreException.Code.NOT_FOUND);
            }

            Long currentAttendeesLong = eventSnapshot.getLong("currentAttendees");
            if (currentAttendeesLong == null || currentAttendeesLong <= 0) {
                throw new FirebaseFirestoreException("currentAttendees is invalid.",
                        FirebaseFirestoreException.Code.DATA_LOSS);
            }

            // Delete the specific attendee document
            transaction.update(attendeeRef, "status", "cancelled");

            // Decrement currentAttendees
            transaction.update(eventRef, "currentAttendees", currentAttendeesLong - 1);


            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(context, "Attendee canceled successfully.", Toast.LENGTH_SHORT).show();
            Log.d("AttendeesAdapter", "Attendee " + attendeeId + " canceled successfully.");
            attendee.setStatus("cancelled");

            attendeeList.remove(position);
            notifyItemRemoved(position);

            if (context instanceof EventDetailsActivity) {
                ((EventDetailsActivity) context).refreshAttendees();
            }

        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to cancel attendee: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("AttendeesAdapter", "Failed to cancel attendee " + attendeeId, e);
        });
    }
}