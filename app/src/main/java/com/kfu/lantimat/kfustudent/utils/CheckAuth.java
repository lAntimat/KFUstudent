package com.kfu.lantimat.kfustudent.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.kfu.lantimat.kfustudent.KFURestClient;
import com.kfu.lantimat.kfustudent.LoginActivity;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.SharedPreferenceHelper;
import com.kfu.lantimat.kfustudent.Timeline.TimeLineActivity;
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

import static com.kfu.lantimat.kfustudent.LoginActivity.AUTH;
import static com.kfu.lantimat.kfustudent.LoginActivity.LOGIN;
import static com.kfu.lantimat.kfustudent.LoginActivity.PASSWORD;

/**
 * Created by GabdrakhmanovII on 07.09.2017.
 */


public class CheckAuth {

    public final static String FULL_NAME = "fullName";
    public final static String GROUP = "group";
    static Context context;

    public interface AuthCallback {
        void onLoggedIn();
        void onNotLoggedIn();
        void onOldSession();
    }
    public interface LoginCallback {
        void onSuccess(String url);
        void onLoginAndPassFail();
        void onConnectFail();
    }
    interface SaveSessionCookieCallback {
        void onSuccess(String response);
    }

    static PersistentCookieStore myCookieStore;
    static boolean isAuth = false;

    public CheckAuth(Context context, AuthCallback authCallback) {
        myCookieStore = KFURestClient.getCookieStore();
        this.context = context;
        checkAuth(authCallback);
    }

    public void checkAuth(final AuthCallback authCallback) {

        checkLogin(new AuthCallback() {
            @Override
            public void onLoggedIn() {
                isAuth = true;
                SharedPreferenceHelper.setSharedPreferenceBoolean(context, AUTH, true);
                //Toast.makeText(context, "Сессия еще жива и авторизация норм", Toast.LENGTH_SHORT).show();
                authCallback.onLoggedIn();
                getFullName();

            }

            @Override
            public void onNotLoggedIn() {
                isAuth = false;
                SharedPreferenceHelper.setSharedPreferenceBoolean(context, AUTH, false);
            }

            @Override
            public void onOldSession() {
                //Toast.makeText(context, "Сессия устарела, необходима переавторизация", Toast.LENGTH_SHORT).show();
                String login = "";
                String password = "";
                if(SharedPreferenceHelper.getSharedPreferenceString(context, LOGIN, "not")!=null) {
                    login = SharedPreferenceHelper.getSharedPreferenceString(context,LOGIN, "");
                    password = SharedPreferenceHelper.getSharedPreferenceString(context, PASSWORD, "");
                    login(login, password, new LoginCallback() {
                        @Override
                        public void onSuccess(String url) {
                            saveSessionCookies(url, new SaveSessionCookieCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    //Toast.makeText(context, "Авторизация успешна", Toast.LENGTH_SHORT).show();
                                    authCallback.onLoggedIn();
                                    //Log.d("ПРОФИЛЬ", responce);
                                    Log.d("saveSessionCookies", "Success");
                                }
                            });
                        }

                        @Override
                        public void onLoginAndPassFail() {
                            authCallback.onNotLoggedIn();
                        }

                        @Override
                        public void onConnectFail() {
                            authCallback.onNotLoggedIn();
                        }
                    });

                }

            }
        });
    }

    public void checkLogin(final AuthCallback authCallback) {
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
                    if(SharedPreferenceHelper.getSharedPreferenceString(context, LOGIN, "not").isEmpty()) { //Если не вводили логин и пароль
                        authCallback.onNotLoggedIn();
                        Log.d("CheckLogin", "onNotLoggedIn");
                    } else {
                        if(SharedPreferenceHelper.getSharedPreferenceBoolean(context, AUTH, false)) {
                            authCallback.onOldSession(); //Иначе если логин и пароль сохранены, но пользователь нажал выйти
                            Log.d("CheckLogin", "onOldSession");
                        }
                    }

                } else {
                    authCallback.onLoggedIn();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("checkLogin", "onFailure");
            }
        });
    }

    public static void login(String email, String pass, final LoginCallback loginCallback) {
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
                Log.d("CheckAuthLogin", str);

                if(str.toLowerCase().contains("неверно введены имя или пароль")) {
                    Toast.makeText(context, R.string.login_error_log_and_pass, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
                loginCallback.onConnectFail();

            }
        });
    }

    public void saveSessionCookies(String link, final SaveSessionCookieCallback saveSessionCookieCallback) {

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
                    SharedPreferenceHelper.setSharedPreferenceString(context, "scheduleUrl", matcher.group(1).replace("student_personal_main.show_notification?", ""));
                    Log.d("scheduleUrl", matcher.group(1).replace("student_personal_main.show_notification?", ""));
                }

                String text = "";
                try {

                    text = new String(responseString.getBytes(), "windows-1251");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                saveSessionCookieCallback.onSuccess(text);

            }
        });
    }

    private static void getFullName() {
        if(SharedPreferenceHelper.getSharedPreferenceString(context, FULL_NAME, "").equalsIgnoreCase("")) {

                KFURestClient.get("new_stud_personal.stud_anketa", null, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            String str = new String(responseBody, "windows-1251");
                            Document doc = Jsoup.parse(str);
                            Elements elements = doc.select("span.value");
                            if(!elements.isEmpty()) {
                                if(elements.get(0).hasText()) SharedPreferenceHelper.setSharedPreferenceString(context, FULL_NAME, elements.get(0).text());
                                if(elements.get(5).hasText())SharedPreferenceHelper.setSharedPreferenceString(context, GROUP, elements.get(5).text());
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
        }


    }

    public static Boolean isAuth() {
        try {
            return SharedPreferenceHelper.getSharedPreferenceBoolean(context, AUTH, false);
        } catch (Exception e) {
            return false;
        }
    }

    public static void exit() {
        KFURestClient.clearCookie();
        SharedPreferenceHelper.setSharedPreferenceBoolean(context, AUTH, false);
        SharedPreferenceHelper.clearSharedPreference(context, FULL_NAME);
        SharedPreferenceHelper.clearSharedPreference(context, GROUP);
    }

}
