package com.kfu.lantimat.kfustudent;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;

/**
 * Created by GabdrakhmanovII on 10.08.2017.
 */

public class KFURestClient {
    private static final String BASE_URL = "http://shelly.kpfu.ru/e-ksu/";

    public static AsyncHttpClient client = new AsyncHttpClient();
    static PersistentCookieStore myCookieStore;


    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.d("KfuRestClient", "get " + getAbsoluteUrl(url));

        client.setTimeout(20000);
        client.setEnableRedirects(true);
        client.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void getUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.d("KfuRestClient", "get " + getAbsoluteUrl(url));
        client.setTimeout(20000);
        client.setEnableRedirects(true);
        client.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        client.get(url, params, responseHandler);
    }
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void setCookieStore(PersistentCookieStore cookieStore) {
        myCookieStore = cookieStore;
        client.setCookieStore(myCookieStore);
    }
    public static PersistentCookieStore getCookieStore() {
        return myCookieStore;
    }

}