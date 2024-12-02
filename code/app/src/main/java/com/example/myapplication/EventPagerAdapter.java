// File: com/example/myapplication/EventPagerAdapter.java
package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.AttendeesFragment;
import com.example.myapplication.Views.WaitingListView;

/**
 * Adapter for managing event detail fragments.
 */
public class EventPagerAdapter extends FragmentStateAdapter {
    private final String eventId;

    public EventPagerAdapter(@NonNull AppCompatActivity activity, String eventId) {
        super(activity);
        this.eventId = eventId;
    }

    /**
     * Creates a new fragment based on the provided position.
     * @param position
     * @return The fragment to display
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return DetailsFragment.newInstance(eventId);
            case 1:
                return WaitingListView.newInstance(eventId);
            case 2:
                return AttendeesFragment.newInstance(eventId);
            case 3:
                return LocationsFragment.newInstance(eventId);
            default:
                return DetailsFragment.newInstance(eventId);
        }
    }

    /**
     * Returns the number of fragments to display.
     * @return The number of fragments to display
     */

    @Override
    public int getItemCount() {
        return 4; // Details, Waitlist, Attendees, Locations
    }
}
