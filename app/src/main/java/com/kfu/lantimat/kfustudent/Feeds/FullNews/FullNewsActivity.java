package com.kfu.lantimat.kfustudent.Feeds.FullNews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.SpRestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FullNewsActivity extends AppCompatActivity {

    TextView textView;
    HtmlTextView htmlTextView;
    RecyclerView recyclerView;
    FullNewsRecyclerAdapter adapter;
    ArrayList<FullNewsItem> ar = new ArrayList<>();
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MyMaterialTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_news);

        textView = findViewById(R.id.textView);
        htmlTextView = findViewById(R.id.html_text);
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new FullNewsRecyclerAdapter(this, ar);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        initRecyclerView();
        getDate(getIntent().getStringExtra("url"));
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(20);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(1024);
        recyclerView.setAdapter(adapter);
    }

    public void getDate(String url) {
        SpRestClient.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               parseNews(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void parseNews(byte[] bytes) {
        Document doc = null;//Здесь хранится будет разобранный html документ
        try {

            String str = new String(bytes, "utf-8");
            doc = Jsoup.parse(str);
        } catch (Exception e) {
            //Если не получилось считать
            e.printStackTrace();

        }
        if (doc != null) {

            Elements textRow = doc.select("div.text-content");
            Node node;

            textRow.forms();

            for (Element element: textRow.get(0).children()) {
                Log.d("FullFeed", element.nodeName());
                switch (element.nodeName()) {
                    case "p":
                        if(element.toString().contains("iframe")) {
                                String video_code = element.children().get(0).attr("src");
                                ar.add(new FullNewsItem(FullNewsItem.VIDEO, video_code));
                    }
                        ar.add(new FullNewsItem(FullNewsItem.TEXT, element.toString()));
                        break;
                    case "img":
                        ar.add(new FullNewsItem(FullNewsItem.IMAGE, element.attr("src")));
                        break;
                    case "iframe":
                        ar.add(new FullNewsItem(FullNewsItem.VIDEO, element.attr("src")));
                        break;
                }
            }

            progressBar.setVisibility(View.INVISIBLE);
            adapter.notifyDataSetChanged();

        }
    }
}
