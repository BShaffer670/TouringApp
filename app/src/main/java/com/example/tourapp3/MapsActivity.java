package com.example.tourapp3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.tourapp3.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MapsActivity extends AppCompatActivity {
    public static final int defaultUpdateInterval = 30;
    public static final int fastUpdateInterval = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address, tv_numMarkers;
    Switch sw_locationsupdates, sw_gps;
    Button btn_CheckIn, btn_TourStops, btn_worldMap;

    //If true then gps services are active
    boolean usingGPS = false;

    //current location
    public Location currentLocation;

    // List that contains stops for the tour
    public List<Location> stopsInTour;
    // 2-D List of all stops in all tours (may not need 2D list, List<Strings> may suffice) [List should be migrated to database to reduce storage usage
    List<List<Location>> allTours;


    //config file for all FusedLocationProviderClient settings
    LocationRequest locationRequest;

    LocationCallback locationCallback;

    // Google Maps API Stuff
    FusedLocationProviderClient fusedLocationProviderClient;

    MyApplication myApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        tv_numMarkers = findViewById(R.id.tv_numMarkers);

        //CheckIn and TourStops need to be move to tour pop up menu
        btn_CheckIn = findViewById(R.id.btn_CheckIn);
        btn_TourStops = findViewById(R.id.btn_TourStops);
        btn_worldMap = findViewById(R.id.btn_worldMap);

        sw_gps = findViewById(R.id.sw_gps);
        sw_locationsupdates = findViewById(R.id.sw_locationsupdates);

        //setting properties of locationrequest
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .build();

        //Event is triggered when the update interval is met
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //Save GPS location
                if(locationResult.getLastLocation() != null) {
                    updateUIValues(locationResult.getLastLocation());
                }
                else{
                    updateGPS();
                }
            }
        };

        btn_CheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // gets GPS location (probably needs to be in a different class)

                // adds a marker to the global list( Needs to be changed to change color of tour marker
                MyApplication myApplication = (MyApplication)getApplicationContext();
                stopsInTour = myApplication.getMyLocations();
                stopsInTour.add(currentLocation);
                updateGPS();
            }
        });

        btn_TourStops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapsActivity.this, ShowTourStopsList.class);
                startActivity(i);
            }
        });

        btn_worldMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapsActivity.this, WorldMapActivity.class);
                startActivity(i);
            }
        });

        //sw_gps.setOnClickListener(new View.OnClickListener() {

//            @Override
//            public void onClick(View view) {
//
//                //isChecked() looks to see if the switch is on the GUI is active
//                if (sw_gps.isChecked()) {
//                    //use most accurate gps
//                    locationRequest.
//                    tv_sensor.setText("Using GPS sensors.");
//
//                } else {
//                    locationRequest.setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
//                    tv_sensor.setText("Cell Towers + WiFi.");
//                }
//            }
        //});

        sw_locationsupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_locationsupdates.isChecked()) {
                    startLocationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }
        });

        updateGPS();
    } // end onCreate method

    private void stopLocationUpdates() {
        tv_updates.setText(("Location is NOT being tracked."));
        tv_lat.setText("Not tracking");
        tv_lon.setText("Not tracking");
        tv_speed.setText("Not tracking");
        tv_address.setText("Not tracking");
        tv_accuracy.setText("Not tracking");
        tv_altitude.setText("Not tracking");

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        tv_updates.setText(("Location is being tracked."));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:;
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                updateGPS();
            }
            else{
                Toast.makeText(this, "This app requires permission to use GPS services.", Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
        }
    }

    @SuppressLint("MissingPermission")
    private void updateGPS(){
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(MapsActivity.this);

        //user  already granted permissions
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Put location values into the UI components
                    updateUIValues(location);
                    currentLocation = location;
                }
            });
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }

    }

    private void updateUIValues(Location location) {
        // Updates all textviews to correspond with new location
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if(location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else{
            tv_altitude.setText("Not Available");
        }

        if(location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }
        else{
            tv_speed.setText("Not Available");
        }

        Geocoder geocoder = new Geocoder(MapsActivity.this);

        try{
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        }
        catch(Exception exception){
            tv_address.setText("Unable to get street address");
        }

        MyApplication myApplication = (MyApplication)getApplicationContext();
        stopsInTour = myApplication.getMyLocations();
        //Show number of saved waypoints (will be changed to show number of visited tours)

        tv_numMarkers.setText(Integer.toString(stopsInTour.size()));

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

}