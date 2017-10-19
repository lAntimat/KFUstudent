package com.kfu.lantimat.kfustudent.Marks;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.kfu.lantimat.kfustudent.KFURestClient;
import com.kfu.lantimat.kfustudent.LoginActivity;
import com.kfu.lantimat.kfustudent.MainActivity;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.SharedPreferenceHelper;
import com.kfu.lantimat.kfustudent.utils.CheckAuth;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MarksActivity extends MainActivity {

    public static final String COURSES_COUNT = "count";


    ArrayList<Mark> arBlock;
    //@BindView(R.id.textView)
    TextView textView;
    //@BindView(R.id.btnSign)
    Button button;
    //@BindView(R.id.progressBar)
    ProgressBar progressBar;
    //private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;
    int count = -1;
    AsyncTask<byte[], Void, Void> parseMarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_marks);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FrameLayout v = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_marks, v);

        //ButterKnife.bind(this);

        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.btnSign);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        textView.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        toolbar.setTitle("Успеваемость");

        arBlock = new ArrayList<>();

        initViewPager();
        result.setSelection(3, false);
    }


    public void showNeedLogin() {
        textView.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void getMarks() {
        if(count!=-1) progressBar.setVisibility(View.INVISIBLE);

        KFURestClient.get("SITE_STUDENT_SH_PR_AC.score_list_book_subject?p_menu=7", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                parseMarks = new ParseMarks().execute(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public class ParseMarks extends AsyncTask<byte[], Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(byte[]... params) {

            //Log.d("MainActivity", "ParseFeed");

            String str = null;
            try {

                //str = new String(params[0], "UTF-8");
                str = new String(params[0], "windows-1251");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Document doc = Jsoup.parse(str);
            Log.d("docToString", doc.toString());
            FirebaseCrash.report(new Exception("ParseMarks-docToString" + doc.toString()));

            Elements courses = doc.select("div.courses");
            count = courses.toString().split("</span>").length;
            FirebaseCrash.report(new Exception("getMarks-coursesCount" + count));


            if(SharedPreferenceHelper.getSharedPreferenceInt(getApplicationContext(), COURSES_COUNT, -1) == -1) {
                adapter = new ViewPagerAdapter(getSupportFragmentManager());
                for (int i = 1; i < count - 1; i++) {
                    adapter.addFragment(new MarksFragment().newInstance(i), i + " курс");
                }
            }
            int savedCoursesCount = SharedPreferenceHelper.getSharedPreferenceInt(getApplicationContext(), COURSES_COUNT, -1);
            if(count!=-1 && savedCoursesCount <= count)
                SharedPreferenceHelper.setSharedPreferenceInt(getApplicationContext(), COURSES_COUNT, count);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //feedsRecyclerAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
            if(count!=-1) {
                viewPager.setOffscreenPageLimit(count);
                viewPager.setAdapter(adapter);
            }
            super.onPostExecute(aVoid);
        }
    }

    private void initViewPager() {


        if (CheckAuth.isAuth()) {

            count = SharedPreferenceHelper.getSharedPreferenceInt(getApplicationContext(), COURSES_COUNT, -1);
            if(count != -1) {
                progressBar.setVisibility(View.INVISIBLE);
                adapter = new ViewPagerAdapter(getSupportFragmentManager());
                for (int i = 1; i < count - 1; i++) {
                    adapter.addFragment(new MarksFragment().newInstance(i), i + " курс");
                }

                viewPager.setOffscreenPageLimit(count);
                viewPager.setAdapter(adapter);
            }
            getMarks();
        }
        else showNeedLogin();


    }

    @Override
    protected void onStop() {
        if(parseMarks!=null) parseMarks.cancel(true);
        super.onStop();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

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

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
