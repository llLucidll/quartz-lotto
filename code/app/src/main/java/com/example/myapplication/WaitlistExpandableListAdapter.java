package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Adapter for displaying user IDs grouped by status in an ExpandableListView.
 */
public class WaitlistExpandableListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "WaitlistExpandableListAdapter";

    private Context context;
    private List<String> listGroupTitles;
    private HashMap<String, List<String>> listData;
    private Set<String> selectedGroups; // To track selected status groups

    public WaitlistExpandableListAdapter(Context context, List<String> listGroupTitles, HashMap<String, List<String>> listData) {
        this.context = context;
        this.listGroupTitles = listGroupTitles;
        this.listData = listData;
        this.selectedGroups = new HashSet<>();
    }

    @Override
    public int getGroupCount() {
        return listGroupTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String group = listGroupTitles.get(groupPosition);
        return listData.get(group).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listGroupTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String group = listGroupTitles.get(groupPosition);
        return listData.get(group).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // Group view (Status)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String statusGroup = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_status_group, parent, false);
        }

        TextView groupTitle = convertView.findViewById(R.id.status_group_title);
        CheckBox groupCheckBox = convertView.findViewById(R.id.status_group_checkbox);

        groupTitle.setText(capitalize(statusGroup));

        // Manage group selection
        groupCheckBox.setOnCheckedChangeListener(null);
        groupCheckBox.setChecked(selectedGroups.contains(statusGroup));
        groupCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedGroups.add(statusGroup);
            } else {
                selectedGroups.remove(statusGroup);
            }
            Log.d(TAG, "Group " + statusGroup + " selected: " + isChecked);
        });

        return convertView;
    }

    // Child view (User ID)
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String userId = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_user_id, parent, false);
        }

        TextView userIdTextView = convertView.findViewById(R.id.user_id_text_view);
        userIdTextView.setText(userId);
        Log.d(TAG, "Displaying userId: " + userId + " under group: " + getGroup(groupPosition));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    /**
     * Returns the list of selected status groups.
     *
     * @return List of selected status groups.
     */
    public List<String> getSelectedGroups() {
        return new ArrayList<>(selectedGroups);
    }

    /**
     * Utility method to capitalize the first letter of a string.
     *
     * @param text The input string.
     * @return The capitalized string.
     */
    private String capitalize(String text) {
        if (text == null || text.length() == 0) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
