package com.example.goodeats9;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyrecipeAdapter extends RecyclerView.Adapter<MyrecipeAdapter.MyViewHolder> {

    private ArrayList<DataClass> dataList;
    private Context context;
    private OnItemClickListener onItemClickListener; // Interface for handling item clicks
    private OnDeleteClickListener onDeleteClickListener; // Interface for handling delete clicks

    // Constructor with click listeners
    public MyrecipeAdapter(Context context, ArrayList<DataClass> dataList,
                           OnItemClickListener onItemClickListener, OnDeleteClickListener onDeleteClickListener) {
        this.context = context;
        this.dataList = dataList;
        this.onItemClickListener = onItemClickListener;
        this.onDeleteClickListener = onDeleteClickListener; // Initialize delete listener
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Load image and set name
        Glide.with(context).load(dataList.get(position).getImageUri()).into(holder.recyclerImage);
        holder.recyclerCaption.setText(dataList.get(position).getName());

        // Set item click listener
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(dataList.get(position)); // Pass the clicked item
            }
        });

        // Set delete icon click listener
        holder.deleteIcon.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(dataList.get(position)); // Pass the item to delete
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // ViewHolder class
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView recyclerImage;
        TextView recyclerCaption;
        ImageView deleteIcon; // Add delete icon reference

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.recyclerImage);
            recyclerCaption = itemView.findViewById(R.id.recyclerCaption);
            deleteIcon = itemView.findViewById(R.id.deletebtn); // Initialize delete icon
        }
    }

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(DataClass data); // Method to pass the clicked data item
    }

    // Interface for handling delete clicks
    public interface OnDeleteClickListener {
        void onDeleteClick(DataClass data); // Method to pass the data item to delete
    }
}
