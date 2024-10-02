package com.example.goodeats9;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class searchFragment extends Fragment {

    private GridView gridView;
    private EditText searchBar;
    private ArrayList<DataClass> dataList;
    private ArrayList<DataClass> filteredList; // List to hold filtered recipes
    private AdaptorSearch adapter;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize UI components
        gridView = view.findViewById(R.id.gridView);
        searchBar = view.findViewById(R.id.searchBar);
        dataList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new AdaptorSearch(getContext(), filteredList); // Set adapter with the context and filtered data
        gridView.setAdapter(adapter);

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes");

        // Set up search functionality
        setupSearchFunctionality();

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
                filteredList.clear(); // Clear the filtered list too

                // Iterate through all users' recipes nodes in the database
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    // For each user, get their recipes
                    for (DataSnapshot recipeSnapshot : userSnapshot.getChildren()) {
                        String imageUri = recipeSnapshot.child("imageUri").getValue(String.class);
                        String name = recipeSnapshot.child("name").getValue(String.class);

                        if (imageUri != null && name != null) {
                            DataClass dataClass = new DataClass(imageUri, name); // Create a new DataClass object
                            dataList.add(dataClass);  // Add the recipe data to the list
                        }
                    }
                }

                // Initially display all recipes
                filteredList.addAll(dataList);
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

    private void setupSearchFunctionality() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRecipes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterRecipes(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(dataList); // Show all recipes if search query is empty
        } else {
            String filterPattern = query.toLowerCase().trim();
            for (DataClass recipe : dataList) {
                if (recipe.getName().toLowerCase().contains(filterPattern)) {
                    filteredList.add(recipe);
                }
            }
        }
        adapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }
}
