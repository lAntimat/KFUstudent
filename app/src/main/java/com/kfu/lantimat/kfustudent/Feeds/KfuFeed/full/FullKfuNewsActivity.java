package com.kfu.lantimat.kfustudent.Feeds.KfuFeed.full;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kfu.lantimat.kfustudent.Feeds.FullFeeds.AppbarImagesAdapter;
import com.kfu.lantimat.kfustudent.Feeds.FullFeeds.FullFeedAppBarItems;
import com.kfu.lantimat.kfustudent.Feeds.FullNews.FullNewsItem;
import com.kfu.lantimat.kfustudent.Feeds.FullNews.FullNewsRecyclerAdapter;
import com.kfu.lantimat.kfustudent.Feeds.PhotoGallery.PhotoGalleryActivity;
import com.kfu.lantimat.kfustudent.MediaKfuRestClient;
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
import me.relex.circleindicator.CircleIndicator;

public class FullKfuNewsActivity extends AppCompatActivity {

    private TextView textView;
    private ProgressBar progressBar;
    private HtmlTextView htmlTextView;
    private RecyclerView recyclerView;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private CoordinatorLayout coordinatorLayout;
    private ViewPager pager;
    private ImageView imageView;

    private FullNewsRecyclerAdapter adapter;
    private ArrayList<FullNewsItem> ar = new ArrayList<>();


    private String title;
    private String photoGalleryUrl = "";
    private ArrayList<FullFeedAppBarItems> appbarImagesUrl = new ArrayList<>();
    private String videoType;
    private static int currentPage = 0;

    //Toolbar back button click
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MyMaterialTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_feed);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setStatusBarTranslucent(true);
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
                Intent intent = new Intent(FullKfuNewsActivity.this, PhotoGalleryActivity.class);
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

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
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

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        pager.setAdapter(new AppbarImagesAdapter(this, ar));
        indicator.setViewPager(pager);
        if(ar.size() == 0) indicator.setVisibility(View.INVISIBLE);

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
        /*swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 3500);*/

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void getDate(String url) {
        progressBar.setVisibility(View.VISIBLE);
        MediaKfuRestClient.getUrl(url, null, new AsyncHttpResponseHandler() {
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


            title = doc.select("div.newsCart-title").text();
            collapsingToolbar.setTitle(title);

            //Парсим часть со слайдера
            Elements appbarImages = doc.select("ul.uk-slider").select("li"); //Считываем данные слайдера
            if(appbarImages.size() == 0) appbarImages = doc.select("article");
            for (Element e : appbarImages) {
                    String img = e.select("img").attr("src"); //Ссылка на картинку
                    appbarImagesUrl.add(new FullFeedAppBarItems(FullFeedAppBarItems.IMAGE, img));
            }

            initAppbarSlider(appbarImagesUrl);

            ar.add(new FullNewsItem(FullNewsItem.TEXT, doc.select("div.summary").toString()));


            //Парсим часть с текстом
            Elements textRow = doc.select("div.body-text");
            for (Element element : textRow.get(0).children().get(0).children()) {
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

            //Инфо о авторе
            ar.add(new FullNewsItem(FullNewsItem.TEXT, doc.select("div.news-autor").toString()));


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
