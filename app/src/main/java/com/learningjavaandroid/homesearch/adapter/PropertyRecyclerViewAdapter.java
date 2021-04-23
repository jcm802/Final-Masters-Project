package com.learningjavaandroid.homesearch.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learningjavaandroid.homesearch.R;
import com.learningjavaandroid.homesearch.model.Properties;
import com.squareup.picasso.Picasso;


import java.util.List;

// ======== RECYCLER VIEW ============ //
public class PropertyRecyclerViewAdapter extends RecyclerView.Adapter<PropertyRecyclerViewAdapter.ViewHolder> {
    private final List<Properties> propertiesList;

    public PropertyRecyclerViewAdapter(List<Properties> propertiesList) {
        this.propertiesList = propertiesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get object at position index
        Properties properties = propertiesList.get(position);

        // handle of the ViewHolder
        holder.propertyPrice.setText("£" + properties.getPrice());
        holder.propertyType.setText(properties.getType());
        holder.propertyImage.setImageResource(R.drawable.ic_baseline_house_24);



    }

    @Override
    public int getItemCount() {
        return propertiesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // public so they can be accessed by the onBindViewHolder
        public ImageView propertyImage;
        public TextView propertyPrice;
        public TextView propertyType;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyImage = itemView.findViewById(R.id.list_row_image_view);
            propertyPrice = itemView.findViewById(R.id.list_row_price_text_view);
            propertyType = itemView.findViewById(R.id.list_row_type_text_view);
        }
    }
}
