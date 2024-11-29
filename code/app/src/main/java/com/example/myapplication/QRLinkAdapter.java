package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Event;

import java.util.List;

public class QRLinkAdapter extends RecyclerView.Adapter<QRLinkAdapter.ViewHolder> {

    private final List<Event> eventList;
    private final OnDeleteQrHashListener deleteQrHashListener;

    public QRLinkAdapter(List<Event> eventList, OnDeleteQrHashListener deleteQrHashListener) {
        this.eventList = eventList;
        this.deleteQrHashListener = deleteQrHashListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qr_link, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventNameTextView.setText(event.getEventName());
        holder.qrLinkTextView.setText(event.getQrCodeLink());

        holder.deleteButton.setOnClickListener(v -> deleteQrHashListener.onDeleteQrHash(event.getEventId()));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public interface OnDeleteQrHashListener {
        void onDeleteQrHash(String eventId);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView qrLinkTextView;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.textViewEventName);
            qrLinkTextView = itemView.findViewById(R.id.textViewQrLink);
            deleteButton = itemView.findViewById(R.id.buttonDeleteQrLink);
        }
    }
}
