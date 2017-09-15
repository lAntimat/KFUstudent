package com.kfu.lantimat.kfustudent.map;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kfu.lantimat.kfustudent.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by lantimat on 04.07.17.
 */

public class MapFragment extends SupportMapFragment
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, MapActivity.UpdateableFragment {



    GoogleMap mGoogleMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker mEnteredMarker;

    LatLng[] geopoint_buildings;
    String[] name_buildings;
    String[] address_buildings;
    LatLng[][] markersLatLng = new LatLng[7][];



    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addLatLng();

    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        super.onResume();
    }

    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EnterLatLngFragment.LatLongEvent event) {
        moveCamera(event.land, event.longt);
    }

    private void setUpMapIfNeeded() {

        if (mGoogleMap == null) {
            getMapAsync(this);
        }
    }

    public void setUpMarker(Float lat, Float lng) {

        //Ставим маркер по введенным значениям
        if(mGoogleMap!=null) {

            if (mEnteredMarker != null) mEnteredMarker.remove();
            LatLng latLng = new LatLng(lat, lng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Введеннная позиция");
            mEnteredMarker = mGoogleMap.addMarker(markerOptions);

            //move map camera
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(5));
        }
    }

    public void moveCamera(Float lat, Float lng) {

        LatLng latLng = new LatLng(lat, lng);
        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

    }

    public void addLatLng() {

        markersLatLng[0] = new LatLng[]{new LatLng(55.790843d, 49.121839d), new LatLng(55.790918d, 49.119216d), new LatLng(55.791714d, 49.119677d), new LatLng(55.791535d, 49.120342d), new LatLng(55.789713d, 49.12022d), new LatLng(55.789576d, 49.121503d), new LatLng(55.790305d, 49.119814d), new LatLng(55.791436d, 49.119374d), new LatLng(55.794076d, 49.114069d), new LatLng(55.793938d, 49.113179d), new LatLng(55.786625d, 49.126034d), new LatLng(55.761985d, 49.149218d), new LatLng(55.791835d, 49.117663d), new LatLng(55.792129d, 49.122164d), new LatLng(55.792456d, 49.122475d), new LatLng(55.792634d, 49.119939d), new LatLng(55.793326d, 49.143072d), new LatLng(55.79337d, 49.143328d), new LatLng(55.787961d, 49.112449d), new LatLng(55.784347d, 49.116633d), new LatLng(55.78995d, 49.108446d), new LatLng(55.787454d, 49.110386d), new LatLng(55.785283d, 49.118412d), new LatLng(55.789598d, 49.158523d), new LatLng(55.822101d, 49.098264d), new LatLng(55.793459d, 49.120393d), new LatLng(55.791571d, 49.122873d), new LatLng(55.789934d, 49.12185d), new LatLng(55.789928d, 49.120686d)};
        markersLatLng[1] = new LatLng[]{new LatLng(55.790867d, 49.121813d), new LatLng(55.794284d, 49.113565d), new LatLng(55.786513d, 49.126308d), new LatLng(55.791859d, 49.117667d), new LatLng(55.792468d, 49.122351d), new LatLng(55.79264d, 49.119869d), new LatLng(55.790139d, 49.16548d), new LatLng(55.792043d, 49.126202d), new LatLng(55.793858d, 49.143994d), new LatLng(55.787971d, 49.11244d), new LatLng(55.784356d, 49.116623d), new LatLng(55.787453d, 49.110399d)};
        markersLatLng[2] = new LatLng[]{new LatLng(55.791197d, 49.124089d), new LatLng(55.742509d, 49.122628d), new LatLng(55.793849d, 49.143935d), new LatLng(55.788928d, 49.108037d)};
        markersLatLng[3] = new LatLng[]{new LatLng(55.788399d, 49.164457d), new LatLng(55.789187d, 49.16511d), new LatLng(55.785532d, 49.163392d), new LatLng(55.790151d, 49.165469d), new LatLng(55.785439d, 49.170386d), new LatLng(55.78584d, 49.163399d), new LatLng(55.791145d, 49.126074d), new LatLng(55.79204d, 49.126202d), new LatLng(55.80524d, 49.189937d), new LatLng(55.786404d, 49.128802d)};
        markersLatLng[4] = new LatLng[]{new LatLng(55.790855d, 49.121786d), new LatLng(55.791185d, 49.124105d)};
        markersLatLng[5] = new LatLng[]{new LatLng(55.791568d, 49.122867d)};
        markersLatLng[6] = new LatLng[]{new LatLng(55.790843d, 49.121839d)};

    }

    private void loadInfo2(int position) {
        String childType = "";
        String city = "Набережые Челны";
        switch (position) {
            case 0:
                name_buildings = getResources().getStringArray(R.array.educational_buildings_chelny);
                address_buildings = getResources().getStringArray(R.array.address_educational_buildings_chelny);
                geopoint_buildings = markersLatLng[0];
                childType = "educationalBuilding";

                break;
            case 1:
                name_buildings = getResources().getStringArray(R.array.library_chelny);
                address_buildings = getResources().getStringArray(R.array.address_library_chelny);
                geopoint_buildings = markersLatLng[1];
                childType = "library";
                break;
            case 2:
                name_buildings = getResources().getStringArray(R.array.dorm_chelny);
                address_buildings = getResources().getStringArray(R.array.address_dorm_chelny);
                geopoint_buildings = markersLatLng[2];
                childType = "dorm";
                break;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("mapBuilds").child(childType);

        mGoogleMap.clear();
        for (int i = 0; i < name_buildings.length; i++) {
            showObject(name_buildings[i], address_buildings[i], markersLatLng[position][i]);
            // Write a message to the database

            String key = myRef.push().getKey();
            String lat = String.valueOf(geopoint_buildings[i].latitude);
            String lng = String.valueOf(geopoint_buildings[i].longitude);
            MapBuilds mapBuilds = new MapBuilds(name_buildings[i], address_buildings[i], city, childType, lat, lng);
            myRef.child(key).setValue(mapBuilds);
        }

    }

    private void updateInfo(ArrayList<MapBuilds> arrayList) {
        mGoogleMap.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            MapBuilds m = arrayList.get(i);
            showObject(m.getName(), m.getAddress(), new LatLng(Double.parseDouble(m.getLat()), Double.parseDouble(m.getLng())));
            // Write a message to the database
        }
    }


    public void showObject(String name, String address, LatLng coords) {
        mGoogleMap.addMarker(new MarkerOptions().position(coords).title(name).snippet(address));
    }


    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        /*//Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));*/

        //optionally, stop location updates if only current location is needed
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Требуется разрешение для доступа к местоположению")
                        .setMessage("Доступ разрешен")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Отказано в доступе к местоположению", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void update(ArrayList<MapBuilds> ar) {
        updateInfo(ar);
    }
}