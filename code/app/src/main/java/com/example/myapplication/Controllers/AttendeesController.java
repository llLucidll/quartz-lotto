// // Tried to make this into MVC, giving up for now - may come back to it later, otherwise will delete
//package com.example.myapplication.Controllers;
//
//import android.util.Log;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myapplication.EventDetailsActivity;
//import com.example.myapplication.Views.AttendeesView;
//import com.example.myapplication.Models.Attendee;
//import com.example.myapplication.Repositories.AttendeesRepository;
//import com.example.myapplication.AttendeesAdapter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class AttendeesController {
//    private AttendeesRepository repository;
//    private AttendeesView attendeesView;
//    List<Attendee> selectedList = new ArrayList<>();
//    List<Attendee> confirmedList = new ArrayList<>();
//    List<Attendee> cancelledList = new ArrayList<>();
//    public AttendeesController() {
//        this.repository = new AttendeesRepository();
//    }
//
//    public void fetchAttendees(String eventId, AttendeesAdapter selectedAdapter, AttendeesAdapter confirmedAdapter, AttendeesAdapter cancelledAdapter, AttendeesRepository.AttendeeCallback callback) {
//        repository.getAttendees(eventId, new AttendeesRepository.AttendeeCallback() {
//            @Override
//            public void onCallback(List<Attendee> attendees) {
//                selectedList.clear();
//                confirmedList.clear();
//                cancelledList.clear();
//                for (Attendee attendee : attendees) {
//                    String status = attendee.getStatus();
//                    if ("selected".equalsIgnoreCase(status)) {
//                        selectedList.add(attendee);
//                    } else if ("confirmed".equalsIgnoreCase(status)) {
//                        confirmedList.add(attendee);
//                    } else if ("cancelled".equalsIgnoreCase(status)) {
//                        cancelledList.add(attendee);
//                    }
//                }
//            }
//        });
//        selectedAdapter.setAttendees(selectedList);
//        confirmedAdapter.setAttendees(confirmedList);
//        cancelledAdapter.setAttendees(cancelledList);
//        selectedAdapter.notifyDataSetChanged();
//        confirmedAdapter.notifyDataSetChanged();
//        cancelledAdapter.notifyDataSetChanged();
//    }
//}
