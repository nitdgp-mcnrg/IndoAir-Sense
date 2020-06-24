package com.mnk.env;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class AddLocation extends AppCompatActivity {

    private AppCompatAutoCompleteTextView placeACTV, roomACTV;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference indoorReference, outdoorReference;
    private List<String> indoorPlaceName, emptyList;
    private String placeName, roomName, placeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        placeACTV = findViewById(R.id.add_place_AutoTextView);
        roomACTV = findViewById(R.id.add_room_AutoTextView);

        indoorPlaceName = new ArrayList<>();
        emptyList = new ArrayList<>();


        setArrayAdapter(placeACTV, indoorPlaceName);
        setArrayAdapter(roomACTV, emptyList);

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        indoorReference = firebaseDatabase.getReference("LocationInfo").child("INDOOR");
        outdoorReference = firebaseDatabase.getReference("LocationInfo").child("OUTDOOR");
    }

    private void setArrayAdapter(AppCompatAutoCompleteTextView a, List<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item, list);
        a.setAdapter(adapter);
    }

    public void checkAddPlaceType(View view) {
        switch (view.getId()) {
            case R.id.add_indoorTypeRadioButton:
                placeType = "INDOOR";
                readIndoorLocation();
                roomACTV.setVisibility(View.VISIBLE);
                break;

            case R.id.add_outdoorTypeRadioButton:
                placeType = "OUTDOOR";
                roomACTV.setVisibility(View.GONE);
                break;
        }
    }

    public void addLocation(View view) {
        if (!getAndCheckErrorData()) {
            if (placeType.equals("INDOOR")) {
                LocationDataModel locationDataModel = new LocationDataModel(roomName);
                indoorReference.child(placeName).push().setValue(locationDataModel);
                roomACTV.setText("");
            } else {
                LocationDataModel locationDataModel = new LocationDataModel(placeName);
                outdoorReference.push().setValue(locationDataModel);
            }
            placeACTV.setText("");
            placeACTV.requestFocus();
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "There is Some Error.\nCheck Again.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean getAndCheckErrorData() {
        placeName = placeACTV.getText().toString().trim();
        roomName = roomACTV.getText().toString().trim();

        if (placeName.equals("")) {
            placeACTV.setError("Place required");
            placeACTV.requestFocus();
            return true;
        }
        if (placeType.equals("INDOOR") && roomName.equals("")) {
            roomACTV.setError("Room Name Required");
            roomACTV.requestFocus();
            return true;
        }
        return false;
    }

    private void readIndoorLocation() {
        indoorReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                indoorPlaceName.clear();
                String Key = "";
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Key = dataSnapshot1.getKey();
                    indoorPlaceName.add(Key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddLocation.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
