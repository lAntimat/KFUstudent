package com.kfu.lantimat.kfustudent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

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

        loginButton.setOnClickListener(view -> {
            String email = loginEditText.getText().toString();
            String password = passEditText.getText().toString();

            checkLogin(new CheckLogin() {
                @Override
                public void succes() {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent); //TODO:  куда напрвить после авторизаци
                }

                @Override
                public void notLogged() {
                    login(login, pass, new Logged() {
                        @Override
                        public void logged(String link) {
                            getProfile(link, new Response() {
                                @Override
                                public void succes(String responce) {
                                    //Log.d("ПРОФИЛЬ", responce);
                                    Log.d("getProfile", "Succes");
                                }
                            });
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
        void succes();

        void notLogged();
    }

    interface Logged {
        void logged(String link);
    }

    interface Response {
        void succes(String responce);
    }

    public void checkLogin(final CheckLogin success) {
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
                    success.notLogged();
                    Log.d("CheckLogin", "not logged");
                } else {
                    success.succes();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("checkLogin", "onFailure");
            }
        });
    }

    public void login(String email, String pass, final Logged success) {
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
                success.logged(url);
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

    public void getProfile(String link, final Response response) {

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
                Log.d("getProfile", "onSuccess");


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

                String text = "";
                try {

                    text = new String(responseString.getBytes(), "windows-1251");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                response.succes(text);

            }
        });
    }
}
