// File: com/example/myapplication/EventPagerAdapter.java
package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Adapter for managing event detail fragments.
 */
public class EventPagerAdapter extends FragmentStateAdapter {
    private final String eventId;

    public EventPagerAdapter(@NonNull AppCompatActivity activity, String eventId) {
        super(activity);
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return DetailsFragment.newInstance(eventId);
            case 1:
                return WaitlistFragment.newInstance(eventId);
            case 2:
                return AttendeesFragment.newInstance(eventId);
            case 3:
                return LocationsFragment.newInstance(eventId);
            default:
                return DetailsFragment.newInstance(eventId);
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Details, Waitlist, Attendees, Locations
    }
}
