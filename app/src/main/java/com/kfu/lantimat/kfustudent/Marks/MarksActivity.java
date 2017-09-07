package com.kfu.lantimat.kfustudent.Marks;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kfu.lantimat.kfustudent.KFURestClient;
import com.kfu.lantimat.kfustudent.LoginActivity;
import com.kfu.lantimat.kfustudent.MainActivity;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.SharedPreferenceHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.kfu.lantimat.kfustudent.LoginActivity.AUTH;

public class MarksActivity extends MainActivity {

    ArrayList<Mark> arBlock;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.button)
    Button button;

    //private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_marks);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FrameLayout v = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_marks, v);

        ButterKnife.bind(this);


        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        toolbar.setTitle("Успеваемость");

        arBlock = new ArrayList<>();

        initViewPager();
    }


    public void showNeedLogin() {
        textView.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
    }

    public void btnClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void getMarks() {
        KFURestClient.get("SITE_STUDENT_SH_PR_AC.score_list_book_subject?p_menu=7", null, new AsyncHttpResponseHandler() {
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
            Elements courses = doc.select("div.courses");
            count = courses.toString().split("</span>").length;

            /*if(SharedPreferenceHelper.getSharedPreferenceInt(getApplicationContext(), "count", -1) == -1) {
                adapter = new ViewPagerAdapter(getSupportFragmentManager());
                for (int i = 1; i < count - 1; i++) {
                    adapter.addFragment(new MarksFragment().newInstance(i), i + " курс");
                }
            }*/

            adapter = new ViewPagerAdapter(getSupportFragmentManager());
            for (int i = 1; i < count - 1; i++) {
                adapter.addFragment(new MarksFragment().newInstance(i), i + " курс");
            }

            SharedPreferenceHelper.setSharedPreferenceInt(getApplicationContext(), "count", count);
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

        /*count = SharedPreferenceHelper.getSharedPreferenceInt(getApplicationContext(), "count", -1);
        if(count != -1) {
            adapter = new ViewPagerAdapter(getSupportFragmentManager());
            for (int i = 1; i < count - 1; i++) {
                adapter.addFragment(new MarksFragment().newInstance(i), i + " курс");
            }

            viewPager.setOffscreenPageLimit(count);
            viewPager.setAdapter(adapter);
        }*/
        if (SharedPreferenceHelper.getSharedPreferenceBoolean(getApplicationContext(), AUTH, false)) getMarks();
        else showNeedLogin();

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
