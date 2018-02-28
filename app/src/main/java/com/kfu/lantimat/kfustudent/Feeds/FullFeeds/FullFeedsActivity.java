package com.kfu.lantimat.kfustudent.Feeds.FullFeeds;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kfu.lantimat.kfustudent.Feeds.FullNews.FullNewsItem;
import com.kfu.lantimat.kfustudent.Feeds.FullNews.FullNewsRecyclerAdapter;
import com.kfu.lantimat.kfustudent.Feeds.PhotoGallery.PhotoGalleryActivity;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.SpRestClient;
import com.kfu.lantimat.kfustudent.utils.Constants;
import com.kfu.lantimat.kfustudent.utils.WrapContentViewPager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.blurry.Blurry;

public class FullFeedsActivity extends AppCompatActivity {

    private TextView textView;
    private ProgressBar progressBar;
    private HtmlTextView htmlTextView;
    private RecyclerView recyclerView;
    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton fab;
    private CoordinatorLayout coordinatorLayout;
    private WrapContentViewPager pager;
    private ImageView imageView;

    private FullNewsRecyclerAdapter adapter;
    private ArrayList<FullNewsItem> ar = new ArrayList<>();


    private String title;
    private String photoGalleryUrl = "";
    private ArrayList<FullFeedAppBarItems> appbarImagesUrl = new ArrayList<>();
    private String videoType;
    private static int currentPage = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MyMaterialTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_feed);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        fab = findViewById(R.id.fab);
        fab.hide();
        collapsingToolbar = findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle("");
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.md_white_1000));
        collapsingToolbar.setExpandedTitleTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.transparent)));

        textView = findViewById(R.id.textView);
        htmlTextView = findViewById(R.id.html_text);
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new FullNewsRecyclerAdapter(this, ar);
        pager = findViewById(R.id.viewPager);
        imageView = findViewById(R.id.imageView);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FullFeedsActivity.this, PhotoGalleryActivity.class);
                intent.putExtra("url", photoGalleryUrl);
                startActivity(intent);
            }
        });

        initRecyclerView();

        getDate(getIntent().getStringExtra("url"));
        Picasso.with(getApplicationContext()).load(getIntent().getStringExtra("img")).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Blurry.with(getApplicationContext()).from(bitmap).into(imageView);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        // from View
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

    private void initAppbarSlider(final ArrayList<FullFeedAppBarItems> ar) {

        pager.setAdapter(new AppbarImagesAdapter(this, ar));
        //CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        //indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == ar.size() - 1) {
                    currentPage = 0;
                }
                if (currentPage == 0) pager.setCurrentItem(currentPage++, false);
                else pager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 3500);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pager.reMeasureCurrentPage(pager.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void getDate(String url) {
        progressBar.setVisibility(View.VISIBLE);
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


            Elements titleRow = doc.select("section.span16.page");
            title = titleRow.select("h1").text();
            collapsingToolbar.setTitle(title);

            //Парсим часть со слайдера
            Elements appbarImages = doc.select("div.event-slider").select("ul").select("li"); //Считываем данные слайдера
            for (Element e : appbarImages) {
                if (e.toString().contains("video-clicker popup-play-video")) {
                    String dataVideo = e.select("div").attr("data-video"); //Ссылка на видео id

                    //Тип источника
                    // youTube = "1"; vimeo = "3";
                    String dataType = e.select("div").attr("data-type");
                    String img = e.select("img").attr("src"); //Ссылка на видео id
                    appbarImagesUrl.add(new FullFeedAppBarItems(FullFeedAppBarItems.VIDEO, dataType, dataVideo, img));
                } else if (e.toString().contains("img")) {
                    String img = e.select("img").attr("src"); //Ссылка на видео id
                    appbarImagesUrl.add(new FullFeedAppBarItems(FullFeedAppBarItems.IMAGE, img));
                }
            }

            initAppbarSlider(appbarImagesUrl);

            //Парсим часть с текстом
            Elements textRow = doc.select("div.text-content");
            for (Element element : textRow.get(0).children()) {
                Log.d("FullFeed", element.nodeName());
                switch (element.nodeName()) {
                    case "p":
                        if (element.toString().contains("iframe")) {
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

            //Ссылка на галерею
            Elements gallery = doc.select("a.span8");
            if (gallery.size() > 0) {
                photoGalleryUrl = gallery.attr("href"); //Адрес галереи
                photoGalleryUrl = Constants.urlStudProf + photoGalleryUrl;
                fab.show();
            }

            progressBar.setVisibility(View.INVISIBLE);
            adapter.notifyDataSetChanged();

        }
    }
}
