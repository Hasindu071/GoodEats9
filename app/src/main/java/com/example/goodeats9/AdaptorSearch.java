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

    private ArrayList<DataClass> dataList;
    private Context context;
    private LayoutInflater layoutInflater;

    // Constructor
    public AdaptorSearch(Context context, ArrayList<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // ViewHolder pattern to improve performance
    static class ViewHolder {
        ImageView gridImage;
        TextView RecipeName,Profile;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder();

            holder.gridImage = convertView.findViewById(R.id.gridImage);
            holder.RecipeName = convertView.findViewById(R.id.RecipeName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set caption
        holder.RecipeName.setText(dataList.get(position).getName());

        // Use Glide to load images with error handling and placeholders
        Glide.with(context)
                .load(dataList.get(position).getImageUri())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                        .error(R.drawable.error_image))     // Error image if URL fails
                .into(holder.gridImage);

        return convertView;
    }
}
