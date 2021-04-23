package com.learningjavaandroid.homesearch.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

// ======= PROVIDES THE URL FOR THE API QUERY ======= //
public class Util {
    // TEST URL
    public static final String PROPERTIES_URL = "https://api.propertydata.co.uk/prices?key=API_KEY&postcode=SW179QQ";

    // POSTCODE CHANGES DEPENDING ON USER INPUT
    public static String getPropertiesUrl(String postcode){
        return "https://api.propertydata.co.uk/prices?key=API_KEY&postcode="+postcode;
    }

    // Remove the soft keyboard
    public static void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
}
