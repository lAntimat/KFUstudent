package com.kfu.lantimat.kfustudent;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
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
    Drawer result;
    FrameLayout frameLayout;
    Intent drawerIntent = null;


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

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        RequestParams params = new RequestParams();
        params.add("p_login", "IlIGabdrahmanov");
        params.add("p_pass", "AnTi89600747198");
        //KFURestClient.setCookieStore(myCookieStore);
       // myCookieStore = new PersistentCookieStore(this);

        //KFURestClient.client.setCookieStore(myCookieStore);

        //final String login = "DAJuzikeev";
        //final String pass = "sjp4bq74";

        final String login = "IlIGabdrahmanov";
        final String pass = "AnTi89600747198";

        //Intent intent = new Intent(MainActivity.this, TimeLineActivity.class);
        //startActivity(intent);
        initAccountHeader();
        setupNavigationDrawer();

        /*getLocale(new Success() {
            @Override
            public void onSuccess() {
                login(login, pass, new LoginCallback() {
                    @Override
                    public void onSuccess(String link) {
                        saveSessionCookies(link, new SaveSessionCookieCallback() {
                            @Override
                            public void onSuccess(String responce) {
                                Log.d("ПРОФИЛЬ", responce);
                            }
                        });
                    }
                });
            }
        });*/

        //checkAuth();

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

    interface Success{
        void succes();
    }
    interface Logged{
        void logged(String link);
    }
    interface Response{
        void succes(String responce);
    }


    public void checkAuth() {
        checkLogin(new LoginActivity.CheckLogin() {
            @Override
            public void onLoggedIn() {
                Toast.makeText(getApplicationContext(), "Сессия еще жива", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNotLoggedIn() {
                Toast.makeText(getApplicationContext(), "Сессия устарела, необходима авторизация", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkLogin(final LoginActivity.CheckLogin checkLogin) {
        KFURestClient.getUrl("http://shelly.kpfu.ru/e-ksu/main_blocks.startpage", new RequestParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("checkLogin", "onSuccess");
                String str = "";
                try {
                    str = new String(responseBody, "windows-1251");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.d("responseString", str);

                if (str.contains("Извините, устарела сессия работы с системой")) {
                    checkLogin.onNotLoggedIn();
                    Log.d("CheckLogin", "not onSuccess");
                } else {
                    checkLogin.onLoggedIn();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("checkLogin", "onFailure");
            }
        });
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

    private void initAccountHeader() {
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.bg)
                //.withCompactStyle(true)
                /*.addProfiles(
                        new ProfileDrawerItem()
                                .withName("Mike Penz")
                                .withEmail("mikepenz@gmail.com"))*/

                .build();
    }

    private void setupNavigationDrawer() {

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Таймлайн");
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("Новости");
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("Расписание");
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName("Успеваемость");
        SecondaryDrawerItem item10 = new SecondaryDrawerItem().withIdentifier(2).withName("Войти");

        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        item1,
                        item2,
                        item3,
                        item4,
                        new DividerDrawerItem(),
                        item10
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1: drawerIntent = new Intent(MainActivity.this, TimeLineActivity.class);
                                break;
                            case 2: //drawerIntent = new Intent(MainActivity.this, TimeLineActivity.class);
                                break;
                            case 3: drawerIntent = new Intent(MainActivity.this, ScheduleActivity.class);
                                break;
                            case 4:
                                drawerIntent = new Intent(MainActivity.this, MarksActivity.class);
                                break;
                            case 6:
                                drawerIntent = new Intent(MainActivity.this, LoginActivity.class);
                                break;
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
                        }
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .build();

    }

    public void getLocale(final Success success){
        KFURestClient.getUrl("http://kpfu.ru/?p_sub=3", new RequestParams(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                success.succes();
            }
        });
    }

    public void login(String email, String pass,final Logged success){
        final RequestParams params = new RequestParams();
        params.add("p_login", email);
        params.add("p_pass", pass);
        KFURestClient.get("private_office.authscript", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.wtf("login","loged");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Pattern pattern = Pattern.compile("href=\\'(.*)\\'</");
                Matcher matcher = pattern.matcher(responseString);
                String url = "";

                if(matcher.find()){
                    url = matcher.group(1);
                }
                success.logged(url);
            }
        });
    }

    public void getProfile(String link, final Response response){

        KFURestClient.get(link, new RequestParams(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {String text= "";
                try {

                    text = new String(responseString.getBytes("windows-1251"),"windows-1251"); // TODO: 04.09.17 Эта хуйня не работает
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Pattern pattern = Pattern.compile("h_id=(.*);domain");
                Matcher  matcher = pattern.matcher(responseString);
                if(matcher.find()){
                    BasicClientCookie basicClientCookie = new BasicClientCookie("h_id", matcher.group(1));
                    basicClientCookie.setDomain("kpfu.ru");
                    basicClientCookie.setPath("/");
                    myCookieStore.addCookie(basicClientCookie);
                }


                pattern = Pattern.compile("s_id=(.*);domain");
                matcher = pattern.matcher(responseString);
                if(matcher.find()){
                    BasicClientCookie basicClientCookie = new BasicClientCookie("s_id", matcher.group(1));
                    basicClientCookie.setDomain("kpfu.ru");
                    basicClientCookie.setPath("/");
                    myCookieStore.addCookie(basicClientCookie);
                }

                pattern = Pattern.compile("<a class = \"ico\" href = \"(.*)\" title");
                matcher = pattern.matcher(responseString);
                matcher.find();
                if(matcher.find()){
                    SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(), "scheduleUrl", matcher.group(1).replace("student_personal_main.show_notification?", ""));
                    Log.d("scheduleUrl", matcher.group(1).replace("student_personal_main.show_notification?", ""));
                }

                String text= "";
                try {

                    text = new String(responseString.getBytes(),"windows-1251");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                response.succes(text);

            }
        });
    }


}


