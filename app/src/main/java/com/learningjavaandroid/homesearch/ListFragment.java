package com.learningjavaandroid.homesearch;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningjavaandroid.homesearch.adapter.PropertyRecyclerViewAdapter;
import com.learningjavaandroid.homesearch.data.AsyncResponse;
import com.learningjavaandroid.homesearch.data.Repository;
import com.learningjavaandroid.homesearch.model.Properties;
import com.learningjavaandroid.homesearch.model.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private PropertyRecyclerViewAdapter propertyRecyclerViewAdapter;
    private List<Properties> propertiesList;
    private PropertyViewModel propertyViewModel;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // For view model
        propertiesList = new ArrayList<>();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ==== PROPERTY VIEW MODEL ==== //
        propertyViewModel = new ViewModelProvider(requireActivity())
                .get(PropertyViewModel.class);
        // check for null in the view model (to check whether the model contains the properties list)
        if(propertyViewModel.getProperties().getValue() != null) {
            // get properties
            propertiesList = propertyViewModel.getProperties().getValue();

            propertyRecyclerViewAdapter = new PropertyRecyclerViewAdapter(propertiesList);
            recyclerView.setAdapter(propertyRecyclerViewAdapter);
            // ================================================= //
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.property_recycler);
        recyclerView.setHasFixedSize(true);
        // Using getActivity() here because all fragments are hosted within Activity
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
}