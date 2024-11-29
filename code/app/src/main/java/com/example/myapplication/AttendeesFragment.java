package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private List<Attendee> selectedList = new ArrayList<>();
    private List<Attendee> confirmedList = new ArrayList<>();
    private List<Attendee> cancelledList = new ArrayList<>();
    private String eventId;
    private String userType = "entrant"; // default

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Factory method to create a new instance of AttendeesFragment with the provided eventId.
     *
     * @param eventId The ID of the event.
     * @return A new instance of fragment AttendeesFragment.
     */
    public static AttendeesFragment newInstance(String eventId) {
        AttendeesFragment fragment = new AttendeesFragment();
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

        // Initialize RecyclerView
        recyclerViewSelected = view.findViewById(R.id.recyclerViewSelected);
        recyclerViewConfirmed = view.findViewById(R.id.recyclerViewConfirmed);
        recyclerViewCancelled = view.findViewById(R.id.recyclerViewCancelled);

        recyclerViewSelected.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewConfirmed.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCancelled.setLayoutManager(new LinearLayoutManager(getContext()));

        selectedAdapter = new AttendeesAdapter(selectedList, getContext(), userType, eventId);
        confirmedAdapter = new AttendeesAdapter(confirmedList, getContext(), userType, eventId);
        cancelledAdapter = new AttendeesAdapter(cancelledList, getContext(), userType, eventId);

        recyclerViewSelected.setAdapter(selectedAdapter);
        recyclerViewConfirmed.setAdapter(confirmedAdapter);
        recyclerViewCancelled.setAdapter(cancelledAdapter);

        // Fetch userType and then attendees
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).getUserType(new BaseActivity.UserTypeCallback() {
                @Override
                public void onCallback(String type) {
                    userType = type;
                    selectedAdapter.setUserType(userType);
                    confirmedAdapter.setUserType(userType);
                    cancelledAdapter.setUserType(userType);
                    selectedAdapter.notifyDataSetChanged();
                    confirmedAdapter.notifyDataSetChanged();
                    cancelledAdapter.notifyDataSetChanged();
                    fetchAttendees();
                }
            });
        } else {
            fetchAttendees();
        }

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
                        Log.d(TAG, "Number of attendees fetched: " + selectedList.size());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching attendees.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching attendees: ", e);
                });
    }
}
