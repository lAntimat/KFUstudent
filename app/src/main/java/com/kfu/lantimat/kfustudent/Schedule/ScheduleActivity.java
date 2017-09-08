package com.kfu.lantimat.kfustudent.Schedule;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kfu.lantimat.kfustudent.KFURestClient;
import com.kfu.lantimat.kfustudent.MainActivity;
import com.kfu.lantimat.kfustudent.Marks.Mark;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.SharedPreferenceHelper;
import com.kfu.lantimat.kfustudent.utils.CheckAuth;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ScheduleActivity extends MainActivity {

    final String EVEN_WEEK = "evenWeek";
    final String ODD_WEEK = "oddWeek";

    ArrayList<Mark> arBlock;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.btnSign)
    Button buttonSignEmpty;
    @BindView(R.id.textView)
    TextView textViewEmpty;
    //Spinner spinner;

    //private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;
    String scheduleUrl;
    String nowWeek;
    int dayOfWeek;
    int selectedDayOfWeek = -1;

    public static enum Week {
        EVEN, ODD
    }

    public static Week week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_schedule);

        FrameLayout v = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_schedule, v);

        ButterKnife.bind(this);
        textViewEmpty.setVisibility(View.INVISIBLE);
        buttonSignEmpty.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("");

        //spinner = (Spinner) v.findViewById(R.id.spinner_nav);

        nowWeek = isEvenOrOddWeek();
        dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        if (CheckAuth.isAuth()) initSpinner();
        else showNeedLogin();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        //viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectedDayOfWeek = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        arBlock = new ArrayList<>();

        //initViewPager();

        scheduleUrl = SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(), "scheduleUrl", "");



        result.setSelection(3, false);
        //getScheduleOddWeek();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Menu
        switch (item.getItemId()) {
            //When home is clicked
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String isEvenOrOddWeek() {
        Calendar calendar = Calendar.getInstance();

        if ((calendar.get(Calendar.WEEK_OF_YEAR) & 1) == 0) {
            return EVEN_WEEK;
        } else {
            return ODD_WEEK;
        }
    }

    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_list_item_array, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setVisibility(View.VISIBLE);
        spinner.setAdapter(adapter);
        if(nowWeek.equals(EVEN_WEEK)) spinner.setSelection(0);
        else spinner.setSelection(1);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        //Toast.makeText(getApplicationContext(), "Pressed " + i, Toast.LENGTH_SHORT).show();
                        getScheduleEvenWeek();
                        break;
                    case 1:
                        getScheduleOddWeek();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void showNeedLogin() {
        progressBar.setVisibility(View.INVISIBLE);
        textViewEmpty.setVisibility(View.VISIBLE);
        buttonSignEmpty.setVisibility(View.VISIBLE);
        toolbar.setTitle("Расписание");
    }

    public void onFailureMethod() {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), "Ошибка соединения", Toast.LENGTH_SHORT).show();
    }
    private void getScheduleOddWeek() {
        progressBar.setVisibility(View.VISIBLE);
        final String week = SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(), ODD_WEEK, "");

        if (!week.isEmpty()) setScheduleToViewPager(week, ODD_WEEK);

        KFURestClient.get("student_personal_main.shedule?" + scheduleUrl + "&p_page=0&p_date=13.09.2017&p_id=uch", null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    //new ParseSchedule().execute(responseBody);
                    String str = null;
                    try {
                        //str = new String(params[0], "UTF-8");
                        str = new String(responseBody, "windows-1251");
                        setScheduleToViewPager(str, ODD_WEEK);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (week.isEmpty()) onFailureMethod();
                }
            });
    }

    private void getScheduleEvenWeek() {
        progressBar.setVisibility(View.VISIBLE);
        final String week = SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(), EVEN_WEEK, "");
        if (!week.isEmpty()) setScheduleToViewPager(week, EVEN_WEEK);

        KFURestClient.get("student_personal_main.shedule?" + scheduleUrl + "&p_page=0&p_date=20.09.2017&p_id=uch", null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    //new ParseSchedule().execute(responseBody);
                    String str = null;
                    try {
                        //str = new String(params[0], "UTF-8");
                        str = new String(responseBody, "windows-1251");
                        setScheduleToViewPager(str, EVEN_WEEK);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (week.isEmpty()) onFailureMethod();

                }
            });
    }

    private void setScheduleToViewPager(String str, String week) {

        Document doc = Jsoup.parse(str);
        //Log.d("docToString", doc.toString());
        Elements courses = doc.select("div.big_td");
        Log.d("div.big_td", courses.toString());
        //  if(SharedPreferenceHelper.getSharedPreferenceInt(getApplicationContext(), "count", -1) == -1) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ScheduleFragment().newInstance(courses.get(0).toString()), "Понедельник");
        adapter.addFragment(new ScheduleFragment().newInstance(courses.get(2).toString()), "Вторник");
        adapter.addFragment(new ScheduleFragment().newInstance(courses.get(4).toString()), "Среда");
        adapter.addFragment(new ScheduleFragment().newInstance(courses.get(1).toString()), "Четверг");
        adapter.addFragment(new ScheduleFragment().newInstance(courses.get(3).toString()), "Пятница");
        adapter.addFragment(new ScheduleFragment().newInstance(courses.get(5).toString()), "Суббота");
        adapter.addFragment(new ScheduleFragment().newInstance(courses.get(6).toString()), "Воскресенье");

        progressBar.setVisibility(View.INVISIBLE);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(adapter);
        if(selectedDayOfWeek!=-1) {
            viewPager.setCurrentItem(selectedDayOfWeek);
            viewPager.computeScroll();
        } else {
            viewPager.setCurrentItem(dayOfWeek-1);
            viewPager.computeScroll();
        }
        viewPager.invalidate();
        SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(), week, str);
    }

    public class ParseSchedule extends AsyncTask<byte[], Void, Void> {
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
            adapter.addFragment(new ScheduleFragment().newInstance(courses.get(2).toString()), "Вторник");
            adapter.addFragment(new ScheduleFragment().newInstance(courses.get(4).toString()), "Среда");
            adapter.addFragment(new ScheduleFragment().newInstance(courses.get(1).toString()), "Четверг");
            adapter.addFragment(new ScheduleFragment().newInstance(courses.get(3).toString()), "Пятница");
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

            progressBar.setVisibility(View.INVISIBLE);
            viewPager.setOffscreenPageLimit(0);
            viewPager.setAdapter(adapter);
            viewPager.invalidate();
            super.onPostExecute(aVoid);
        }
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
