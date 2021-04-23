package com.learningjavaandroid.homesearch.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.learningjavaandroid.homesearch.controller.AppController;
import com.learningjavaandroid.homesearch.util.MyStringMethods;
import com.learningjavaandroid.homesearch.util.Util;
import com.learningjavaandroid.homesearch.model.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// ====== THIS MAKES THE GET REQUEST TO THE API RETRIEVING APPROPRIATE
// ====== INFORMATION AND SETTING POJO ATTRIBUTES ================== //
public class Repository {

    private static Context ctx;
    RequestQueue queue;

    // set up properties list
    static List<Properties> propertiesList = new ArrayList<>();
    // this is where we process the asyncResponse callback
    public static void getProperties(final AsyncResponse callback, String postcode){
        String url = Util.getPropertiesUrl(postcode);
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject jsonObject = response.getJSONObject("data");
                                    JSONArray jsonArray = jsonObject.getJSONArray("raw_data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Properties properties = new Properties();
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        MyStringMethods sm = new MyStringMethods();
                                        // set price
                                        properties.setPrice(jsonObject1.getInt("price"));
                                        // set lat
                                        properties.setLat(jsonObject1.getString("lat"));
                                        // set lng
                                        properties.setLng(jsonObject1.getString("lng"));
                                        // set type
                                        properties.setType(sm.convertName(jsonObject1.getString("type")));
                                        // set distance
                                        properties.setDistance(jsonObject1.getString("distance"));

                                        // add objects to properties list
                                        propertiesList.add(properties);

                                        // attempt at setting a limit on objects in the array list, not fully tested
                                        if(propertiesList.size() >= 50){
                                            propertiesList.remove(properties);
                                        }
                                    }
                                    if(null != callback){ callback.processProperties(propertiesList);}
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        // add to request queue
        AppController.getInstance(ctx).addToRequestQueue(jsonObjectRequest);
    }

}

