package com.learningjavaandroid.homesearch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.learningjavaandroid.homesearch.R;

// ======= CUSTOM INFO WINDOW ON MARKER CLICK ====== //
public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter{

    private Context context;
    private View view;
    private LayoutInflater layoutInflater;

    // Inflates the custom info window for our view
    public CustomInfoWindow(Context context){
        this.context = context;
        // Inflate the window for view
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Get the fragment that was set up
        view = layoutInflater.inflate(R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView propertyPrice = view.findViewById(R.id.info_price);
        TextView propertyType = view.findViewById(R.id.info_type);

        // Get the current title for custom info window
        propertyPrice.setText(marker.getTitle());
        propertyType.setText(marker.getSnippet());

        return view;
    }
}
