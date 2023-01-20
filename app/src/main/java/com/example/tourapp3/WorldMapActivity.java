package com.example.tourapp3;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.tourapp3.databinding.ActivityWorldMapBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WorldMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityWorldMapBinding binding;

    List<Location> savedLocations;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference;

    MyApplication myApplication;

    List<String> tourIDs;

    EditText ET_Firestorechecker;

    Button startTour;

    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Somewhere in this method there should be code that retrieves a list of locations "starting point for tours"
        //After getting that list of locations, it will need to place markers at the locations place this code in onMapReady

        //currentLocation = fusedLocationProviderClient.getLastLocation().addOnSuccessListener()

        binding = ActivityWorldMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        savedLocations = new ArrayList<Location>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //savedLocations = myApplication.getMyLocations();


        ET_Firestorechecker = findViewById(R.id.ET_Firestorechecker);

        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();

//        db.collection("Tours")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            tourIDs = new ArrayList<>();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                tourIDs.add(document.getId());
//                                Log.d("TAG", document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = new Location(location);
                    Toast.makeText(getApplicationContext(), location.getLatitude() + "" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(WorldMapActivity.this);
                }
            }
        });
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //double uLat = currentLocation.getLatitude();
        //double uLon = currentLocation.getLongitude();



//        if(tourIDs != null) {
//            for (String ID : tourIDs) {
//                db.collection("Tours").document(ID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        double _Lat = (double) documentSnapshot.get("Lat");
//                        double _Lon = (double) documentSnapshot.get("Lon");
//
//                        LatLng latLng = makeLatLng(_Lat, _Lon);
//                        savedLocations.add(latLng);
//                        Log.d("TAG","Line of code after list add");
//                        if(savedLocations.size() != 0) {
//                            for (LatLng location : savedLocations) {
//                                mMap.addMarker(new MarkerOptions().position(location));
//                                Log.d("TAG","Line of code after marker add");
//
//                                //lastLocationPlaced = latLng;
//                            }
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(savedLocations.get(0), 12.0f));
//                        }
//                    }
//                });
//            }
//
//        }



        myApplication = (MyApplication)getApplicationContext();
        savedLocations = myApplication.getMyLocations();
        if(savedLocations != null) {
             for (Location location : savedLocations) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng));
                Log.d("TAG", "Line of code after marker add");

                //lastLocationPlaced = latLng;
            }

        }

        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                openPopUpWindow();

                return true;
            }
        });
    }

    private void openPopUpWindow() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_marker_popup, null);

        startTour = findViewById(R.id.startTour);
        startTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public LatLng makeLatLng(double lat, double lon){
        LatLng latlng = new LatLng(lat,lon);

        return latlng;
    }


}