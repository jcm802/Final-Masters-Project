package com.learningjavaandroid.homesearch.data;

import com.learningjavaandroid.homesearch.model.Properties;

import java.util.List;

// ====== Allows passing around processProperties method with properties list ======= //
public interface AsyncResponse {
    void processProperties(List<Properties> properties);

}
