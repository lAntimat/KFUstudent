package com.kfu.lantimat.kfustudent.Schedule;

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

import com.kfu.lantimat.kfustudent.KFURestClient;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.SharedPreferenceHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;


public class ScheduleFragment extends Fragment implements
        ScheduleActivity.UpdateableFragment {

    private final String ARG_PARAM1 = "param1";

    RecyclerView recyclerView;
    ScheduleRecyclerAdapter scheduleRecyclerAdapter;
    ArrayList<String> arBlock = new ArrayList<>();
    ArrayList<Schedule> arSchedule;
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




    public ScheduleFragment() {
        // Required empty public constructor
    }

    public ScheduleFragment newInstance(int day) {

        ScheduleFragment fragment = new ScheduleFragment();
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

        arSchedule = new ArrayList<>();
        scheduleRecyclerAdapter = new ScheduleRecyclerAdapter(arSchedule);
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

        //parseScheduleFromString(string);
        /*String marksCashStr = SharedPreferenceHelper.getSharedPreferenceString(getContext(), "marks" + string, "-1"); //Достаем из памяти строку с успеваемостью;
        if (!marksCashStr.equalsIgnoreCase("-1")) getScheduleFromCash(marksCashStr);
        getSchedule();*/

        return v;

    }

    @Override
    public void update(String xyzData, int day) {
        if(this.day==day) parseScheduleFromString(xyzData);
    }

    private void parseScheduleFromString(String str) {
        ArrayList<Schedule> arScheduleTemp = new ArrayList<>();
        Schedule schedule;

        /*Pattern datePattern = Pattern.compile(">(.*) <\\/font>");
        Matcher dateMatcher = datePattern.matcher(str.replaceAll("", ""));
        if (dateMatcher.find()) schedule.setDate(dateMatcher.group(1));*/


        Pattern schedulePattern = Pattern.compile("nowrap> (.*)<\\/td>|<td class=\"table_td\" width=\"180\" style=\"\"> (.*)<\\/td>|<td class=\"table_td\">(.*)<\\/td>");
        Matcher sheduleMatcher = schedulePattern.matcher(str);

        while (sheduleMatcher.find()) {
            schedule = new Schedule();
            schedule.setTime(sheduleMatcher.group(1));
            if (sheduleMatcher.find()) schedule.setSubjectName(sheduleMatcher.group(2));
            if (sheduleMatcher.find()) schedule.setPlace(sheduleMatcher.group(3));
            arScheduleTemp.add(schedule);
        }

        arSchedule.clear();
        arSchedule.addAll(arScheduleTemp);

        progressBar.setVisibility(View.INVISIBLE);
        scheduleRecyclerAdapter.notifyDataSetChanged();
        recyclerView.invalidate();

        if(arSchedule.size() == 0) emptyPic();

    }

    private void onPreExecuteMethod() {
        if(arSchedule.isEmpty()) progressBar.setVisibility(View.VISIBLE);
    }

    private void onPostExecuteMethod() {
        progressBar.setVisibility(View.INVISIBLE);
        scheduleRecyclerAdapter.notifyDataSetChanged();
        emptyPic();
    }

    private void emptyPic() {
        if (arSchedule.size() == 0) {
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

    public class ParseSchedule extends AsyncTask<byte[], Void, Void> {

        @Override
        protected void onPreExecute() {
            onPreExecuteMethod();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(byte[]... params) {

            String str = null;
            try {

                //str = new String(params[0], "UTF-8");
                str = new String(params[0], "windows-1251");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            parseScheduleFromString(str);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            //feedsRecyclerAdapter.notifyDataSetChanged();
            //progressBar.setVisibility(View.INVISIBLE);
            onPostExecuteMethod();
            super.onPostExecute(aVoid);
        }
    }

    public class LoadScheduleFromCash extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            onPreExecuteMethod();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            //Log.d("MainActivity", "ParseFeed");

            String str = null;
            str = String.valueOf(params[0]);
            parseScheduleFromString(str);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //feedsRecyclerAdapter.notifyDataSetChanged();
            //progressBar.setVisibility(View.INVISIBLE);
            onPostExecuteMethod();
            super.onPostExecute(aVoid);
        }
    }

}
