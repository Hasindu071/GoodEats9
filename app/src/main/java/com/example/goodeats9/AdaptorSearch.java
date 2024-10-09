package com.example.goodeats9;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;

public class AdaptorSearch extends BaseAdapter {

    private final ArrayList<DataClass> dataList; // List of recipe data
    private final LayoutInflater layoutInflater; // LayoutInflater for inflating views

    // Constructor
    public AdaptorSearch(Context context, ArrayList<DataClass> dataList) {
        this.dataList = dataList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    // ViewHolder pattern to improve performance
    private static class ViewHolder {
        ImageView gridImage; // ImageView for the recipe image
        TextView recipeName; // TextView for the recipe name
    }

    @Override
    public int getCount() {
        return dataList.size(); // Return the number of items in the list
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position); // Return the item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position; // Return the item ID (position) for the item
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // Check if the view needs to be created or reused
        if (convertView == null) {
            // Inflate the layout for the grid item
            convertView = layoutInflater.inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder();

            // Initialize the views from the layout
            holder.gridImage = convertView.findViewById(R.id.gridImage);
            holder.recipeName = convertView.findViewById(R.id.RecipeName);

            // Tag the holder for future recycling
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag(); // Reuse the existing holder
        }

        // Get the current recipe data
        DataClass currentRecipe = dataList.get(position);

        // Set the recipe name
        holder.recipeName.setText(currentRecipe.getName());

        // Load the image using Glide with placeholder and error image handling
        Glide.with(convertView.getContext())
                .load(currentRecipe.getImageUri())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder_image)  // Placeholder while loading
                        .error(R.drawable.error_image))             // Error image if loading fails
                .into(holder.gridImage);

        return convertView; // Return the completed view to render on screen
    }
}
