package com.example.goodeats9;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
//-----------------------------------------IM/2021/062 - Ksvishi ---------------------------------------------------//

public class IngredientsFragment extends Fragment {

    private RecyclerView recyclerView;
    private IngredientAdapter ingredientAdapter;
    private List<String> ingredientList; // List of Strings to hold ingredient names
    private DatabaseReference recipesDatabaseReference;

    public IngredientsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_ingredients); // Ensure you have a RecyclerView in your fragment layout
        ingredientList = new ArrayList<>();
        ingredientAdapter = new IngredientAdapter(ingredientList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(ingredientAdapter);

        // Assuming email and recipeID are already passed as arguments
        Bundle args = getArguments();
        if (args != null) {
            String email = args.getString("email");
            String recipeID = args.getString("recipeID");
            fetchIngredients(email, recipeID);
        }

        return view;
    }

    private void fetchIngredients(String email, String recipeID) {
        recipesDatabaseReference = FirebaseDatabase.getInstance().getReference("recipes")
                .child(email) // Assuming the email is used as a node
                .child(recipeID)
                .child("ingredients"); // Adjust based on your Firebase structure

        recipesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ingredientList.clear(); // Clear the list before adding new data
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()) {
                        String ingredient = ingredientSnapshot.getValue(String.class);
                        ingredientList.add(ingredient); // Directly add the string to the list
                    }
                    ingredientAdapter.notifyDataSetChanged(); // Notify the adapter of data change
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
//-----------------------------------------IM/2021/050 - Kavishi ---------------------------------------------------//
