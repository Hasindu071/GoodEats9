package com.example.goodeats9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyRecipies extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<DataClass> dataList;
    private MyrecipeAdapter adapter;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipies);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI Components
        recyclerView = findViewById(R.id.myRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Data List and Adapter with click listener
        dataList = new ArrayList<>();
        adapter = new MyrecipeAdapter(this, dataList, this::onItemClick, this::onDeleteClick,this::onEditClick); // Pass the delete handler
        recyclerView.setAdapter(adapter);

        // Fetch the user's recipes
        fetchAllRecipes();
    }

    private void fetchAllRecipes() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail().replace(".", "_"); // Replace dots with underscores for Firebase key

            // Reference to the user's recipes in Firebase
            DatabaseReference userRecipesRef = FirebaseDatabase.getInstance().getReference("recipes").child(userEmail);
            userRecipesRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dataList.clear();  // Clear the existing list to avoid duplicates

                    // Iterate through the user's recipes
                    for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                        String imageUri = recipeSnapshot.child("imageUri").getValue(String.class);
                        String name = recipeSnapshot.child("name").getValue(String.class);
                        String description = recipeSnapshot.child("description").getValue(String.class);
                        String serves = recipeSnapshot.child("serves").getValue(String.class);
                        String cookTime = recipeSnapshot.child("cookTime").getValue(String.class);
                        String username = recipeSnapshot.child("username").getValue(String.class);
                        String videoUri = recipeSnapshot.child("videoUri").getValue(String.class);
                        String recipeId = recipeSnapshot.getKey();  // Use recipeSnapshot key as recipeId

                        // Add the recipe data to the list if imageUri and name are available
                        if (imageUri != null && name != null) {
                            // Populate DataClass with all necessary fields
                            DataClass dataClass = new DataClass(imageUri, name, cookTime, description, serves, username, videoUri, recipeId, userEmail);
                            dataList.add(dataClass);
                        }
                    }

                    // Notify adapter that the data has changed
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MyRecipies", "Error fetching data: " + error.getMessage());
                    Toast.makeText(MyRecipies.this, "Failed to load recipes", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("MyRecipies", "User not authenticated");
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle item clicks
    private void onItemClick(DataClass selectedRecipe) {
        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = currentUser != null ? currentUser.getEmail() : null;

        // Create an intent
        Intent intent = new Intent(this, recipeMain.class);

        // Pass the recipe data to the new activity
        intent.putExtra("imageUri", selectedRecipe.getImageUri());
        intent.putExtra("name", selectedRecipe.getName());
        intent.putExtra("description", selectedRecipe.getDescription());
        intent.putExtra("cookTime", selectedRecipe.getCookTime());
        intent.putExtra("serves", selectedRecipe.getServes());
        intent.putExtra("username", selectedRecipe.getUserName());
        intent.putExtra("videoUri", selectedRecipe.getVideoUri());

        // Pass recipe ID and current user email
        intent.putExtra("recipeID", selectedRecipe.getRecipeId());
        intent.putExtra("currentUserEmail", selectedRecipe.getUserEmail());

        // Start the new activity
        startActivity(intent);
    }

    // Handle delete clicks
    private void onDeleteClick(DataClass recipeToDelete) {
        String recipeId = recipeToDelete.getRecipeId();
        String userEmail = recipeToDelete.getUserEmail();

        // Delete recipe from Firebase
        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("recipes").child(userEmail).child(recipeId);
        recipeRef.removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MyRecipies.this, "Recipe deleted successfully", Toast.LENGTH_SHORT).show();
                        // Optionally refresh the list
                        fetchAllRecipes(); // This will re-fetch the updated list
                    } else {
                        Toast.makeText(MyRecipies.this, "Failed to delete recipe", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Handle delete clicks
    public void onEditClick(DataClass selectedRecipe) {
        // Handle edit icon click
        Intent intent = new Intent(this,UpdateRecipe.class);

        // Pass the data from the selected recipe to the edit activity
        intent.putExtra("recipeID", selectedRecipe.getRecipeId());
        intent.putExtra("title", selectedRecipe.getName());
        intent.putExtra("Image", selectedRecipe.getImageUri());
        intent.putExtra("cookTime", selectedRecipe.getCookTime());
        intent.putExtra("serves", selectedRecipe.getServes());
        intent.putExtra("video", selectedRecipe.getVideoUri());
        intent.putExtra("description", selectedRecipe.getDescription());

        // Pass the lists (convert List to ArrayList)
        //intent.putStringArrayListExtra("stepList", new ArrayList<>(selectedRecipe.getStepList()));
        //intent.putStringArrayListExtra("ingredientList", new ArrayList<>(selectedRecipe.getIngredientList()));

        // Start the new activity for editing
        startActivity(intent);
    }
}
