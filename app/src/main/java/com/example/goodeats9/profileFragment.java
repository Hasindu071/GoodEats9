package com.example.goodeats9;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class profileFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile, container, false); // Inflate the layout for this fragment
        ImageButton profileIcon = view.findViewById(R.id.editProfileButton);    // Find the icon by ID
        profileIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Handle the click event (show edit profile activity)
                Intent intent = new Intent(getActivity(), edit_profile.class);
                startActivity(intent);

            }
        });

        // Access SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginDetails", MODE_PRIVATE);
        String username = sharedPreferences.getString("UserName", "User"); //If no name is found type user
        String description = sharedPreferences.getString("description", "Description"); //If no name is found type user

        TextView UserNameText = view.findViewById(R.id.userName); // get the username textview ID
        UserNameText.setText(username);//Set the name

        TextView DescText = view.findViewById(R.id.userBio);// get the description textview ID
        DescText.setText(description);



        Button viewRecipe = view.findViewById(R.id.viewRecipeButton);
        viewRecipe.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Handle the click event (show recipe activity)
                Intent intent = new Intent(getActivity(), recipeMain.class);
                startActivity(intent);
            }
        });

        return view;
    }
}