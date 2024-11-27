package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private final List<String> eventNames;
    private final List<String> eventIds;
    private final Context context;

    public EventAdapter(Context context, List<String> eventNames, List<String> eventIds) {
        this.context = context;
        this.eventNames = eventNames;
        this.eventIds = eventIds;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.eventNameTextView.setText(eventNames.get(position));

        holder.itemView.setOnClickListener(v -> {
            String selectedEventId = eventIds.get(position);
            Intent intent = new Intent(context, WaitinglistActivity.class);
            intent.putExtra("event_id", selectedEventId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventNames.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
