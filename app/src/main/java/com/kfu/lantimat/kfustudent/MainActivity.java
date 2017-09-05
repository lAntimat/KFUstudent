package com.kfu.lantimat.kfustudent;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.kfu.lantimat.kfustudent.Marks.MarksActivity;
import com.kfu.lantimat.kfustudent.Schedule.ScheduleActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;


public class MainActivity extends AppCompatActivity {

    PersistentCookieStore myCookieStore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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

        /*getLocale(new Success() {
            @Override
            public void succes() {
                login(login, pass, new Logged() {
                    @Override
                    public void logged(String link) {
                        getProfile(link, new Response() {
                            @Override
                            public void succes(String responce) {
                                Log.d("ПРОФИЛЬ", responce);
                            }
                        });
                    }
                });
            }
        });*/

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


