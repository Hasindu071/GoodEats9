package com.example.goodeats9;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class searchFragment extends Fragment {

    private GridView gridView;
    private ArrayList<DataClass> dataList;
    private AdaptorSearch adapter;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize UI components
        gridView = view.findViewById(R.id.gridView);
        dataList = new ArrayList<>();
        adapter = new AdaptorSearch(getContext(), dataList); // Set adapter with the context and data
        gridView.setAdapter(adapter);

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes");


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected recipe
                DataClass selectedRecipe = (DataClass) parent.getItemAtPosition(position);

                // Create an intent to open the RecipeDetailsActivity
                Intent intent = new Intent(getActivity(), recipeMain.class);

                // Pass the recipe data to the new activity
                intent.putExtra("imageUri", selectedRecipe.getImageUri());
                intent.putExtra("name", selectedRecipe.getName());
                intent.putExtra("description", selectedRecipe.getDescription());
                intent.putExtra("cookTime", selectedRecipe.getCookTime());
                intent.putExtra("serves", selectedRecipe.getServes());
                intent.putExtra("username", selectedRecipe.getUserName());
                intent.putExtra("videoUri", selectedRecipe.getVideoUri());

                // Start the new activity
                startActivity(intent);
            }
        });

        // Fetch all recipes from Firebase
        fetchAllRecipes();

        return view;
    }

    private void fetchAllRecipes() {
        // Listen for changes in the "recipes" node
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();

                // Iterate through all users' recipes nodes in the database
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                    // For each user, get their recipes and details
                    for (DataSnapshot recipeSnapshot : userSnapshot.getChildren()) {
                        String imageUri = recipeSnapshot.child("imageUri").getValue(String.class);
                        String name = recipeSnapshot.child("name").getValue(String.class);
                        String description = recipeSnapshot.child("description").getValue(String.class);
                        String serves = recipeSnapshot.child("serves").getValue(String.class);
                        String cookTime = recipeSnapshot.child("cookTime").getValue(String.class);
                        String username = recipeSnapshot.child("username").getValue(String.class);
                        String videoUri = recipeSnapshot.child("videoUri").getValue(String.class);


                        if (imageUri != null && name != null) {
                            DataClass dataClass = new DataClass(imageUri,name,cookTime,description,serves,username,videoUri); // Create a new DataClass object
                            dataList.add(dataClass);  // Add the recipe data to the list
                        }
                    }
                }

                // Notify the adapter that data has changed to update the UI
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
                Toast.makeText(getContext(), "Failed to load images: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }


        });
    }
}
