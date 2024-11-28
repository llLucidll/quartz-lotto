package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Fragment displaying the waitlist for an event.
 */
public class WaitlistFragment extends Fragment {
    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;

    public WaitlistFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param eventId The unique ID of the event.
     * @return A new instance of fragment WaitlistFragment.
     */
    public static com.example.myapplication.WaitlistFragment newInstance(String eventId) {
        com.example.myapplication.WaitlistFragment fragment = new com.example.myapplication.WaitlistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve eventId from arguments
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        } else {
            Toast.makeText(getContext(), "Event ID missing.", Toast.LENGTH_SHORT).show();
            // Optionally, navigate back or show an error
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waitlist, container, false);
        //

        Toast.makeText(getContext(), "Waitlist feature not implemented yet.", Toast.LENGTH_SHORT).show();

        return view;
    }
}
