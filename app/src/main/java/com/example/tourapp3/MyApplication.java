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

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location location) {
        this.currentLocation = location;
    }

    public MyApplication getInstance() {
        return singleton;
    }

    private Location currentLocation;

    public void onCreate() {
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();

        List<String> TourIDS = new ArrayList<>();

        //currentLocation = new Location();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Tours").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        TourIDS.add(doc.getId());
                    }

                if(TourIDS != null) {
                    //for (String id: TourIDS) {
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot docSnap:documents) {
                            double _lat = docSnap.getDouble("Lat");
                            double _lon = docSnap.getDouble("Lon");

                            //LatLng latLng = new LatLng();
                            Location location = new Location("Marker");
                            location.setLatitude(_lat);
                            location.setLongitude(_lon);

                            myLocations.add(location);
                        }

//                        db.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                double _lat = (double) documentSnapshot.get("Lat");
//                                double _lon = (double) documentSnapshot.get("Lon");
//
//                                //LatLng latLng = new LatLng();
//                                Location location = new Location("Marker");
//                                location.setLatitude(_lat);
//                                location.setLongitude(_lon);
//
//                                myLocations.add(location);
//                            }
//                        });
                    //}
                }
            }
        });
//        {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//            }
//
//        });

//        if (TourIDS != null) {
//            for (String id : TourIDS) {
//                db.collection("Tours").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        double _lat = (double) documentSnapshot.get("Lat");
//                        double _lon = (double) documentSnapshot.get("Lon");
//
//                        //LatLng latLng = new LatLng();
//                        Location location = new Location("Marker");
//                        location.setLatitude(_lat);
//                        location.setLongitude(_lon);
//
//                        myLocations.add(location);
//                    }
//
//                });
//            }
//        }

    }

}




