package com.example.tourapp3;

import android.app.Application;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    private Timer autoupdate;

    List<String> TourIDS;

    FirebaseFirestore db;

    public void onCreate() {
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();

        TourIDS = new ArrayList<>();



        //currentLocation = new Location();
        db = FirebaseFirestore.getInstance();

        db.collection("Tours").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        TourIDS.add(doc.getId());
                    }
                    MyAsyncTask myAsyncTask = new MyAsyncTask();
                    myAsyncTask.doInBackground();
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


    class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            for (String id:TourIDS) {
                //for (String id: TourIDS) {
                db.collection("Tours").document(id).collection("Stops").document("Stop1").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        double _lat = (double) documentSnapshot.getDouble("Lat");
                        double _lon = (double) documentSnapshot.getDouble("Lon");

                        Location location = new Location("Marker");
                        location.setLatitude(_lat);
                        location.setLongitude(_lon);

                        myLocations.add(location);
                    }
                });

            }
            return null;
        }
    }
}



