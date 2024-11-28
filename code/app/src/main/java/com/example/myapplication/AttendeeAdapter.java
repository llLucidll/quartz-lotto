//package com.example.myapplication;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myapplication.Attendee;
//import com.example.myapplication.AttendingActivity;
//import com.example.myapplication.R;
//
//import java.util.ArrayList;
//import java.util.List;
///*
//Attendee adapter for handling the list of attendees in the attending activity.
// */
//public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.ViewHolder> {
//
//    private List<String> attendeeList;
//    private boolean isWaiting;
//    private boolean notChosen;
//
//    public AttendeeAdapter(List<Attendee> attendeeList, boolean isWaiting, boolean notChosen) {
//        this.attendeeList = attendeeList != null ? attendeeList : new ArrayList<>();
//        this.isWaiting = isWaiting;
//        this.notChosen = notChosen;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Attendee attendee = attendeeList.get(position);
//        holder.name.setText(attendee.getName());
//        holder.status.setText(attendee.getStatus());
//
//        if (isWaiting) {
//            holder.cancelButton.setVisibility(View.VISIBLE);
//            holder.cancelButton.setOnClickListener(v -> {
//                if (holder.itemView.getContext() instanceof AttendingActivity) {
//                    ((AttendingActivity) holder.itemView.getContext()).moveToCancelled(position);
//                    notifyDataSetChanged();
//                }
//            });
//        } else {
//            holder.cancelButton.setVisibility(View.GONE);
//        }
//
//        if (notChosen) {
//            holder.status.setVisibility(View.GONE);
//        } else {
//            holder.status.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return attendeeList.size();
//    }
//
//    /**
//     * Updates the data in the adapter.
//     */
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView name, status;
//        Button cancelButton;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            name = itemView.findViewById(R.id.attendeeName);
//            status = itemView.findViewById(R.id.attendeeStatus);
//            cancelButton = itemView.findViewById(R.id.cancelButton);
//        }
//    }
//}
