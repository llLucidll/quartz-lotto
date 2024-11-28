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
 * Adapter for displaying events in the admin view. Handles the binding of event data to the RecyclerView.
 */
public class EventAdapterAdmin extends RecyclerView.Adapter<EventAdapterAdmin.ViewHolder> {

    private final Context context;
    public final List<Event> eventList;
    private final EventDeleteCallback deleteCallback;

    /**
     * Callback interface for handling event deletion.
     */
    public interface EventDeleteCallback {
        /**
         * Called when an event needs to be deleted.
         *
         * @param event the event to delete
         */
        void onDelete(Event event);
    }

    /**
     * Constructs an EventAdapterAdmin.
     *
     * @param context        the context in which the adapter is used
     * @param eventList      the list of events to display
     * @param deleteCallback the callback for handling event deletion
     */
    public EventAdapterAdmin(Context context, List<Event> eventList, EventDeleteCallback deleteCallback) {
        this.context = context;
        this.eventList = eventList;
        this.deleteCallback = deleteCallback;
    }

    /**
     * Creates a new ViewHolder to represent an event item.
     *
     * @param parent   the parent ViewGroup
     * @param viewType the type of the view
     * @return a new ViewHolder instance
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_admin, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds an event to a ViewHolder.
     *
     * @param holder   the ViewHolder to bind data to
     * @param position the position of the event in the list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventNameTextView.setText(event.getEventName());
        holder.deleteButton.setOnClickListener(v -> deleteCallback.onDelete(event));
    }

    /**
     * Returns the total number of events in the list.
     *
     * @return the number of events
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder class for representing individual event items.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        Button deleteButton;

        /**
         * Constructs a ViewHolder for an event item.
         *
         * @param itemView the view of the item
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_name_text_view);
            deleteButton = itemView.findViewById(R.id.delete_event_button);
        }
    }
}
