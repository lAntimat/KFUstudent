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
import com.kfu.lantimat.kfustudent.Marks.Mark;
import com.kfu.lantimat.kfustudent.Marks.MarksRecyclerAdapter;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.SharedPreferenceHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;


public class ScheduleFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    RecyclerView recyclerView;
    ScheduleRecyclerAdapter scheduleRecyclerAdapter;
    ArrayList<String> arBlock = new ArrayList<>();
    ArrayList<Schedule> arSchedule;
    String course = "";
    @BindView(R.id.textView2)
    TextView textView;
    @BindView(R.id.imageView)
    ImageView imageView;
    Unbinder unbinder;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance(String str) {
        ScheduleFragment fragment = new ScheduleFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, str);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            course = getArguments().getString(ARG_PARAM1);
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

        View v = inflater.inflate(R.layout.fragment_marks, null);
        unbinder = ButterKnife.bind(this, v);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        initRecyclerView();

        parseMarksFromString(course);
        /*String marksCashStr = SharedPreferenceHelper.getSharedPreferenceString(getContext(), "marks" + course, "-1"); //Достаем из памяти строку с успеваемостью;
        if (!marksCashStr.equalsIgnoreCase("-1")) getMarksFromCash(marksCashStr);
        getMarks();*/

        return v;

    }

    private void getMarks() {
        KFURestClient.get("SITE_STUDENT_SH_PR_AC.score_list_book_subject?p_menu=7&p_course=" + course, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                new ParseMarks().execute(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void getMarksFromCash(String str) {
        new LoadMarksFromCash().execute(str);
    }

    private void parseMarksFromString(String str) {
        ArrayList<Schedule> arScheduleTemp = new ArrayList<>();

        arScheduleTemp.add(new Schedule(str));

        arSchedule.clear();
        arSchedule.addAll(arScheduleTemp);


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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public class ParseMarks extends AsyncTask<byte[], Void, Void> {

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

            SharedPreferenceHelper.setSharedPreferenceString(getContext(), "marks" + course, str);
            parseMarksFromString(str);

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

    public class LoadMarksFromCash extends AsyncTask<String, Void, Void> {

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
            parseMarksFromString(str);

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
