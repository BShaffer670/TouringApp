package com.example.tourapp3;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private static MyApplication singleton;

    private List<Location> myLocations;

    public List<Location> getMyLocations() {
        return myLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }

    public Location getCurrentLocation(){
        return currentLocation;}

    public void setCurrentLocation(Location location){this.currentLocation = location;}

    public MyApplication getInstance() {
        return singleton;
    }

    private Location currentLocation;

    public void onCreate(){
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();

        List<String>TourIDS = new ArrayList<>();

        //currentLocation = new Location();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Tours").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc:task.getResult())
                    {
                        TourIDS.add(doc.getId().toString());
                    }
                }
            }
        });

        if(TourIDS != null){
            for (String id:TourIDS)
            {
                db.collection("Tours").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        double _lat = (double) documentSnapshot.get("Lat");
                        double _lon = (double) documentSnapshot.get("Lon");

                        //LatLng latLng = new LatLng();
                        Location location = new Location("Marker");
                        location.setLatitude(_lat);
                        location.setLongitude(_lon);

                        myLocations.add(location);
                    }

                });
            }
        }
    }
}




