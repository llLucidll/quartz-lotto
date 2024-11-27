package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/** Ayesha --
 * BaseActivity allows a method to get the current user's UID
 * and other activities should extend this class to access user identification
 */
public class BaseActivity extends AppCompatActivity {
    /**
     * Retrieves the current user's UID
     *
     */
    protected String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }
}
