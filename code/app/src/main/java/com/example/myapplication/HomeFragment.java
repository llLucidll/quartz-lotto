//*
package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the button by ID and set up the click listener
        Button navigateButton = view.findViewById(R.id.edit_or_create_button);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // next fragment will be aditi's thing
              //  Fragment nextFragment = new NextFragment(); // destination fragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
              //  transaction.replace(R.id.fragment_container, nextFragment);
                transaction.addToBackStack(null); //adds the transaction to the back stack
                transaction.commit();
            }
        });

        return view;
    }
}
