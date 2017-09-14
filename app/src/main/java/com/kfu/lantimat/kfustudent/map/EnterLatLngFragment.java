package com.kfu.lantimat.kfustudent.map;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kfu.lantimat.kfustudent.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lantimat on 04.07.17.
 */

public class EnterLatLngFragment extends Fragment {

    SharedPreferences sharedpreferences;

    EditText edLat;
    EditText edLong;
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
        View rootView = inflater.inflate(R.layout.fragment_add_lat, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        edLat = (EditText) rootView.findViewById(R.id.edLat);
        edLong = (EditText) rootView.findViewById(R.id.edLong);
        Button btn = (Button) rootView.findViewById(R.id.btnAdd);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(edLat.getText())) {
                    Toast.makeText(getActivity(), "Проверьте правильность ввода широты", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(edLong.getText())) {
                    Toast.makeText(getActivity(), "Проверьте правильность ввода долготы", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Отправка данных в другой фрагмент
                EventBus.getDefault().post(new LatLongEvent(Float.parseFloat(edLat.getText().toString()), Float.parseFloat(edLong.getText().toString())));
            }
        });
        return rootView;
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
