package com.kfu.lantimat.kfustudent;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kfu.lantimat.kfustudent.Marks.MarksActivity;
import com.kfu.lantimat.kfustudent.Marks.MarksFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/*import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;*/

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
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
        myCookieStore = new PersistentCookieStore(this);

        KFURestClient.client.setCookieStore(myCookieStore);

        getLocale(new Success() {
            @Override
            public void succes() {
                login("IlIGabdrahmanov", "AnTi89600747198", new Logged() {
                    @Override
                    public void logged(String link) {
                        getProfile(link, new Response() {
                            @Override
                            public void succes(String responce) {
                                Log.wtf("ПРОФИЛЬ", responce);
                            }
                        });
                    }
                });
            }
        });

    }

    public class ParseFeeds extends AsyncTask<byte[], Void, Void> {


        @Override
        protected Void doInBackground(byte[]... params) {

            //Log.d("MainActivity", "ParseFeed");

            String str = null;
            try {

                //str = new String(params[0], "UTF-8");
                str = new String(params[0], "windows-1251");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Document doc = Jsoup.parse(str);
            //Log.d("docToStrng", doc.toString());
            Elements title = doc.select("tr");
            //Log.d("title", title.text());

            /*
            //Elements feed = doc.select("table.center-center.big");
            //feed = feed.select("a");
            /*Elements title = feed.select("a[title]");
            Elements imgUrl = feed.select("[src]");
            Elements url = feed.select("a[title]");
            for (int j = 0; j < feed.size(); j++) {
                //arFeeds.add(new Feed(title.get(j).attr("title").replace("Permalink to ", ""), feed.get(j).select("div.excerpt-stats").text(), imgUrl.get(j).attr("src"), url.get(j).attr("href"), ""));
            }*/

            //Toast.makeText(getApplicationContext(), feed.text(), Toast.LENGTH_SHORT).show();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //feedsRecyclerAdapter.notifyDataSetChanged();
            //progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(aVoid);
        }
    }

    public class ParseParametrs extends AsyncTask<byte[], Void, Void> {

        String p2;
        String p_h;

        @Override
        protected Void doInBackground(byte[]... params) {

            //Log.d("MainActivity", "ParseFeed");

            String str = null;
            try {

                //str = new String(params[0], "UTF-8");
                str = new String(params[0], "windows-1251");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            /*Document doc = Jsoup.parse(str);

            Log.d("Parseparamtrs", doc.toString());*/
            /*if(doc.toString().contains("p2=")) {
                p2 = doc.toString().substring(doc.toString().indexOf("p2=") + 3, doc.toString().indexOf("&p_h="));
                Log.d("p2 ", p2);
                p_h = doc.toString().substring(doc.toString().indexOf("p_h") + 4, doc.toString().indexOf("'</script>"));
                Log.d("p_h ", p_h);
            }*/

            //Elements feed = doc.select("table.center-center.big");
            //feed = feed.select("a");
            /*Elements title = feed.select("a[title]");
            Elements imgUrl = feed.select("[src]");
            Elements url = feed.select("a[title]");
            for (int j = 0; j < feed.size(); j++) {
                //arFeeds.add(new Feed(title.get(j).attr("title").replace("Permalink to ", ""), feed.get(j).select("div.excerpt-stats").text(), imgUrl.get(j).attr("src"), url.get(j).attr("href"), ""));
            }*/

            //Toast.makeText(getApplicationContext(), feed.text(), Toast.LENGTH_SHORT).show();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //feedsRecyclerAdapter.notifyDataSetChanged();
            //progressBar.setVisibility(View.INVISIBLE);


            RequestParams params1 = new RequestParams();
            params1.add("p2", p2);
            params1.add("p_h", p_h);

            //client.addHeader("s_id", p2);
            //client.addHeader("h_id", p_h);

            //Log.d("cookieStorySize", String.valueOf(myCookieStore.getCookies().size()));
            if(!myCookieStore.getCookies().isEmpty()) {
                for (int i = 0; i <myCookieStore.getCookies().size() ; i++) {
                    //Log.d("", myCookieStore.getCookies().get(i).toString());
                }
            }

            BasicClientCookie newCookie = new BasicClientCookie("s_id", p2);
            myCookieStore.addCookie(newCookie);
            newCookie = new BasicClientCookie("h_id", p_h);
            myCookieStore.addCookie(newCookie);

            KFURestClient.get("e-ksu/main_blocks.startpage", params1, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    new ParseFeeds().execute(responseBody);

                    RequestParams params2 = new RequestParams();
                    params2.add("p_menu", "7");

                    KFURestClient.get("e-ksu/SITE_STUDENT_SH_PR_AC.score_list_book_subject", params2, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            //Log.d("MainActivity", "listBookSucces");
                            //PersistentCookieStore p = myCookieStore;
                            new ParseFeeds().execute(responseBody);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
            super.onPostExecute(aVoid);
        }
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


