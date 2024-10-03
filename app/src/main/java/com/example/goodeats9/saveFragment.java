package com.example.goodeats9;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import java.util.List;

public class saveFragment extends Fragment {

    private RecyclerView recyclerViewSavedRecipes;
    private SavedRecipeAdapter savedRecipeAdapter;
    private List<Datacls> savedRecipesList;

    private DatabaseReference savedRecipesRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save, container, false);

        recyclerViewSavedRecipes = view.findViewById(R.id.recyclerViewSavedRecipes);
        recyclerViewSavedRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        savedRecipesList = new ArrayList<>();
        savedRecipeAdapter = new SavedRecipeAdapter(getContext(), savedRecipesList);
        recyclerViewSavedRecipes.setAdapter(savedRecipeAdapter);

        // Fetch saved recipes from Firebase
        fetchSavedRecipes();

        return view;
    }

    private void fetchSavedRecipes() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Please log in to see saved recipes.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        savedRecipesRef = FirebaseDatabase.getInstance().getReference("saved_recipes").child(userId);

        savedRecipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                savedRecipesList.clear(); // Clear the list before adding new items
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Datacls recipe = snapshot.getValue(Datacls.class);
                    savedRecipesList.add(recipe);
                }
                savedRecipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load saved recipes: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
