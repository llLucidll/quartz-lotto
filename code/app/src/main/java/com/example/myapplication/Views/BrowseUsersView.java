package com.example.myapplication.Views;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.UserAdapter;
import com.example.myapplication.Controllers.BrowseUsersController;

import java.util.ArrayList;

/**
 * View for managing the user browsing UI.
 * Handles RecyclerView setup and data display.
 */
public class BrowseUsersView {

    private final Context context;
    private final RecyclerView userRecyclerView;
    private final UserAdapter userAdapter;
    private final BrowseUsersController controller;

    /**
     * Constructs a BrowseUsersView.
     *
     * @param context        The context of the activity.
     * @param recyclerView   The RecyclerView to display users.
     * @param controller     The controller for managing user data.
     * @param currentUserId  The current user's ID for filtering or special handling.
     */
    public BrowseUsersView(Context context, RecyclerView recyclerView, BrowseUsersController controller, String currentUserId) {
        this.context = context;
        this.userRecyclerView = recyclerView;
        this.controller = controller;

        userAdapter = new UserAdapter(context, new ArrayList<>(), currentUserId);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        userRecyclerView.setAdapter(userAdapter);
    }

    /**
     * Configures the toolbar for back navigation.
     *
     * @param toolbar       The toolbar to set up.
     * @param onBackPressed The action to perform when the back button is pressed.
     */
    public void setToolbar(Toolbar toolbar, Runnable onBackPressed) {
        toolbar.setNavigationOnClickListener(v -> onBackPressed.run());
    }

    /**
     * Loads user data into the RecyclerView by fetching it from the controller.
     */
    public void loadUsers() {
        controller.fetchUsers(users -> {
            userAdapter.getUserList().clear();
            userAdapter.getUserList().addAll(users);
            userAdapter.notifyDataSetChanged();
        }, errorMessage -> Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show());
    }
}
