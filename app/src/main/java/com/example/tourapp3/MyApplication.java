package com.example.tourapp3;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    public List<Location> getMyLocations() {
        return myLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }

    public Location getCurrentLocation(){
        return currentLocation;}

    public void setCurrentLocation(Location location){this.currentLocation = location;}

    private static MyApplication singleton;

    public MyApplication getInstance() {
        return singleton;
    }

    private List<Location> myLocations;

    private Location currentLocation;

    public void onCreate(){
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();
        //currentLocation = new Location();
    }
}
