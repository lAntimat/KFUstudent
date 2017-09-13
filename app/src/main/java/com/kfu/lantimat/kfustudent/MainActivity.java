package com.kfu.lantimat.kfustudent;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.kfu.lantimat.kfustudent.Marks.MarksActivity;
import com.kfu.lantimat.kfustudent.Schedule.Schedule;
import com.kfu.lantimat.kfustudent.Schedule.ScheduleActivity;
import com.kfu.lantimat.kfustudent.Timeline.TimeLineActivity;
import com.kfu.lantimat.kfustudent.Timeline.model.Orientation;
import com.kfu.lantimat.kfustudent.utils.About;
import com.kfu.lantimat.kfustudent.utils.CheckAuth;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;


public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_ORIENTATION = "EXTRA_ORIENTATION";
    public final static String EXTRA_WITH_LINE_PADDING = "EXTRA_WITH_LINE_PADDING";

    PersistentCookieStore myCookieStore;

    public Toolbar toolbar;
    public Spinner spinner;
    AccountHeader headerResult;
    public Drawer result;
    FrameLayout frameLayout;
    Intent drawerIntent = null;
    boolean dontFinish = false;
    SecondaryDrawerItem sign_exit;
    int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        frameLayout = (FrameLayout)findViewById(R.id.content_frame);

        spinner = (Spinner) findViewById(R.id.spinner_nav);
        spinner.setVisibility(View.INVISIBLE);

        color = ContextCompat.getColor(getApplicationContext(), R.color.accent);

        getSupportActionBar().setTitle("");


        initAccountHeader();
        setupNavigationDrawer();
        authCheck();

    }

    public void authCheck() {
        new CheckAuth(getApplicationContext(), new CheckAuth.AuthCallback() {
            @Override
            public void onLoggedIn() {
                updateDrawer();
                //setupNavigationDrawer();
            }

            @Override
            public void onNotLoggedIn() {
                updateDrawer();
                //setupNavigationDrawer();
            }

            @Override
            public void onOldSession() {
                updateDrawer();
                //setupNavigationDrawer();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        //startActivity(new Intent(MainActivity.this, LoginActivity.class));
        //Log.d("OnPostResume", "WTF");
        super.onPostResume();
    }


    public void btnClick(View view) {
        Intent intent = new Intent(MainActivity.this, MarksActivity.class);
        startActivity(intent);
    }
    public void btnClick2(View view) {
        Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
        startActivity(intent);
    }
    public void btnClick3(View view) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    public void btnClick4(View view) {
        Intent intent = new Intent(this, TimeLineActivity.class);
        intent.putExtra(EXTRA_ORIENTATION, Orientation.VERTICAL);
        intent.putExtra(EXTRA_WITH_LINE_PADDING, false);
        startActivity(intent);
    }

    public void btnLoginClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void updateDrawer() {
        StringHolder stringHolder;
        if(CheckAuth.isAuth()) stringHolder = new StringHolder(R.string.drawer_item_exit);
        else stringHolder = new StringHolder(R.string.drawer_item_sign_in);
        result.updateName(2, stringHolder);
    }

    @Override
    public void onBackPressed() {
        if(result!=null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else super.onBackPressed();
    }

    private void initAccountHeader() {
        String fullName = "";
        String group = "";
        ProfileDrawerItem profileDrawerItem = null;

        if(!SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(), CheckAuth.FULL_NAME, "").isEmpty()) {
            fullName = SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(), CheckAuth.FULL_NAME, "");
            if(!SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(), CheckAuth.GROUP, "").isEmpty()) {
                group = SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(), CheckAuth.GROUP, "");
                profileDrawerItem = new ProfileDrawerItem()
                        .withName(fullName)
                        .withEmail(getString(R.string.drawer_item_group) + " " + group)
                        .withIcon(R.mipmap.ic_launcher);
            } else {
                profileDrawerItem = new ProfileDrawerItem()
                        .withName(fullName)
                        .withIcon(R.mipmap.ic_launcher);
            }

        }

        if(profileDrawerItem!=null) {
            headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.bg)
                    //.withCompactStyle(true)
                    .addProfiles(profileDrawerItem)
                    .build();
        } else {
            headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.bg)
                    //.withCompactStyle(true)
                    .build();
        }
    }

    public void setupNavigationDrawer() {

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_timeline).withIcon(R.drawable.ic_chart_timeline_grey600_24dp).withIconColor(color);
        //PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("Новости");
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.drawer_item_schedule).withIcon(R.drawable.ic_school_grey600_24dp).withIconColor(color);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.drawer_item_marks).withIcon(R.drawable.ic_calendar_multiple_grey600_24dp).withIconColor(color);
        SecondaryDrawerItem sign_exit;
        if(CheckAuth.isAuth()) sign_exit = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_exit).withIconColor(color);
        else sign_exit = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_sign_in).withIconColor(color);

        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(4).withName("Intro").withIcon(R.drawable.ic_calendar_multiple_grey600_24dp).withIconColor(color);
        PrimaryDrawerItem about = new PrimaryDrawerItem().withIdentifier(5).withName(R.string.drawer_item_about).withIcon(R.drawable.ic_information_grey600_24dp).withIconColor(color);

        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        item1,
                        item3,
                        item4,
                        new DividerDrawerItem(),
                        about,
                        sign_exit
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                drawerIntent = new Intent(MainActivity.this, TimeLineActivity.class);
                                break;
                            case 10: //drawerIntent = new Intent(MainActivity.this, TimeLineActivity.class);
                                break;
                            case 2: drawerIntent = new Intent(MainActivity.this, ScheduleActivity.class);
                                break;
                            case 3:
                                drawerIntent = new Intent(MainActivity.this, MarksActivity.class);
                                break;
                            case 6:
                                if(CheckAuth.isAuth()) {
                                    CheckAuth.exit();
                                    startActivity(new Intent(MainActivity.this, TimeLineActivity.class));
                                    finish();
                                }
                                else {
                                    drawerIntent = new Intent(MainActivity.this, LoginActivity.class);
                                    dontFinish = true;
                                }
                                break;
                            case 5: //TODO: вызов абаут
                                new About().onCreateDialog(MainActivity.this).show();
                        }
                        result.closeDrawer();
                        return true;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        if(drawerIntent!=null) {
                            startActivity(drawerIntent);
                            overridePendingTransition(0, 0);
                            drawerIntent = null;
                            if(!dontFinish) finish();
                            dontFinish = false;
                        }
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .build();
    }

}


