package com.example.goodeats9;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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

public class ProcedureFragment extends Fragment {

    private RecyclerView recyclerView;
    private StepAdapter methodAdapter;
    private List<MethodStep> methodStepList;
    private DatabaseReference recipesDatabaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_procedure, container, false);
        recyclerView = view.findViewById(R.id.recyclerView); // Ensure you have a RecyclerView in your fragment layout
        methodStepList = new ArrayList<>();
        methodAdapter = new StepAdapter(methodStepList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(methodAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Assuming email and recipeID are already passed as arguments
        Bundle args = getArguments();
        if (args != null) {
            String email = args.getString("email");
            String recipeID = args.getString("recipeID");
            fetchMethods(email, recipeID); // Call fetchMethods every time the fragment is resumed
        }
    }

    private void fetchMethods(String email, String recipeID) {
        recipesDatabaseReference = FirebaseDatabase.getInstance().getReference("recipes")
                .child(email) // Assuming the email is used as a node
                .child(recipeID)
                .child("methods"); // Adjust based on your Firebase structure

        recipesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                methodStepList.clear(); // Clear the list before adding new data
                if (dataSnapshot.exists()) {
                    int stepCount = 1; // Initialize step count
                    for (DataSnapshot methodSnapshot : dataSnapshot.getChildren()) {
                        String method = methodSnapshot.getValue(String.class);
                        methodStepList.add(new MethodStep("Step " + stepCount, method));
                        stepCount++; // Increment step count
                    }
                    methodAdapter.notifyDataSetChanged(); // Notify the adapter of data change
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}