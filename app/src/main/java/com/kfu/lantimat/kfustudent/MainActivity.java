package com.kfu.lantimat.kfustudent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;

import com.kfu.lantimat.kfustudent.CustomSchedule.CustomScheduleActivity;
import com.kfu.lantimat.kfustudent.Feeds.FeedActivity;
import com.kfu.lantimat.kfustudent.Marks.MarksActivity;
import com.kfu.lantimat.kfustudent.Schedule.ScheduleActivity;
import com.kfu.lantimat.kfustudent.Timeline.TimeLineActivity;
import com.kfu.lantimat.kfustudent.Timeline.model.Orientation;
import com.kfu.lantimat.kfustudent.map.MapActivity;
import com.kfu.lantimat.kfustudent.utils.About;
import com.kfu.lantimat.kfustudent.utils.CheckAuth;
import com.kfu.lantimat.kfustudent.utils.KfuUser;
import com.kfu.lantimat.kfustudent.utils.User;
import com.loopj.android.http.PersistentCookieStore;
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
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity implements CheckAuth.AuthCallback {

    public final static String TAG = "MainActivity";
    public final static String EXTRA_ORIENTATION = "EXTRA_ORIENTATION";
    public final static String EXTRA_WITH_LINE_PADDING = "EXTRA_WITH_LINE_PADDING";

    PersistentCookieStore myCookieStore;

    public Toolbar toolbar;
    public Spinner spinner;
    public AppBarLayout appBarLayout;
    AccountHeader headerResult;
    public Drawer result;
    public FrameLayout frameLayout;
    public FrameLayout frameLayout2;
    Intent drawerIntent = null;
    boolean dontFinish = false;
    SecondaryDrawerItem sign_exit;
    int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MyMaterialTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        appBarLayout = findViewById(R.id.appbar);
        frameLayout = (FrameLayout)findViewById(R.id.content_frame);
        frameLayout2 = (FrameLayout)findViewById(R.id.content_frame2);

        spinner = (Spinner) findViewById(R.id.spinner_nav);
        spinner.setVisibility(View.INVISIBLE);

        color = ContextCompat.getColor(getApplicationContext(), R.color.accent);

        getSupportActionBar().setTitle("");

        DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).fit().into(imageView);

            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx) {
                return null;
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                return null;
            }
        });


        initAccountHeader();
        if(result==null){
            setupNavigationDrawer();
        }
        authCheck();
    }


    public void authCheck() {
        new CheckAuth(getApplicationContext(), this);
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
        result.updateName(6, stringHolder);
    }

    public void updateAccount() {
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

            headerResult.updateProfile(profileDrawerItem);
        }
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
                        .withIcon(KfuUser.getImgUrl(getApplicationContext()));
            } else {
                profileDrawerItem = new ProfileDrawerItem()
                        .withName(fullName)
                        .withIcon(KfuUser.getImgUrl(getApplicationContext()));
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
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_schedule).withIcon(R.drawable.ic_school_grey600_24dp).withIconColor(color);
        PrimaryDrawerItem item11 = new PrimaryDrawerItem().withIdentifier(11).withName(R.string.drawer_item_custom_schedule).withIcon(R.drawable.ic_school_grey600_24dp).withIconColor(color);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.drawer_item_marks).withIcon(R.drawable.ic_calendar_multiple_grey600_24dp).withIconColor(color);
        PrimaryDrawerItem itemMap = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.drawer_item_map).withIcon(R.drawable.ic_google_maps_grey600_24dp).withIconColor(color);
        SecondaryDrawerItem sign_exit;
        if(CheckAuth.isAuth()) sign_exit = new SecondaryDrawerItem().withIdentifier(6).withName(R.string.drawer_item_exit).withIconColor(color);
        else sign_exit = new SecondaryDrawerItem().withIdentifier(6).withName(R.string.drawer_item_sign_in).withIconColor(color);

        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(10).withName("Intro").withIcon(R.drawable.ic_calendar_multiple_grey600_24dp).withIconColor(color);
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
                        item11,
                        item4,
                        itemMap,
                        new DividerDrawerItem(),
                        about,
                        sign_exit
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                drawerIntent = new Intent(MainActivity.this, FeedActivity.class);
                                break;
                            case 10: //drawerIntent = new Intent(MainActivity.this, TimeLineActivity.class);
                                break;
                            case 2: drawerIntent = new Intent(MainActivity.this, ScheduleActivity.class);
                                break;
                            case 3: drawerIntent = new Intent(MainActivity.this, CustomScheduleActivity.class);
                                break;
                            case 4:
                                drawerIntent = new Intent(MainActivity.this, MarksActivity.class);
                                break;
                            case 5:
                                drawerIntent = new Intent(MainActivity.this, MapActivity.class);
                                break;
                            case 8:
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
                            case 7: //TODO: вызов абаут
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

    @Override
    public void onLoggedIn() {
        updateAccount();
        updateDrawer();
    }

    @Override
    public void onNotLoggedIn() {
        updateDrawer();
    }

    @Override
    public void onOldSession() {
        updateDrawer();
    }
}


