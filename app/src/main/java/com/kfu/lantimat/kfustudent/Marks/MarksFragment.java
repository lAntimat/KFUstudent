package com.kfu.lantimat.kfustudent.Marks;

/**
 * Created by GabdrakhmanovII on 04.09.2017.
 */

import android.content.Context;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;


public class MarksFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    RecyclerView recyclerView;
    MarksRecyclerAdapter marksRecyclerAdapter;
    ArrayList<String> arBlock = new ArrayList<>();
    ArrayList<Mark> arMarks;
    String course = "";
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.imageView)
    ImageView imageView;
    Unbinder unbinder;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    AsyncTask<byte[], Void, Void> parseMarks;
    boolean isStopped = false;
    Context context;

    public MarksFragment() {
        // Required empty public constructor
    }

    public static MarksFragment newInstance(int course) {
        MarksFragment fragment = new MarksFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, String.valueOf(course));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            course = getArguments().getString(ARG_PARAM1);
        }

        context = getContext();
        arMarks = new ArrayList<>();
        marksRecyclerAdapter = new MarksRecyclerAdapter(arMarks);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));
        recyclerView.setAdapter(marksRecyclerAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_marks, null);
        unbinder = ButterKnife.bind(this, v);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        initRecyclerView();

        getMarks();

        return v;

    }

    private void getMarks() {
        if(arMarks.isEmpty()) progressBar.setVisibility(View.VISIBLE);
        String marksCashStr = SharedPreferenceHelper.getSharedPreferenceString(getContext(), "marks" + course, "-1"); //Достаем из памяти строку с успеваемостью;
        if (!marksCashStr.equalsIgnoreCase("-1")) getMarksFromCash(marksCashStr);
        KFURestClient.get("SITE_STUDENT_SH_PR_AC.score_list_book_subject?p_menu=7&p_course=" + course, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                parseMarks = new ParseMarks().execute(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void getMarksFromCash(String str) {
        parseMarksFromString(str);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void parseMarksFromString(String str) {
        ArrayList<Mark> arMarksTemp = new ArrayList<>();

        Document doc = Jsoup.parse(str);
        Log.d("docToString", doc.toString());
        Elements marksBlockBody = doc.select("tbody");  //Тело баллов/курсовых/рейтингов
        Elements marksBlockHeader = doc.select("thead"); //Заголовок блока с баллами/курсовыми/рейтингом
        int scopeType = -1;

        for (int i = 0; i < marksBlockHeader.size(); i++) {
            Elements marksBlock = marksBlockBody.get(i).select("tr");
            Pattern pattern = Pattern.compile(">(.*)<\\/");
            Matcher matcher = pattern.matcher(marksBlockHeader.get(i).toString());
            while (matcher.find()) {
                if (matcher.group(1).equalsIgnoreCase("Семестровый рейтинг"))
                    scopeType = Mark.RATING_TYPE;
                if (matcher.group(1).equalsIgnoreCase("Дисциплина")) scopeType = Mark.SCORE_TYPE;
                if (matcher.group(1).equalsIgnoreCase("Практики")) scopeType = Mark.PRACTICE_TYPE;
                if (matcher.group(1).equalsIgnoreCase("Курсовые")) scopeType = Mark.COURSEWORK_TYPE;
            }
            for (int j = 0; j < marksBlock.size(); j++) {
                if (!marksBlock.get(j).toString().equalsIgnoreCase("<tr> \n" +
                        "</tr>")) {

                    Mark mark = new Mark(scopeType, marksBlock.get(j).toString());
                    arMarksTemp.add(mark);

                    Collections.sort(arMarksTemp, Mark.COMPARE_BY_SEMESTER);

                    Log.d("MarksActivity", mark.getTestString());
                }
            }
        }

        arMarks.clear();
        arMarks.addAll(arMarksTemp);


    }

    private void onPreExecuteMethod() {
        //if(arMarks.isEmpty()) progressBar.setVisibility(View.VISIBLE);
    }

    private void onPostExecuteMethod() {
        if(progressBar!=null) progressBar.setVisibility(View.INVISIBLE);
        marksRecyclerAdapter.notifyDataSetChanged();
        emptyPic();
    }

    private void emptyPic() {
        if(imageView!=null & textView!=null) {
            if (arMarks.size() == 0) {
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                Calendar calendar = Calendar.getInstance();
                Date dateCalendar = calendar.getTime();
                String full = new SimpleDateFormat("hh:mm").format(dateCalendar);
                textView.setText("Погоди-ка… минуточку, Док! Сейчас что, " + full + "?");

            } else {
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPause() {
        if(parseMarks!=null) parseMarks.cancel(true);
        super.onPause();
    }

    @Override
    public void onStop() {
        if(parseMarks!=null) parseMarks.cancel(true);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(parseMarks!=null) parseMarks.cancel(true);
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

            SharedPreferenceHelper.setSharedPreferenceString(context, "marks" + course, str);
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
