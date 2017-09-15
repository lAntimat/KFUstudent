package com.kfu.lantimat.kfustudent.map;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kfu.lantimat.kfustudent.BuildConfig;
import com.kfu.lantimat.kfustudent.MainActivity;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.Schedule.ScheduleActivity;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends MainActivity {

    final String EDUCATION = "educationalBuilding";
    final String LIBRARY = "library";
    final String DORM = "dorm";
    final String CITY_KAZAN = "Казань";
    final String CITY_CHELNY = "Набережные Челны";
    final String CITY_ELABUGA = "Елабуга";
    final String CITY_CHISTOPOL = "Чистополь";

    LatLng[][] geopoint_buildings;
    String[] name_buildings;
    String[] address_buildings;
    LatLng[][] markersLatLng = new LatLng[7][];

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private FloatingActionButton fab;
    EnterLatLngFragment addLatFragment;
    MapFragment mapFragment;
    float lat;
    float longt;
    String selectedCity = CITY_KAZAN;

    ArrayList<MapBuilds> arrayList = new ArrayList<>();
    DatabaseReference mapBuildsRef;

    public interface UpdateableFragment {
        public void update(ArrayList<MapBuilds> ar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout v = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_map, v);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateDialog(MapActivity.this).show();
            }
        });

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



        initSpinner();
        initViewPager();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mapBuildsRef = database.getReference().child("mapBuilds");
    }

    private void initSpinner() {
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_list_map_activity, R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setVisibility(View.VISIBLE);
        spinner.setAdapter(arrayAdapter);

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
    public void initViewPager() {
        viewPager.setOffscreenPageLimit(7);

        adapter.addFragment(new EnterLatLngFragment(), "Список");
        adapter.addFragment(new MapFragment(), "Карта");

        adapter.notifyDataSetChanged();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position==0) fab.show();
                else fab.hide();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }

    private void loadInfo(final int position) {
        String type = "";
        switch (position) {
            case 0:
                type = EDUCATION;
                break;
            case 1:
                type = LIBRARY;
                break;
            case 2:
                type = DORM;
                break;

        }

        final String finalType = type;
        mapBuildsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();

                for (DataSnapshot snapshot: dataSnapshot.child(finalType).getChildren()) {
                    MapBuilds mapBuilds = snapshot.getValue(MapBuilds.class);
                    if(mapBuilds.getCity().equals(selectedCity))
                    arrayList.add(mapBuilds);
                }

                adapter.update(arrayList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public Dialog onCreateDialog(final Context context) {
        String text = "Выберите город";
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(text)
                .setItems(R.array.dialog_list_cities, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                selectedCity = CITY_KAZAN;
                                break;
                            case 1:
                                selectedCity = CITY_CHELNY;
                                break;
                            case 2:
                                selectedCity = CITY_ELABUGA;
                                break;
                            case 3:
                                selectedCity = CITY_CHISTOPOL;
                                break;
                        }

                        loadInfo(spinner.getSelectedItemPosition());
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.showHome) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private ArrayList<MapBuilds> mapBuildses;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public void clear() {
            mFragmentList.clear();
            mFragmentTitleList.clear();
        }

        //call this method to update fragments in ViewPager dynamically
        public void update(ArrayList<MapBuilds> mapBuildses) {
            this.mapBuildses = mapBuildses;
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof MapActivity.UpdateableFragment) {
                ((MapActivity.UpdateableFragment) object).update(mapBuildses);
            }
            //don't return POSITION_NONE, avoid fragment recreation.
            return super.getItemPosition(object);
        }



        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MapFragment.MY_PERMISSIONS_REQUEST_LOCATION){
            MapFragment mapFragment = (MapFragment) adapter.mFragmentList.get(1);
            if (mapFragment != null) {
                mapFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
