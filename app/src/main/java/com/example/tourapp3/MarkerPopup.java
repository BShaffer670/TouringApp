package com.example.tourapp3;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

public class MarkerPopup extends Activity {
    Button btn_startTour, btn_website;
    WorldMapActivity worldMapActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btn_startTour= findViewById(R.id.startTour);
        btn_website = findViewById(R.id.visitWebsite);

        btn_startTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                worldMapActivity.StartTour();
            }
        });
    }
}
