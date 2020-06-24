package com.mnk.env;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FunctionalitiesActivity extends AppCompatActivity {

    //variables
    private BluetoothAdapter bluetoothAdapter = null;
    static BluetoothSocket bluetoothSocket = null;
    private List<String> placeList, roomList;
    private List<UserComponentListItem> listItems, list2;
    private DatabaseReference getIndoorReference, getoutdoorReference;
    private FirebaseDatabase firebaseDatabase;

    //widgets
    private RecyclerView mainrecyclerView, otherRecyclerView;
    private AlertDialog alertDialog, alertDialog1 = null;
    private LinearLayout placeLayout, roomLayout;
    private TextView dialogHeading;
    private ProgressBar placeProgressBar, roomProgressBar;
    private Button placeButton, roomButton;
    private ListView dialogListView;

    //parameters
    private String placeType, locationtype, placeName, roomName;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functionalities);

        //setting Firebase indoor and Outdoor Reference
        setFirebaseReferences();


        listItems = new ArrayList<>();
        list2 = new ArrayList<>();
        placeList = new ArrayList<>();
        roomList = new ArrayList<>();

        addItems();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.SEND_SMS,
                    Manifest.permission.CAMERA
            }, 10);
        }

        mainrecyclerView = findViewById(R.id.mainRecyclerView);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            mainrecyclerView.setVisibility(View.GONE);
            findViewById(R.id.testingTextView).setVisibility(View.GONE);
        } else {
            mainrecyclerView.setHasFixedSize(true);
            mainrecyclerView.setLayoutManager(new LinearLayoutManager(this));

            mainrecyclerView.setAdapter(new MyAdapter(listItems));

            mainrecyclerView.addOnItemTouchListener(new RecyclerViewOnItemClick(getApplicationContext(), mainrecyclerView, new RecyclerViewOnItemClick.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Intent intent = null;

                    if (position == 0) {
                        intent = new Intent(getApplicationContext(), Bluetooth_set.class);
                    } else {
                        if (bluetoothSocket != null) {
                            switch (position) {
                                case 1:
                                    intent = new Intent(getApplicationContext(), Datasets.class);
                                    break;

                                case 2:
                                    intent = new Intent(getApplicationContext(), Graphset.class);
                                    break;

                                case 3:
                                    intent = new Intent(getApplicationContext(), Mapset.class);
                                    break;
                            }
                        } else {
                            Toast.makeText(FunctionalitiesActivity.this, "Bluetooth Socket Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (intent != null) {
                        startActivity(intent);
                    }
                }

                @Override
                public void onLongClick(View view, int position) {
                }
            }));

            if (!bluetoothAdapter.isEnabled()) {
                showBluetoothEnableDialog();
            }
            //Toast.makeText(getApplicationContext(), "Bluetooth found..\n" + bluetoothAdapter, Toast.LENGTH_LONG).show();
        }

        otherRecyclerView = findViewById(R.id.otherRecyclerView);
        otherRecyclerView.setHasFixedSize(true);
        otherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        otherRecyclerView.setAdapter(new MyAdapter(list2));

        otherRecyclerView.addOnItemTouchListener(new RecyclerViewOnItemClick(getApplicationContext(), otherRecyclerView, new RecyclerViewOnItemClick.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        Toast.makeText(FunctionalitiesActivity.this, "No Activity is defined", Toast.LENGTH_SHORT).show();
                        //intent = new Intent(getApplicationContext(), );
                        break;

                    case 1:
                        intent = new Intent(getApplicationContext(), FetchActivity.class);
                        break;

                    case 2:
                        intent = new Intent(getApplicationContext(), Settings.class);
                        break;

                    case 3:
                        intent = new Intent(getApplicationContext(), AddLocation.class);
                        break;

                    case 4:
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        break;
                }
                if (intent != null) {
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        openIndoorHintDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.userProfileActivity:
                intent = new Intent(getApplicationContext(), UserProfile.class);
                startActivity(intent);
                break;

            case R.id.signOutUser:
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Toast.makeText(this, "SignOut Successfully", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFirebaseReferences() {
        //setting Firebase indoor and Outdoor Reference
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(this);
        if (firebaseApp != null) {
            Toast.makeText(this, "FirebaseApp is NOT NULL", Toast.LENGTH_SHORT).show();
            firebaseDatabase = FirebaseDatabase.getInstance();
            getIndoorReference = FirebaseDatabase.getInstance(firebaseApp).getReference("LocationInfo").child("INDOOR");
            getoutdoorReference = FirebaseDatabase.getInstance(firebaseApp).getReference("LocationInfo").child("OUTDOOR");
        } else {
            Toast.makeText(this, "FirebaseApp is NULL", Toast.LENGTH_SHORT).show();
        }

        placeList = new ArrayList<>();
        roomList = new ArrayList<>();
    }

    private void showBluetoothEnableDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("App wants to turn Bluetooth On for the device");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                bluetoothAdapter.enable();
            }
        });
        builder.show();
    }

    void addItems() {
        listItems.add(new UserComponentListItem(R.drawable.bluetooth, "Connection"));
        listItems.add(new UserComponentListItem(R.drawable.binary_code, "Data"));
        listItems.add(new UserComponentListItem(R.drawable.analysis, "Graph"));
        listItems.add(new UserComponentListItem(R.drawable.route, "Map"));

        list2.add(new UserComponentListItem(R.drawable.forecast, "Forecast"));
        list2.add(new UserComponentListItem(R.drawable.fetch, "Fetch Old Data"));
        list2.add(new UserComponentListItem(R.drawable.settings, "Settings"));
        list2.add(new UserComponentListItem(R.drawable.add_location, "Add Location"));
        list2.add(new UserComponentListItem(R.drawable.dice, "Try Older Version"));
    }

    // Location Dialog Box
    private void openIndoorHintDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Indoor Location means only rooms/hall.\nNot the open places, like ground inside a college.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openLocationDialog();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void openLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.location_dialog, null);

        findLocationDialogIds(customLayout);

        placeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeType = "Place";
                //first set the progress Bar visible as if the button may be pressed again after selecting the room
                roomButton.setText("Show List");
                roomButton.setVisibility(View.GONE);
                roomProgressBar.setVisibility(View.VISIBLE);

                openPlaceOrRoomDialog(placeList, placeButton);
            }
        });

        roomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeType = "Room ID";
                openPlaceOrRoomDialog(roomList, roomButton);
            }
        });

        Button saveDetails = customLayout.findViewById(R.id.saveLocationDetails);
        saveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //save the location details in shared Preferences
                placeName = placeButton.getText().toString().trim();
                roomName = roomButton.getText().toString().trim();

                if (!setError()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("Location_Data", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Place_Type", locationtype);
                    editor.putString("Place_Name", placeName);
                    editor.putString("Room_Name", roomName);
                    editor.apply();

                    alertDialog1.dismiss();
                }
            }
        });

        builder.setView(customLayout);
        alertDialog1 = builder.create();
        alertDialog1.setCancelable(false);
        alertDialog1.setCanceledOnTouchOutside(false);
        alertDialog1.show();
    }

    private void findLocationDialogIds(View customLayout) {
        placeLayout = customLayout.findViewById(R.id.placeLayout);
        roomLayout = customLayout.findViewById(R.id.roomLayout);

        placeButton = customLayout.findViewById(R.id.placeButton);
        roomButton = customLayout.findViewById(R.id.roomButton);

        placeProgressBar = customLayout.findViewById(R.id.placeProgressBar);
        roomProgressBar = customLayout.findViewById(R.id.roomProgressBar);
    }

    private void readIndoorPlacesName() {
        getIndoorReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                placeList.clear();
                String tempKey = "";
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    tempKey = dataSnapshot1.getKey();
                    placeList.add(tempKey);
                }
                //set the button not touched while all the names don't fetched
                placeProgressBar.setVisibility(View.GONE);
                placeButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FunctionalitiesActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void readIndoorRoomNames(String place) {
        //add a progress bar for this also
        DatabaseReference indoorReference = getIndoorReference.child(place);
        indoorReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roomList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    LocationDataModel locationDataModel = dataSnapshot1.getValue(LocationDataModel.class);
                    roomList.add(locationDataModel.getRoomName());
                }
                roomProgressBar.setVisibility(View.GONE);
                roomButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FunctionalitiesActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openPlaceOrRoomDialog(List<String> useByList, final Button useByButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.location_dialogbox, null);

        dialogHeading = customLayout.findViewById(R.id.locationDailogHeading);
        dialogHeading.setText("Select a " + placeType);
        dialogListView = customLayout.findViewById(R.id.locationlistView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, useByList);
        dialogListView.setAdapter(adapter);
        dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String temp = adapterView.getItemAtPosition(i).toString();
                useByButton.setText(temp);
                useByButton.setTextColor(Color.BLACK);
                alertDialog.dismiss();
                if (placeType.equals("Place")) {
                    readIndoorRoomNames(temp);
                }
            }
        });

        builder.setView(customLayout);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private boolean setError() {
        if (placeName.equals("Show List") || placeName.equals("")) {
            placeButton.setError("Select a valid Place Name");
            placeButton.requestFocus();
            return true;
        }
        if (roomName.equals("Show List") || roomName.equals("")) {
            roomButton.setError("Select a valid Place Name");
            roomButton.requestFocus();
            return true;
        }
        return false;
    }

    public void checkPlaceType(View view) {
        switch (view.getId()) {
            case R.id.indoorTypeRadioButton:
                locationtype = "INDOOR";

                //set the place and room layout visibility acordingly
                placeLayout.setVisibility(View.VISIBLE);
                roomLayout.setVisibility(View.VISIBLE);

                //read the places Names
                readIndoorPlacesName();
                break;

            case R.id.outdoorTypeRadioButton:
                locationtype = "OUTDOOR";
                //first set the visibility of Place and Rooms
                placeLayout.setVisibility(View.GONE);
                roomLayout.setVisibility(View.GONE);
                break;
        }
    }
}
