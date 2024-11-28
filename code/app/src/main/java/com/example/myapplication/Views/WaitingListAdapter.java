package com.example.myapplication.Views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class WaitingListAdapter extends RecyclerView.Adapter<WaitingListAdapter.WaitingListViewHolder> {

    private final ArrayList<String> waitlist;

    /**
     * Constructor for the WaitingListAdapter.
     *
     * @param waitlist The list of attendee names to display.
     */
    public WaitingListAdapter(ArrayList<String> waitlist) {
        this.waitlist = waitlist != null ? waitlist : new ArrayList<>();
    }

    @NonNull
    @Override
    public WaitingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual items in the RecyclerView
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_waitlist, parent, false);
        return new WaitingListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WaitingListViewHolder holder, int position) {
        // Get the attendee name from the list
        String attendeeName = waitlist.get(position);

        // Handle null or empty names
        if (attendeeName == null || attendeeName.trim().isEmpty()) {
            attendeeName = "Unknown Attendee";
        }

        // Set the attendee name in the TextView
        holder.textViewName.setText(attendeeName);
    }

    @Override
    public int getItemCount() {
        // Return the size of the waitlist
        return waitlist.size();
    }

    /**
     * ViewHolder class for the WaitingListAdapter.
     */
    public static class WaitingListViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        public WaitingListViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find the TextView for attendee names
            textViewName = itemView.findViewById(R.id.textViewName);

            // Ensure the TextView is not null
            if (textViewName == null) {
                throw new NullPointerException("TextView with ID 'textViewName' not found in item_waitlist.xml");
            }
        }
    }
}
