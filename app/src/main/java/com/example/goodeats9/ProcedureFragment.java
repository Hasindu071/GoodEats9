package com.example.goodeats9;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProcedureFragment extends Fragment {

    private List<String> steps; // List to hold steps (can be any list)
    private RecyclerView recyclerView;
    private StepAdapter adapter;

    public ProcedureFragment(List<String> steps) {
        this.steps = steps; // Pass steps when initializing fragment
    }

    public ProcedureFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_procedure, container, false);
        recyclerView = view.findViewById(R.id.step_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the adapter and RecyclerView
        adapter = new StepAdapter(steps);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
