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

import com.google.firebase.crash.FirebaseCrash;
import com.kfu.lantimat.kfustudent.Feeds.FeedActivity;
import com.kfu.lantimat.kfustudent.Timeline.TimeLineActivity;
import com.kfu.lantimat.kfustudent.utils.CheckAuth;
import com.kfu.lantimat.kfustudent.utils.User;
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
    CheckAuth checkAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MyMaterialTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.loginButton);
        forgotButton = (Button) findViewById(R.id.forgotButton);
        loginEditText = (EditText) findViewById(R.id.loginEditText);
        passEditText = (EditText) findViewById(R.id.passEditText);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);

        //loginEditText.setText(login);
        //passEditText.setText(pass);

        if(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(), LOGIN, "").isEmpty()) {
            loginEditText.setText(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),LOGIN, ""));
            passEditText.setText(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),PASSWORD, ""));
        }

        //KFURestClient.setCookieStore(myCookieStore);


       checkAuth = new CheckAuth(getApplicationContext());

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String l = loginEditText.getText().toString();
                final String p = passEditText.getText().toString();
                SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(), LOGIN, loginEditText.getText().toString());
                SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(), PASSWORD, passEditText.getText().toString());
                progressBar.setVisibility(View.VISIBLE);

                CheckAuth.login(l, p, new CheckAuth.LoginCallback() {
                    @Override
                    public void onSuccess(String url) {
                        Toast.makeText(getApplicationContext(), R.string.auth_succes, Toast.LENGTH_SHORT).show();
                        SharedPreferenceHelper.setSharedPreferenceBoolean(getApplicationContext(), AUTH, true);

                        CheckAuth.saveSessionCookies(url, new CheckAuth.SaveSessionCookieCallback() {
                            @Override
                            public void onSuccess(String response) {
                                //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                                CheckAuth.getUserInfo(new CheckAuth.UserInfoCallback() {
                                    @Override
                                    public void onSuccess(User user) {
                                        Intent intent = new Intent(LoginActivity.this, FeedActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        });
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
            }
        });


        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://kpfu.ru/change_user_pass.forget_pass_form?p_lang=1"));
                startActivity(browserIntent);
            }
        });
    }
}
