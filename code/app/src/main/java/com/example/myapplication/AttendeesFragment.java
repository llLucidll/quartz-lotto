package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Attendee;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display the list of attendees for an event.
 */
public class AttendeesFragment extends Fragment {

    private static final String TAG = "AttendeesFragment";
    private static final String ARG_EVENT_ID = "eventId";

    private RecyclerView recyclerViewSelected, recyclerViewConfirmed, recyclerViewCancelled;
    private AttendeesAdapter selectedAdapter, confirmedAdapter, cancelledAdapter;
    private TextView noneConfirmed, noneSelected, noneCancelled;
    private List<Attendee> selectedList = new ArrayList<>();
    private String selected = "selected";
    private List<Attendee> confirmedList = new ArrayList<>();
    private String confirmed = "confirmed";
    private List<Attendee> cancelledList = new ArrayList<>();
    private String cancelled = "cancelled";
    private String eventId;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Factory method to create a new instance of AttendeesFragment with the provided eventId.
     *
     * @param eventId The ID of the event.
     * @return A new instance of fragment AttendeesFragment.
     */
    public static com.example.myapplication.AttendeesFragment newInstance(String eventId) {
        com.example.myapplication.AttendeesFragment fragment = new com.example.myapplication.AttendeesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    public AttendeesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve eventId from arguments
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
            Log.d(TAG, "AttendeesFragment created with eventId: " + eventId);
        } else {
            Toast.makeText(getContext(), "Event ID missing.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Event ID is missing from arguments.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendees, container, false);

        recyclerViewSelected = view.findViewById(R.id.recyclerViewSelected);
        recyclerViewConfirmed = view.findViewById(R.id.recyclerViewConfirmed);
        recyclerViewCancelled = view.findViewById(R.id.recyclerViewCancelled);

        noneConfirmed = view.findViewById(R.id.noneConfirmed);
        noneSelected = view.findViewById(R.id.noneSelected);
        noneCancelled = view.findViewById(R.id.noneCancelled);

        recyclerViewSelected.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewConfirmed.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCancelled.setLayoutManager(new LinearLayoutManager(getContext()));

        selectedAdapter = new AttendeesAdapter(selectedList, getContext(), selected, eventId);
        confirmedAdapter = new AttendeesAdapter(confirmedList, getContext(), confirmed, eventId);
        cancelledAdapter = new AttendeesAdapter(cancelledList, getContext(), cancelled, eventId);

        recyclerViewSelected.setAdapter(selectedAdapter);
        recyclerViewConfirmed.setAdapter(confirmedAdapter);
        recyclerViewCancelled.setAdapter(cancelledAdapter);



//        // Fetch userType and then attendees
//        if (getActivity() instanceof BaseActivity) {
//            ((BaseActivity) getActivity()).getUserType(new BaseActivity.UserTypeCallback() {
//                @Override
//                public void onCallback(String status) {
//                    selectedAdapter.setStatus(status);
//                    confirmedAdapter.setStatus(status);
//                    cancelledAdapter.setStatus(status);
//                    selectedAdapter.notifyDataSetChanged();
//                    confirmedAdapter.notifyDataSetChanged();
//                    cancelledAdapter.notifyDataSetChanged();
//                    fetchAttendees();
//                }
//            });
//        } else {
        fetchAttendees();


        return view;
    }

    /**
     * Fetches the list of attendees from Firestore.
     */
    public void fetchAttendees() {
        if (eventId == null) {
            Log.e(TAG, "Cannot fetch attendees. eventId is null.");
            return;
        }

        db.collection("Events").document(eventId)
                .collection("Waitlist")
                .get()
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        selectedList.clear();
                        confirmedList.clear();
                        cancelledList.clear();
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Attendee attendee = doc.toObject(Attendee.class);
                            if (attendee != null) {
                                attendee.setUserId(doc.getId());
                                String status = attendee.getStatus();
                                if ("selected".equalsIgnoreCase(status)) {
                                    selectedList.add(attendee);
                                } else if ("confirmed".equalsIgnoreCase(status)) {
                                    confirmedList.add(attendee);
                                } else if ("cancelled".equalsIgnoreCase(status)) {
                                    cancelledList.add(attendee);
                                }
                                Log.d(TAG, "Attendee fetched: " + attendee.getUserName());
                            }
                        }
                        selectedAdapter.notifyDataSetChanged();
                        confirmedAdapter.notifyDataSetChanged();
                        cancelledAdapter.notifyDataSetChanged();
                        updateEmptyListMessages();
                        Log.d(TAG, "Number of attendees fetched: " + selectedList.size());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching attendees.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching attendees: ", e);
                });
    }
    private void updateEmptyListMessages() {


        // Confirmed List
        if (confirmedList.isEmpty()) {
            noneConfirmed.setVisibility(View.VISIBLE);
            recyclerViewConfirmed.setVisibility(View.GONE);
        } else {
            noneConfirmed.setVisibility(View.GONE);
            recyclerViewConfirmed.setVisibility(View.VISIBLE);
        }

        // Selected List
        if (selectedList.isEmpty()) {
            noneSelected.setVisibility(View.VISIBLE);
            recyclerViewSelected.setVisibility(View.GONE);
        } else {
            noneSelected.setVisibility(View.GONE);
            recyclerViewSelected.setVisibility(View.VISIBLE);
        }

        // Cancelled List
        if (cancelledList.isEmpty()) {
            noneCancelled.setVisibility(View.VISIBLE);
            recyclerViewCancelled.setVisibility(View.GONE);
        } else {
            noneCancelled.setVisibility(View.GONE);
            recyclerViewCancelled.setVisibility(View.VISIBLE);
        }
    }

}
