package com.learningjavaandroid.homesearch.model;

import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

// ====== PROPERTIES POJO ====== //
// Converts API information to Java object form for easier access
public class Properties {

        private Integer price;

        private String lat;

        private String lng;

        private Integer bedrooms;

        private String type;

        private String distance;

        public int getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public Integer getBedrooms() {
            return bedrooms;
        }

        public void setBedrooms(Integer bedrooms) {
            this.bedrooms = bedrooms;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDistance() {
            return distance;
        }


        public void setDistance(String distance) {
            this.distance = distance;
        }

        static com.learningjavaandroid.homesearch.model.Properties fill(JSONObject jsonobj) throws JSONException {
            com.learningjavaandroid.homesearch.model.Properties entity = new com.learningjavaandroid.homesearch.model.Properties();
            if (jsonobj.has("price")) {
                entity.setPrice(jsonobj.getInt("price"));
            }
            if (jsonobj.has("lat")) {
                entity.setLat(jsonobj.getString("lat"));
            }
            if (jsonobj.has("lng")) {
                entity.setLng(jsonobj.getString("lng"));
            }
            if (jsonobj.has("bedrooms")) {
                entity.setBedrooms(jsonobj.getInt("bedrooms"));
            }
            if (jsonobj.has("type")) {
                entity.setType(jsonobj.getString("type"));
            }
            if (jsonobj.has("distance")) {
                entity.setDistance(jsonobj.getString("distance"));
            }
            return entity;
        }

        static List<com.learningjavaandroid.homesearch.model.Properties> fillList(JSONArray jsonarray) throws JSONException {
            if (jsonarray == null || jsonarray.length() == 0)
                return null;
            List<com.learningjavaandroid.homesearch.model.Properties> olist = new ArrayList<>();
            for (int i = 0; i < jsonarray.length(); i++) {
                olist.add(fill(jsonarray.getJSONObject(i)));
            }
            return olist;
        }
}



