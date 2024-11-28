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

    private RecyclerView recyclerView;
    private AttendeesAdapter attendeesAdapter;
    private List<Attendee> attendeeList = new ArrayList<>();
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
        recyclerView = view.findViewById(R.id.attendeesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        attendeesAdapter = new AttendeesAdapter(attendeeList, getContext(), userType, eventId);
        recyclerView.setAdapter(attendeesAdapter);

        // Fetch userType and then attendees
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).getUserType(new BaseActivity.UserTypeCallback() {
                @Override
                public void onCallback(String type) {
                    userType = type;
                    attendeesAdapter.setUserType(userType);
                    attendeesAdapter.notifyDataSetChanged();
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
                .collection("Attendees")
                .whereEqualTo("status", "Attending")
                .get()
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        attendeeList.clear();
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Attendee attendee = doc.toObject(Attendee.class);
                            if (attendee != null) {
                                attendee.setUserId(doc.getId());
                                attendeeList.add(attendee);
                                Log.d(TAG, "Attendee fetched: " + attendee.getUserName());
                            }
                        }
                        attendeesAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Number of attendees fetched: " + attendeeList.size());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching attendees.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching attendees: ", e);
                });
    }
}
