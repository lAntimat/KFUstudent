package com.kfu.lantimat.kfustudent.map;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kfu.lantimat.kfustudent.ItemClickSupport;
import com.kfu.lantimat.kfustudent.Marks.MarksRecyclerAdapter;
import com.kfu.lantimat.kfustudent.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by lantimat on 04.07.17.
 */

public class EnterLatLngFragment extends Fragment implements MapActivity.UpdateableFragment {

    SharedPreferences sharedpreferences;

    RecyclerView recyclerView;
    MapRecyclerAdapter mapRecyclerAdapter;
    ArrayList<MapBuilds> arrayList;
    public EnterLatLngFragment() {
    }


    public static EnterLatLngFragment newInstance() {
        EnterLatLngFragment fragment = new EnterLatLngFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_lat, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);

        return v;
    }

    @Override
    public void update(ArrayList<MapBuilds> ar) {
        arrayList = ar;
        mapRecyclerAdapter = new MapRecyclerAdapter(ar);
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));
        recyclerView.setAdapter(mapRecyclerAdapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                //Отправка данных в другой фрагмент
                EventBus.getDefault().post(new LatLongEvent(Float.parseFloat(arrayList.get(position).getLat()), Float.parseFloat(arrayList.get(position).getLng())));
            }
        });
    }

    //Класс с описанием данных для EventBus
    public class LatLongEvent {

        public final float land;
        public final float longt;

        public LatLongEvent(Float land, float longt) {
           this.land = land;
            this.longt = longt;
        }
    }
}
