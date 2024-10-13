package com.example.goodeats9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//---------------------------IM/2021/011 - Tharushika --------------------------------------------//
public class searchFragment extends Fragment {

    private GridView gridView;
    private ArrayList<DataClass> dataList;
    private ArrayList<DataClass> filteredList;
    private AdaptorSearch adapter;
    private DatabaseReference databaseReference;
    private EditText searchBar;
    private Button btnAll, btnBreakfast, btnLunch, btnDinner, btnSnack; // Category buttons
    private String selectedCategory = ""; // Store the selected category

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize UI components
        gridView = view.findViewById(R.id.gridView);
        searchBar = view.findViewById(R.id.searchBar);
        btnAll = view.findViewById(R.id.all);
        btnBreakfast = view.findViewById(R.id.breakfast);
        btnLunch = view.findViewById(R.id.lunch);
        btnDinner = view.findViewById(R.id.dinner);
        btnSnack = view.findViewById(R.id.snack);

        dataList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new AdaptorSearch(getContext(), filteredList); // Set adapter with the filtered data
        gridView.setAdapter(adapter);

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes");

        gridView.setOnItemClickListener((parent, view1, position, id) -> {
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
            intent.putExtra("recipeID", selectedRecipe.getRecipeId()); // Send recipe ID
            intent.putExtra("currentUserEmail", selectedRecipe.getUserEmail()); // Send current Email

            // Start the new activity
            startActivity(intent);
        });

        // Fetch all recipes from Firebase
        fetchAllRecipes();

        // Setup search functionality
        setupSearch();

        // Setup category button functionality
        setupCategoryButtons();

        return view;
    }

    private void fetchAllRecipes() {
        // Listen for changes in the "recipes" node
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userEmail = userSnapshot.getKey(); // Get the user's email
                    for (DataSnapshot recipeSnapshot : userSnapshot.getChildren()) {
                        String recipeId = recipeSnapshot.getKey(); // Get the recipe ID
                        String imageUri = recipeSnapshot.child("imageUri").getValue(String.class);
                        String name = recipeSnapshot.child("name").getValue(String.class);
                        String description = recipeSnapshot.child("description").getValue(String.class);
                        String serves = recipeSnapshot.child("serves").getValue(String.class);
                        String cookTime = recipeSnapshot.child("cookTime").getValue(String.class);
                        String username = recipeSnapshot.child("username").getValue(String.class);
                        String videoUri = recipeSnapshot.child("videoUri").getValue(String.class);
                        String category = recipeSnapshot.child("category").getValue(String.class); // Fetch category

                        if (imageUri != null && name != null) {
                            DataClass dataClass = new DataClass(imageUri, name, cookTime, description, serves, username, videoUri, recipeId, userEmail, category);
                            dataList.add(dataClass);  // Add the recipe data to the list
                        }
                    }
                }

                // Initially, display all the recipes
                filteredList.clear();
                filteredList.addAll(dataList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load images: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter recipes as the user types
                filterRecipes(s.toString(), selectedCategory);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    //-----------------------------IM/2021/062 - Hasindu -------------------------------------------------//
    private void setupCategoryButtons() {
        // Defaultly make "All" button active
        selectedCategory = "";
        setButtonActive(btnAll);
        setButtonInactive(btnBreakfast);
        setButtonInactive(btnLunch);
        setButtonInactive(btnDinner);
        setButtonInactive(btnSnack);

        View.OnClickListener categoryClickListener = v -> {
            // Set the selected category based on the button clicked
            if (v.getId() == R.id.all) {
                selectedCategory = ""; // Show all categories
                setButtonActive(btnAll);
                setButtonInactive(btnBreakfast);
                setButtonInactive(btnLunch);
                setButtonInactive(btnDinner);
                setButtonInactive(btnSnack);
            } else if (v.getId() == R.id.breakfast) {
                selectedCategory = "Breakfast";
                setButtonActive(btnBreakfast);
                setButtonInactive(btnAll);
                setButtonInactive(btnLunch);
                setButtonInactive(btnDinner);
                setButtonInactive(btnSnack);
            } else if (v.getId() == R.id.lunch) {
                selectedCategory = "Lunch";
                setButtonActive(btnLunch);
                setButtonInactive(btnAll);
                setButtonInactive(btnBreakfast);
                setButtonInactive(btnDinner);
                setButtonInactive(btnSnack);
            } else if (v.getId() == R.id.dinner) {
                selectedCategory = "Dinner";
                setButtonActive(btnDinner);
                setButtonInactive(btnAll);
                setButtonInactive(btnBreakfast);
                setButtonInactive(btnLunch);
                setButtonInactive(btnSnack);
            } else if (v.getId() == R.id.snack) {
                selectedCategory = "Snack";
                setButtonActive(btnSnack);
                setButtonInactive(btnAll);
                setButtonInactive(btnBreakfast);
                setButtonInactive(btnLunch);
                setButtonInactive(btnDinner);
            }

            // Call the filter method, ensuring the search bar is not null
            if (searchBar != null) {
                filterRecipes(searchBar.getText().toString(), selectedCategory);
            }
        };

        // Assign the listener to all category buttons
        btnAll.setOnClickListener(categoryClickListener);
        btnBreakfast.setOnClickListener(categoryClickListener);
        btnLunch.setOnClickListener(categoryClickListener);
        btnDinner.setOnClickListener(categoryClickListener);
        btnSnack.setOnClickListener(categoryClickListener);
    }

    // Methods to set buttons active or inactive
    private void setButtonActive(Button button) {
        button.setBackgroundTintList(getResources().getColorStateList(R.color.green));  // Change background color to green
        button.setTextColor(getResources().getColor(R.color.white));  // Set text color to white
    }

    private void setButtonInactive(Button button) {
        button.setBackgroundTintList(getResources().getColorStateList(R.color.white));  // Change background color to white
        button.setTextColor(getResources().getColor(R.color.green));  // Set text color to green
    }

    //-----------------------------------------IM/2021/062 - Hasindu---------------------------------------//

    // Method to filter recipes based on the search query and selected category
    private void filterRecipes(String query, String category) {
        // Initialize or clear the filteredList before filtering
        if (filteredList == null) {
            filteredList = new ArrayList<>();
        } else {
            filteredList.clear();
        }

        String lowerCaseQuery = query != null ? query.toLowerCase() : "";

        if (lowerCaseQuery.isEmpty() && category.isEmpty()) {
            // Show all recipes if no query or category is selected
            filteredList.addAll(dataList);
        } else {
            // Iterate through the original list and filter based on query and category
            for (DataClass recipe : dataList) {
                if (recipe != null) {
                    boolean matchesQuery = recipe.getName() != null && recipe.getName().toLowerCase().contains(lowerCaseQuery);
                    boolean matchesCategory = category.isEmpty() || (recipe.getCategory() != null && recipe.getCategory().equalsIgnoreCase(category));

                    if (matchesQuery && matchesCategory) {
                        filteredList.add(recipe);
                    }
                }
            }
        }

        // Notify the adapter that the data has changed to update the UI
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
//----------------------------------------------IM/2021/011 Tharushika ----------------------------------------------------------//