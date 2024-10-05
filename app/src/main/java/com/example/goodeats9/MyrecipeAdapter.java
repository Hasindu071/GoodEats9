package com.example.goodeats9;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.goodeats9.DataClass;
import com.example.goodeats9.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyrecipeAdapter extends RecyclerView.Adapter<MyrecipeAdapter.MyViewHolder> {

    private ArrayList<DataClass> dataList;
    private Context context;
    private OnItemClickListener onItemClickListener; // Interface for handling clicks

    // Constructor with click listener
    public MyrecipeAdapter(Context context, ArrayList<DataClass> dataList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.dataList = dataList;
        this.onItemClickListener = onItemClickListener;
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
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // ViewHolder class
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView recyclerImage;
        TextView recyclerCaption;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.recyclerImage);
            recyclerCaption = itemView.findViewById(R.id.recyclerCaption);
        }
    }

    // Interface for handling click events
    public interface OnItemClickListener {
        void onItemClick(DataClass data); // Method to pass the clicked data item
    }
}