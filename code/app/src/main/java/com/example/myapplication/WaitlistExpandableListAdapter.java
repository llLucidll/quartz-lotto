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

    /**
     * Returns the number of groups in the list.
     * @return
     */
    @Override
    public int getGroupCount() {
        return listGroupTitles.size();
    }

    /**
     *  Returns the number of children for a given group.
     * @param groupPosition the position of the group for which the children
     *            count should be returned
     * @return
     */

    @Override
    public int getChildrenCount(int groupPosition) {
        String group = listGroupTitles.get(groupPosition);
        return listData.get(group).size();
    }

    /**
     * Returns the group at the specified position.
     * @param groupPosition the position of the group
     * @return
     */

    @Override
    public Object getGroup(int groupPosition) {
        return listGroupTitles.get(groupPosition);
    }

    /**
     *  Returns the child at the specified position.
     * @param groupPosition the position of the group that the child resides in
     * @param childPosition the position of the child with respect to other
     *            children in the group
     * @return
     */

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String group = listGroupTitles.get(groupPosition);
        return listData.get(group).get(childPosition);
    }

    /**
     * Returns the ID for the group at the specified position.
     * @param groupPosition the position of the group for which the ID is wanted
     * @return
     */

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Returns the ID for the child at the specified position.
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group for which
     *            the ID is wanted
     * @return
     */

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Indicates whether the group ID is stable across changes to the underlying
     * @return
     */

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Returns the View for the group at the specified position.
     * @param groupPosition the position of the group for which the View is
     *            returned
     * @param isExpanded whether the group is expanded or collapsed
     * @param convertView the old view to reuse, if possible. You should check
     *            that this view is non-null and of an appropriate type before
     *            using. If it is not possible to convert this view to display
     *            the correct data, this method can create a new view. It is not
     *            guaranteed that the convertView will have been previously
     *            created by
     *            {@link #getGroupView(int, boolean, View, ViewGroup)}.
     * @param parent the parent that this view will eventually be attached to
     * @return
     */

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

    /**
     * Returns the View for the child at the specified position.
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child (for which the View is
     *            returned) within the group
     * @param isLastChild Whether the child is the last child within the group
     * @param convertView the old view to reuse, if possible. You should check
     *            that this view is non-null and of an appropriate type before
     *            using. If it is not possible to convert this view to display
     *            the correct data, this method can create a new view. It is not
     *            guaranteed that the convertView will have been previously
     *            created by
     *            {@link #getChildView(int, int, boolean, View, ViewGroup)}.
     * @param parent the parent that this view will eventually be attached to
     * @return
     */

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

    /**
     * Indicates whether the child at the specified position is selectable.
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group
     * @return
     */

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
