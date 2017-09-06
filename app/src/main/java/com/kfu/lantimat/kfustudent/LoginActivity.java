package com.kfu.lantimat.kfustudent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kfu.lantimat.kfustudent.Timeline.TimeLineActivity;
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
    Button loginButton, forgotButton;
    EditText loginEditText, passEditText;
    PersistentCookieStore myCookieStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.loginButton);
        forgotButton = (Button) findViewById(R.id.forgotButton);
        loginEditText = (EditText) findViewById(R.id.loginEditText);
        passEditText = (EditText) findViewById(R.id.passEditText);

        final String login = "IlIGabdrahmanov";
        final String pass = "AnTi89600747198";

        loginEditText.setText(login);
        passEditText.setText(pass);

        //KFURestClient.setCookieStore(myCookieStore);
        myCookieStore = new PersistentCookieStore(this);

        KFURestClient.client.setCookieStore(myCookieStore);

        checkAuth();

        loginButton.setOnClickListener(view -> {
            String email = loginEditText.getText().toString();
            String password = passEditText.getText().toString();

            login(login, pass, new LoginCallback() {
                @Override
                public void onSuccess(String url) {
                    saveSessionCookies(url, new SaveSessionCookieCallback() {
                        @Override
                        public void onSuccess(String response) {
                            Toast.makeText(getApplicationContext(), "Авторизация успешна", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, TimeLineActivity.class);
                            startActivity(intent);
                            finish();
                            //Log.d("ПРОФИЛЬ", responce);
                            Log.d("saveSessionCookies", "Succes");
                        }
                    });
                }
            });
        });


        forgotButton.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://kpfu.ru/change_user_pass.forget_pass_form?p_lang=1"));
            startActivity(browserIntent);
        });
    }

    interface CheckLogin {
        void onLoggedIn();

        void onNotLoggedIn();
    }

    interface LoginCallback {
        void onSuccess(String url);
    }

    interface SaveSessionCookieCallback {
        void onSuccess(String response);
    }

    public void checkAuth() {
        checkLogin(new CheckLogin() {
            @Override
            public void onLoggedIn() {
                Toast.makeText(getApplicationContext(), "Сессия еще жива", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(LoginActivity.this, TimeLineActivity.class);
                //startActivity(intent);
                finish();

            }

            @Override
            public void onNotLoggedIn() {
                Toast.makeText(getApplicationContext(), "Сессия устарела, необходима авторизация", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkLogin(final CheckLogin checkLogin) {
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

    public void login(String email, String pass, final LoginCallback success) {
        final RequestParams params = new RequestParams();
        params.add("p_login", email);
        params.add("p_pass", pass);
        KFURestClient.get("private_office.authscript", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.wtf("login", "loged");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Pattern pattern = Pattern.compile("href=\\'(.*)\\'</");
                Matcher matcher = pattern.matcher(responseString);
                String url = "";

                if (matcher.find()) {
                    url = matcher.group(1);
                }
                success.onSuccess(url);
            }
        });
    }

    public void getLocale(final MainActivity.Success success){
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
                Log.d("saveSessionCookies", "onSuccess");
                Log.d("saveSessionCookies", responseString);

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
}
