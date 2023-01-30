package com.example.tourapp3;

import static com.example.tourapp3.MapsActivity.PERMISSIONS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.tourapp3.databinding.ActivityWorldMapBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorldMapActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final int defaultUpdateInterval = 30;
    public static final int fastUpdateInterval = 5;

    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private GoogleMap mMap;
    private ActivityWorldMapBinding binding;
    private static WorldMapActivity singleton;

    Boolean inTour;
    List<Location> savedLocations;

    private String currentTourId;

    List<LatLng> stopsInTour;

    LocationRequest locationRequest;

    LocationCallback locationCallback;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference;

    MyApplication myApplication;

    String tourIDs;

    EditText ET_Firestorechecker;

    Button startTour, website;

    FirebaseStorage storageRef;

    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inTour = false;



        storageRef = FirebaseStorage.getInstance();

        //Somewhere in this method there should be code that retrieves a list of locations "starting point for tours"
        //After getting that list of locations, it will need to place markers at the locations place this code in onMapReady

        //currentLocation = fusedLocationProviderClient.getLastLocation().addOnSuccessListener()

        //tourIDs = new ArrayList<>();
        db.collection("Tours").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    tourIDs = doc.getId();
                }
            }
        });

        binding = ActivityWorldMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        savedLocations = new ArrayList<Location>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //savedLocations = myApplication.getMyLocations();



        //ET_Firestorechecker = findViewById(R.id.ET_Firestorechecker);

//        startTour.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(WorldMapActivity.this);
        //getCurrentLocation();

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

//    private void getCurrentLocation() {
//        if (ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
//            return;
//        }
//        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
//        task.addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    Toast.makeText(getApplicationContext(), location.getLatitude() + "" + location.getLongitude(), Toast.LENGTH_SHORT).show();
//                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//                    assert supportMapFragment != null;
//                    supportMapFragment.getMapAsync(WorldMapActivity.this);
//                }
//            }
//        });
//    }

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
        Handler handler = new Handler();


        Runnable refresh = new Runnable() {
            public void run() {
                // Do something
                updateGPS();
                if(inTour != true) {
                    mMap.clear();
                    savedLocations = myApplication.getMyLocations();
                    if (savedLocations != null) {
                        for (Location location : myApplication.getMyLocations()) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(latLng));
                            Log.d("TAG", "Line of code after marker add");
                        }
                    }

                }

                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_user);

                if(currentLocation != null) {
                    LatLng lng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
//                    mMap.addMarker(new MarkerOptions().position(lng).title("User")
//                            .icon(Bit));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(lng).title("User").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lng, 16.0f));
                }
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(refresh);


        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if(marker.getTitle() != "User") {
                    //currentTourId = db.collection("Tours").;
                    openPopUpWindow();

                }
                return true;
            }
        });
    }

    private void openPopUpWindow() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_marker_popup, null);

        startTour = popupView.findViewById(R.id.startTour);
        website = popupView.findViewById(R.id.visitWebsite);

        if(inTour == true)
            startTour.setText("Stop Tour");

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        startTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                StartTour();
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            String url;
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                db.collection("Tours").document(tourIDs).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        url = documentSnapshot.get("Website").toString();
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(launchBrowser);
                    }

                });
            }
        });
        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //popupWindow.dismiss();
                return true;
            }
        });


    }

    public LatLng makeLatLng(double lat, double lon){
        LatLng latlng = new LatLng(lat,lon);

        return latlng;
    }

    public void StartTour(){
        final MediaPlayer[] mediaPlayer = {new MediaPlayer()};
        int stopsCounter = 0;

        inTour = !inTour;
        stopsInTour = new ArrayList<>();

        if(inTour == true) {
            db.collection("Tours").document(tourIDs).collection("Stops").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        LatLng latLng = new LatLng(documentSnapshot.getDouble("Lat"), documentSnapshot.getDouble("Lon"));
                        stopsInTour.add(latLng);
                    }


//                    storageRef.getReference().child("songs/song1.mp3").downloadUrl.addOnSuccessListener({
//                    mediaPlayer[0] = new MediaPlayer();
//                    mediaPlayer[0].setDataSource(it.toString());
//                    mediaPlayer[0].setOnPreparedListener {player ->
//                    player.start()
//            }
//                    mediaPlayer[0].prepareAsync()
//            })
                    TourHandler();



                }

            });
        }
        //stopsCounter = db.collection("Tours").document(currentTourId);

    }

    private void TourHandler() {
        MediaPlayer mediaPlayer = new MediaPlayer();

        StorageReference ref = storageRef.getReference().child("Full Sail Tour Audios");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();

            }
        });

        while (inTour) {
            mMap.clear();
            PolylineOptions tourLine = new PolylineOptions();
            for (LatLng lng:stopsInTour){
                mMap.addMarker(new MarkerOptions().position(lng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            tourLine.addAll(stopsInTour);
            tourLine.width(12);
            tourLine.color(Color.RED);
            tourLine.geodesic(true);

            mMap.addPolyline(tourLine);
            break;
//            storageRef.getReference().child("songs/song1.mp3").downloadUrl.addOnSuccessListener({
//                    val mediaPlayer = MediaPlayer()
//                    mediaPlayer.setDataSource(it.toString())
//                    mediaPlayer.setOnPreparedListener { player ->
//                    player.start()
//            }
//                    mediaPlayer.prepareAsync()
//            })
        }
    }

    public void EndTour(){
        inTour = false;
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
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
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(WorldMapActivity.this);

        //user  already granted permissions
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLocation = new Location(location);
                }
            });
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }

    }


}