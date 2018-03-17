package com.kfu.lantimat.kfustudent.Schedule;

/**
 * Created by GabdrakhmanovII on 04.09.2017.
 */

import android.content.Intent;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kfu.lantimat.kfustudent.CustomSchedule.AddScheduleActivity;
import com.kfu.lantimat.kfustudent.CustomSchedule.CustomScheduleConstants;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;
import com.kfu.lantimat.kfustudent.ItemClickSupport;
import com.kfu.lantimat.kfustudent.KFURestClient;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.SharedPreferenceHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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

        scheduleRecyclerAdapter.setOnDotsClickListener(new ScheduleRecyclerAdapter.OnDotsClickListener() {
            @Override
            public void onClick(int position) {
                showImportOptionsDialog(position);
            }
        });


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

    private void showImportOptionsDialog(final int position) {
        new MaterialDialog.Builder(getActivity())
                .items(R.array.dialog_list_import)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        subjectImportIntent(position);
                    }
                })
                .show();
    }

    private void subjectImportIntent(int position) {
        Schedule schedule = arSchedule.get(position);
        String startStr = schedule.getTime().substring(0, 5);
        String endStr = schedule.getTime().replace(startStr + "-", "");

        SimpleDateFormat sf = new SimpleDateFormat("HH:mm", Locale.UK);

        Date startTime = new Date();
        Date endTime = new Date();

        try {
            startTime = sf.parse(startStr);
            endTime = sf.parse(endStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String cab = schedule.getPlace();
        cab = cab.substring(0, cab.indexOf(" "));
        String campus = schedule.getPlace().replace(cab + " ", "");

        Subject subject = new Subject(startTime, endTime, null, null, schedule.getSubjectName(), "", campus, cab, "", day, 0);

        Intent intent = new Intent(getContext(), AddScheduleActivity.class);
        intent.putExtra(CustomScheduleConstants.SUBJECT_MODEL, subject);
        intent.putExtra(CustomScheduleConstants.IS_IMPORT, true);
        intent.putExtra("isOffline", false);
        startActivity(intent);
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
