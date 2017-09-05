package com.kfu.lantimat.kfustudent.Schedule;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.kfu.lantimat.kfustudent.KFURestClient;
import com.kfu.lantimat.kfustudent.Marks.Mark;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.SharedPreferenceHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ScheduleActivity extends AppCompatActivity {

    ArrayList<Mark> arBlock;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;
    String scheduleUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        arBlock = new ArrayList<>();

        //initViewPager();

        scheduleUrl = SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(), "scheduleUrl", "");

        getMarks();
    }




    private void getMarks() {

        KFURestClient.get("student_personal_main.shedule?" + scheduleUrl + "&p_menu=1&p_type_menu=3", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                new ParseMarks().execute(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public class ParseMarks extends AsyncTask<byte[], Void, Void> {
        int count;

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
            //Log.d("docToString", doc.toString());

            Elements courses = doc.select("div.big_td");

            Log.d("div.big_td", courses.toString());

          //  if(SharedPreferenceHelper.getSharedPreferenceInt(getApplicationContext(), "count", -1) == -1) {
                adapter = new ViewPagerAdapter(getSupportFragmentManager());

                adapter.addFragment(new ScheduleFragment().newInstance(courses.get(0).toString()), "Понедельник");
                adapter.addFragment(new ScheduleFragment().newInstance(courses.get(1).toString()), "Вторник");
                adapter.addFragment(new ScheduleFragment().newInstance(courses.get(2).toString()), "Среда");
                adapter.addFragment(new ScheduleFragment().newInstance(courses.get(3).toString()), "Четверг");
                adapter.addFragment(new ScheduleFragment().newInstance(courses.get(4).toString()), "Пятница");
                adapter.addFragment(new ScheduleFragment().newInstance(courses.get(5).toString()), "Суббота");
                adapter.addFragment(new ScheduleFragment().newInstance(courses.get(6).toString()), "Воскресенье");

           // }

            //SharedPreferenceHelper.setSharedPreferenceInt(getApplicationContext(), "count", count);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //feedsRecyclerAdapter.notifyDataSetChanged();
            //progressBar.setVisibility(View.INVISIBLE);

            viewPager.setOffscreenPageLimit(count);
            viewPager.setAdapter(adapter);
            super.onPostExecute(aVoid);
        }
    }

    private void initViewPager() {

        /*int count = SharedPreferenceHelper.getSharedPreferenceInt(getApplicationContext(), "count", -1);
        if(count != -1) {
            adapter = new ViewPagerAdapter(getSupportFragmentManager());
            for (int i = 1; i < count - 1; i++) {
                adapter.addFragment(new MarksFragment().newInstance(""), i + " курс");
            }

            viewPager.setOffscreenPageLimit(count);
            viewPager.setAdapter(adapter);
        }
        getMarks();*/

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
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
