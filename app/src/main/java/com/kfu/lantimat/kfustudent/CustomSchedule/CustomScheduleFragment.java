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
import android.widget.TextView;

import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Day;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.Schedule.ScheduleRecyclerAdapter;

import java.util.ArrayList;


public class CustomScheduleFragment extends Fragment implements
        CustomScheduleActivity.UpdateableFragment {

    private final String ARG_PARAM1 = "param1";

    RecyclerView recyclerView;
    ScheduleRecyclerAdapter scheduleRecyclerAdapter;
    ArrayList<String> arBlock = new ArrayList<>();
    ArrayList<Subject> arSubjects;
    int day;
    //@BindView(R.id.textView)
    TextView textView;
    //@BindView(R.id.imageView)
    ImageView imageView;
    //Unbinder unbinder;
    //@BindView(R.id.progressBar)
    ProgressBar progressBar;
    AsyncTask<byte[], Void, Void> parseSchedule;
    AsyncTask<String, Void, Void> loadScheduleFromCash;




    public CustomScheduleFragment() {
        // Required empty public constructor
    }

    public CustomScheduleFragment newInstance(int day) {

        CustomScheduleFragment fragment = new CustomScheduleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, day);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            day = getArguments().getInt(ARG_PARAM1);
        }

        arSubjects = new ArrayList<>();
        scheduleRecyclerAdapter = new ScheduleRecyclerAdapter(arSubjects);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));
        recyclerView.setAdapter(scheduleRecyclerAdapter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_schedule, null);
        //unbinder = ButterKnife.bind(this, v);

        textView = v.findViewById(R.id.textView);
        imageView = v.findViewById(R.id.imageView);
        progressBar = v.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        initRecyclerView();

        //addDataToAdapter(string);
        /*String marksCashStr = SharedPreferenceHelper.getSharedPreferenceString(getContext(), "marks" + string, "-1"); //Достаем из памяти строку с успеваемостью;
        if (!marksCashStr.equalsIgnoreCase("-1")) getScheduleFromCash(marksCashStr);
        getSchedule();*/

        return v;

    }

    @Override
    public void update(Day day, int dayNumber) {
        if(this.day==dayNumber) addDataToAdapter(day.getSubjects());
    }

    private void addDataToAdapter(ArrayList<Subject> arSubjects) {

        this.arSubjects.clear();
        this.arSubjects.addAll(arSubjects);

        progressBar.setVisibility(View.INVISIBLE);
        scheduleRecyclerAdapter.notifyDataSetChanged();
        recyclerView.invalidate();

        emptyPic();

    }

    private void emptyPic() {
        if (arSubjects.size() == 0) {
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        //if(loadScheduleFromCash!=null) loadScheduleFromCash.cancel(true);
        //if(parseSchedule!=null) parseSchedule.cancel(true);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("ScheduleFragment", "destroyVIew");
        //unbinder.unbind();
    }



}
