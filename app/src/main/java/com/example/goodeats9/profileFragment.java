package com.example.goodeats9;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class profileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Find the icon by ID
        ImageView profileIcon = view.findViewById(R.id.editProfileButton);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event (show edit profile activity)

                Intent intent = new Intent(getActivity(), edit_profile.class);
                startActivity(intent);

            }
        });

        return view;
    }
}