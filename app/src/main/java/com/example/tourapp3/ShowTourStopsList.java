package com.example.tourapp3;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ShowTourStopsList extends AppCompatActivity {

    ListView lv_savedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tour_stops_list);

        lv_savedLocations = findViewById((R.id.lv_waypoints));

        MyApplication myApplication = (MyApplication)getApplicationContext();
        List<Location> saveLocation = myApplication.getMyLocations();

        lv_savedLocations.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, saveLocation));
    }
}