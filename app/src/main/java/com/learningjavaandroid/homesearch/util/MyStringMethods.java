package com.learningjavaandroid.homesearch.util;

public class MyStringMethods {

    // Converts API raw data to acceptable display format with capitalisation and no underscores
    public String convertName(String name) {
        if (name.contains("_")) {
            String s1 = name.replace("_", " ");
            String s2 = s1.substring(0, 1).toUpperCase() + s1.substring(1, s1.indexOf(" "));
            String s3 = s1.substring(s1.indexOf(" ") + 1, s1.indexOf(" ") + 2).toUpperCase() + s1.substring(s1.indexOf(" ") + 2);
            return s2 + " " + s3;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    // Method converts raw price API data with commas acceptable for display
    // Failed to implement with repository successfully
    // Intention was to insert commas within api returned for sale house prices
    public String convertPrice(int price){
        String s1 = "£" + price;
        int normalLength = 7;
        if(s1.length() <= normalLength) {
            return s1.substring(0, 4) + "," + s1.substring(4);
        } else {
            return s1.substring(0, 2) + "," + s1.substring(2, 5) + "," + s1.substring(5);
        }
    }
}
