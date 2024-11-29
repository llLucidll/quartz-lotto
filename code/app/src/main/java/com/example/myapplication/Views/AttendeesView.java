// // Tried to make this into MVC, giving up for now - may come back to it later, otherwise will delete
//package com.example.myapplication.Views;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myapplication.AttendeesAdapter;
//import com.example.myapplication.Controllers.AttendeesController;
//import com.example.myapplication.Models.Attendee;
//import com.example.myapplication.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class AttendeesView extends Fragment {
//    private static final String TAG = "AttendeesFragment";
//    private static final String ARG_EVENT_ID = "eventId";
//
//    private RecyclerView recyclerViewSelected, recyclerViewConfirmed, recyclerViewCancelled;
//    private AttendeesAdapter selectedAdapter, confirmedAdapter, cancelledAdapter;
//    private TextView noneConfirmed, noneSelected, noneCancelled;
//    private List<Attendee> selectedList = new ArrayList<>();
//    private String selected = "selected";
//    private List<Attendee> confirmedList = new ArrayList<>();
//    private String confirmed = "confirmed";
//    private List<Attendee> cancelledList = new ArrayList<>();
//    private String cancelled = "cancelled";
//
//    private String eventId;
//    private AttendeesController controller;
//
//    public static com.example.myapplication.Views.AttendeesView newInstance(String eventId) {
//        com.example.myapplication.Views.AttendeesView fragment = new com.example.myapplication.Views.AttendeesView();
//        Bundle args = new Bundle();
//        args.putString(ARG_EVENT_ID, eventId);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    public AttendeesView() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // Retrieve eventId from arguments
//        if (getArguments() != null) {
//            eventId = getArguments().getString(ARG_EVENT_ID);
//            Log.d(TAG, "AttendeesFragment created with eventId: " + eventId);
//        } else {
//            Toast.makeText(getContext(), "Event ID missing.", Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "Event ID is missing from arguments.");
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_attendees, container, false);
//
//        recyclerViewSelected = view.findViewById(R.id.recyclerViewSelected);
//        recyclerViewConfirmed = view.findViewById(R.id.recyclerViewConfirmed);
//        recyclerViewCancelled = view.findViewById(R.id.recyclerViewCancelled);
//
//        noneConfirmed = view.findViewById(R.id.noneConfirmed);
//        noneSelected = view.findViewById(R.id.noneSelected);
//        noneCancelled = view.findViewById(R.id.noneCancelled);
//
//        recyclerViewSelected.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerViewConfirmed.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerViewCancelled.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        selectedAdapter = new AttendeesAdapter(selectedList, getContext(), selected, eventId);
//        confirmedAdapter = new AttendeesAdapter(confirmedList, getContext(), confirmed, eventId);
//        cancelledAdapter = new AttendeesAdapter(cancelledList, getContext(), cancelled, eventId);
//
//        recyclerViewSelected.setAdapter(selectedAdapter);
//        recyclerViewConfirmed.setAdapter(confirmedAdapter);
//        recyclerViewCancelled.setAdapter(cancelledAdapter);
//
//        controller = new AttendeesController();
//        controller.fetchAttendees(eventId, selectedAdapter, confirmedAdapter, cancelledAdapter);
//        controller.updateEmptyListMessages(noneConfirmed, noneSelected, noneCancelled, recyclerViewConfirmed, recyclerViewSelected, recyclerViewCancelled);
//
//        return view;
//    }
//}
