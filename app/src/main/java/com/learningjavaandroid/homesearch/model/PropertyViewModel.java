package com.learningjavaandroid.homesearch.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

// ======= FOR USE BY METHODS OF APP FOR EASY RETRIEVAL OF DATA FROM API, NO NEED FOR REPEATED
//         API CALLS ========== //
public class PropertyViewModel extends ViewModel {
    private MutableLiveData<Properties> selectedProperty = new MutableLiveData<>();
    private MutableLiveData<List<Properties>> selectedProperties = new MutableLiveData<>();

    // setting up the code as live data
    private MutableLiveData<String> code = new MutableLiveData<>();

    // set 1 property
    public void setSelectedProperty(Properties properties){
        selectedProperty.setValue(properties);
    }

    // get 1 property
    public LiveData<Properties> getSelectedProperty(){
        return selectedProperty;
    }

    // set all
    public void setSelectedProperties(List<Properties> properties){
        selectedProperties.setValue(properties);
    }

    // get all
    public LiveData<List<Properties>> getProperties(){
        return selectedProperties;
    }

    // get the live postcode
    public LiveData<String> getCode() {
        return code;
    }

    // set the live postcode
    public void selectPostcode(String c) {
        code.setValue(c);
    }
}
