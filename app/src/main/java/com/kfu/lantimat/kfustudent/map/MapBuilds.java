package com.kfu.lantimat.kfustudent.map;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by GabdrakhmanovII on 15.09.2017.
 */

@IgnoreExtraProperties
public class MapBuilds {
    private String name;
    private String address;
    private String city;
    private String type;
    private String lat;
    private String lng;

    public MapBuilds() {
    }

    public MapBuilds(String name, String address, String city, String type, String lat, String lng) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.type = type;
        this.lat = lat;
        this.lng = lng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
}


