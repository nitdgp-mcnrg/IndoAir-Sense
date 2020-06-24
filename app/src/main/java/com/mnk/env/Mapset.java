package com.mnk.env;

import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.location.Location;
import android.os.Looper;
import android.os.StrictMode;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.text.DateFormat;
import java.util.Date;

public class Mapset extends AppCompatActivity {

    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;

    private double currentLatitude;
    private double currentLongitude;
    MapView map;
    IMapController mapController;
    // location last updated time
    private String mLastUpdateTime;
    Marker marker;
    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;
    GeoPoint startPoint;

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    TextView tv1;
    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    private String TAG="mapset";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapset);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(15);
        marker=new Marker(map);


        startPoint = new GeoPoint(23.548512, 87.2894873);
        mapController.setCenter(startPoint);

        marker.setPosition(startPoint);
        //startMarker.setIcon(getResources().getDrawable(android.R.drawable.));

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);


        init();


        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);
        startLocationUpdates();



    }

    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }


    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            map.getOverlays().clear();
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(marker);

            marker.setPosition(new GeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));

            // Toast.makeText(this, mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

        }
    }

    private void init()
    {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                //
                // Toast.makeText(Mapset.this, "" + mCurrentLocation.getLatitude() + "" + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                //  tv1.setText("" + mCurrentLocation.getLatitude() + "" + mCurrentLocation.getLongitude());
            }

        };




        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {

                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(Mapset.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(Mapset.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

}
