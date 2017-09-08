package com.kfu.lantimat.kfustudent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kfu.lantimat.kfustudent.Timeline.TimeLineActivity;
import com.kfu.lantimat.kfustudent.utils.CheckAuth;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

public class LoginActivity extends AppCompatActivity {

    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String AUTH = "auth";

    Button loginButton, forgotButton;
    EditText loginEditText, passEditText;
    ProgressBar progressBar;
    PersistentCookieStore myCookieStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.loginButton);
        forgotButton = (Button) findViewById(R.id.forgotButton);
        loginEditText = (EditText) findViewById(R.id.loginEditText);
        passEditText = (EditText) findViewById(R.id.passEditText);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        String login = "IlIGabdrahmanov";
        String pass = "AnTi89600747198";

        //loginEditText.setText(login);
        //passEditText.setText(pass);

        if(!SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(), LOGIN, "not").isEmpty()) {
            loginEditText.setText(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),LOGIN, ""));
            passEditText.setText(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),PASSWORD, ""));
        }

        //KFURestClient.setCookieStore(myCookieStore);


        //checkAuth();

        loginButton.setOnClickListener(view -> {
            String login2 = loginEditText.getText().toString();
            String pass2 = passEditText.getText().toString();

            SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(), LOGIN, loginEditText.getText().toString());
            SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(), PASSWORD, passEditText.getText().toString());

            progressBar.setVisibility(View.VISIBLE);

            CheckAuth.login(login2, pass2, new CheckAuth.LoginCallback() {
                @Override
                public void onSuccess(String url) {
                    SharedPreferenceHelper.setSharedPreferenceBoolean(getApplicationContext(), AUTH, true);
                    startActivity(new Intent(LoginActivity.this, TimeLineActivity.class));
                    finish();
                }

                @Override
                public void onLoginAndPassFail() {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onConnectFail() {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        });


        forgotButton.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://kpfu.ru/change_user_pass.forget_pass_form?p_lang=1"));
            startActivity(browserIntent);
        });
    }

    /*interface CheckLoginCallback {
        void onLoggedIn();

        void onNotLoggedIn();

        void onOldSession();
    }

    interface LoginCallback {
        void onSuccess(String url);
        void onLoginAndPassFail();
    }

    interface SaveSessionCookieCallback {
        void onSuccess(String response);
    }

    public void checkAuth() {
        checkLogin(new CheckLoginCallback() {
            @Override
            public void onLoggedIn() {
                Toast.makeText(getApplicationContext(), "Сессия еще жива", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(LoginActivity.this, TimeLineActivity.class);
                //startActivity(intent);
                //finish();

            }

            @Override
            public void onNotLoggedIn() {
                Toast.makeText(getApplicationContext(), "Сессия устарела, необходима авторизация", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOldSession() {

            }
        });
    }

    public void checkLogin(final CheckLoginCallback checkLogin) {
        KFURestClient.getUrl("http://shelly.kpfu.ru/e-ksu/main_blocks.startpage", new RequestParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //Log.d("checkLogin", "onSuccess");
                String str = "";
                try {
                    str = new String(responseBody, "windows-1251");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //Log.d("responseString", str);

                if (str.contains("Извините, устарела сессия работы с системой")) {
                    checkLogin.onNotLoggedIn();
                    Log.d("CheckLoginCallback", "not onSuccess");
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

    public void login(String email, String pass, final LoginCallback loginCallback) {
        final RequestParams params = new RequestParams();
        params.add("p_login", email);
        params.add("p_pass", pass);
        KFURestClient.get("private_office.authscript", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = null;
                try {
                    str = new String(responseBody, "windows-1251");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                Pattern pattern = Pattern.compile("href=\\'(.*)\\'</");
                Matcher matcher = pattern.matcher(str);
                String url = "";
                Log.d("login", str);

                if(str.contains("Извините, неверно введены имя или пароль")) {
                    loginCallback.onLoginAndPassFail();
                } else {
                    if (matcher.find()) {
                        url = matcher.group(1);
                    }
                    loginCallback.onSuccess(url);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.wtf("login", "loged");
            }
        });
    }


    public void saveSessionCookies(String link, final SaveSessionCookieCallback response) {

        KFURestClient.get(link, new RequestParams(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String text = "";
                try {

                    text = new String(responseString.getBytes("windows-1251"), "windows-1251"); // TODO: 04.09.17 Эта хуйня не работает
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //Log.d("saveSessionCookies", "onSuccess");
                //Log.d("saveSessionCookies", responseString);

                Pattern pattern = Pattern.compile("h_id=(.*);domain");
                Matcher matcher = pattern.matcher(responseString);
                if (matcher.find()) {
                    BasicClientCookie basicClientCookie = new BasicClientCookie("h_id", matcher.group(1));
                    basicClientCookie.setDomain("kpfu.ru");
                    basicClientCookie.setPath("/");
                    myCookieStore.addCookie(basicClientCookie);
                }


                pattern = Pattern.compile("s_id=(.*);domain");
                matcher = pattern.matcher(responseString);
                if (matcher.find()) {
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

                String text = "";
                try {

                    text = new String(responseString.getBytes(), "windows-1251");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                response.onSuccess(text);

            }
        });
    }

    public void getLocale(final LoginCallback success){
        KFURestClient.getUrl("http://kpfu.ru/?p_sub=3", new RequestParams(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
            }
        });
    }*/

}
