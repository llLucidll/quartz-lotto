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

    /**
     * Required empty public constructor
     */

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

    /**
     * Called to do initial creation of a fragment.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */

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

    /**
     * Inflate the layout for this fragment.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waitlist, container, false);
        //

        Toast.makeText(getContext(), "Waitlist feature not implemented yet.", Toast.LENGTH_SHORT).show();

        return view;
    }
}
