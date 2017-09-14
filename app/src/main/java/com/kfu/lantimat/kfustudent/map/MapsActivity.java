package com.kfu.lantimat.kfustudent.map;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kfu.lantimat.kfustudent.MainActivity;
import com.kfu.lantimat.kfustudent.R;

public class MapsActivity extends MainActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng[][] geopoint_buildings;
    String[] name_buildings;
    String[] address_buildings;
    LatLng[][] markersLatLng = new LatLng[7][];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps);

        FrameLayout v = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_maps, v);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        addLatLng();
        initSpinner();
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
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        addLatLng();
        initSpinner();
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

    private void loadInfo(int position) {
        switch (position) {
            case 0:
                name_buildings = getResources().getStringArray(R.array.educational_buildings);
                address_buildings = getResources().getStringArray(R.array.address_educational_buildings);
                break;
            case 1:
                name_buildings = getResources().getStringArray(R.array.library);
                address_buildings = getResources().getStringArray(R.array.address_library);
                break;
        }


        for (int i = 0; i < name_buildings.length; i++) {
            showObject(name_buildings[i], address_buildings[i], markersLatLng[position][i]);
        }
    }

    public void showObject(String name, String address, LatLng coords) {
        mMap.addMarker(new MarkerOptions().position(coords).title(name).snippet(address));
    }

    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_list_item_array, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setVisibility(View.VISIBLE);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadInfo(i);
                switch (i) {
                    case 0:
                        //Toast.makeText(getApplicationContext(), "Pressed " + i, Toast.LENGTH_SHORT).show();

                        break;
                    case 1:

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


}
