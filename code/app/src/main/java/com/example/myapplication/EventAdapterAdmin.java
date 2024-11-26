package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Event;

import java.util.List;

/**
 * Adapter for displaying events in the admin view.
 */
public class EventAdapterAdmin extends RecyclerView.Adapter<EventAdapterAdmin.ViewHolder> {

    private final Context context;
    private final List<Event> eventList;
    private final EventDeleteCallback deleteCallback;

    public interface EventDeleteCallback {
        void onDelete(Event event);
    }

    public EventAdapterAdmin(Context context, List<Event> eventList, EventDeleteCallback deleteCallback) {
        this.context = context;
        this.eventList = eventList;
        this.deleteCallback = deleteCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventNameTextView.setText(event.getEventName());
        holder.deleteButton.setOnClickListener(v -> deleteCallback.onDelete(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_name_text_view);
            deleteButton = itemView.findViewById(R.id.delete_event_button);
        }
    }
}
