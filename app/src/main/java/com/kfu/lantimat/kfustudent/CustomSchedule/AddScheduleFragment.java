package com.kfu.lantimat.kfustudent.CustomSchedule;

/**
 * Created by GabdrakhmanovII on 04.09.2017.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.Schedule.Schedule;
import com.kfu.lantimat.kfustudent.Schedule.ScheduleActivity;
import com.kfu.lantimat.kfustudent.Schedule.ScheduleRecyclerAdapter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddScheduleFragment extends Fragment implements
        ScheduleActivity.UpdateableFragment {

    private final String ARG_PARAM1 = "param1";

    RecyclerView recyclerView;
    ScheduleRecyclerAdapter scheduleRecyclerAdapter;

    public AddScheduleFragment() {
        // Required empty public constructor
    }

    public AddScheduleFragment newInstance(int day) {

        AddScheduleFragment fragment = new AddScheduleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, day);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add_schedule_options, null);
        initRadioGroup(v);
        initRadioGroupWeek(v);
        return v;

    }

    private void initRadioGroup(View view) {
        final AddScheduleActivity customScheduleActivity = ((AddScheduleActivity) getActivity());

        RadioGroup radioGroup = view.findViewById(R.id.RGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.Pn) {
                   customScheduleActivity.repeatDay = CustomScheduleConstants.MONDAY;
                } else if(checkedId == R.id.Bt) {
                    customScheduleActivity.repeatDay = CustomScheduleConstants.TUESDAY;
                } else if(checkedId == R.id.Cp){
                    customScheduleActivity.repeatDay = CustomScheduleConstants.WEDNESDAY;
                } else if(checkedId == R.id.Cht){
                    customScheduleActivity.repeatDay = CustomScheduleConstants.THURSDAY;
                } else if(checkedId == R.id.Pt){
                    customScheduleActivity.repeatDay = CustomScheduleConstants.FRIDAY;
                } else if(checkedId == R.id.Sb){
                    customScheduleActivity.repeatDay = CustomScheduleConstants.SATURDAY;
                } else if(checkedId == R.id.Bs){
                    customScheduleActivity.repeatDay = CustomScheduleConstants.SUNDAY;
                }
            }

        });
    }

    private void initRadioGroupWeek(View view) {
        final AddScheduleActivity customScheduleActivity = ((AddScheduleActivity) getActivity());

        RadioGroup radioGroup = view.findViewById(R.id.RGroupRepeatType);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.all) {
                    customScheduleActivity.repeatWeek = CustomScheduleConstants.ALL_WEEK;
                } else if(checkedId == R.id.evenWeek) {
                    customScheduleActivity.repeatWeek = CustomScheduleConstants.EVEN_WEEK;
                } else if(checkedId == R.id.oddWeek){
                    customScheduleActivity.repeatWeek = CustomScheduleConstants.ODD_WEEK;
                }
            }

        });
    }

    @Override
    public void update(String xyzData, int day) {
    }


    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



}
