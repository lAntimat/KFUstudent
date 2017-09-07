package com.kfu.lantimat.kfustudent.Timeline;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.kfu.lantimat.kfustudent.KFURestClient;
import com.kfu.lantimat.kfustudent.MainActivity;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.Timeline.model.OrderStatus;
import com.kfu.lantimat.kfustudent.Timeline.model.Orientation;
import com.kfu.lantimat.kfustudent.Timeline.model.TimeLineModel;
import com.kfu.lantimat.kfustudent.utils.CheckAuth;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class TimeLineActivity extends MainActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> mDataList = new ArrayList<>();
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    int positionForScroll = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_timeline);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //if(getSupportActionBar()!=null)
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FrameLayout v = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_timeline, v);

        ButterKnife.bind(this);

        //mOrientation = (Orientation) getIntent().getSerializableExtra(MainActivity.EXTRA_ORIENTATION);
        //mWithLinePadding = getIntent().getBooleanExtra(MainActivity.EXTRA_WITH_LINE_PADDING, false);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Таймлайн");

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        mRecyclerView.setHasFixedSize(true);

        initView();
        getEventTimeLine();

        PersistentCookieStore myCookieStore = new PersistentCookieStore(this); /**Обязательно один раз нужно задать CookieStore*/
        KFURestClient.setCookieStore(myCookieStore);
        new CheckAuth(getApplicationContext(), new CheckAuth.AuthCallback() {
            @Override
            public void onLoggedIn() {
                setupNavigationDrawer();
            }

            @Override
            public void onNotLoggedIn() {
                setupNavigationDrawer();
            }

            @Override
            public void onOldSession() {
                setupNavigationDrawer();
            }
        });

        result.setSelection(1, false);

    }

    private LinearLayoutManager getLinearLayoutManager() {
        if (mOrientation == Orientation.HORIZONTAL) {
            return new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        } else {
            return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
    }

    private void initView() {
        //setDataListItems();
        mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }

    private void getEventTimeLine() {
        KFURestClient.getUrl("https://media.kpfu.ru/events/month", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                new ParseStrFromByte().execute(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


    private void setDataListItems() {
        mDataList.add(new TimeLineModel("Item successfully delivered", "", OrderStatus.INACTIVE));
        mDataList.add(new TimeLineModel("Courier is out to delivery your order", "2017-02-12 08:00", OrderStatus.ACTIVE));
        mDataList.add(new TimeLineModel("Item has reached courier facility at New Delhi", "2017-02-11 21:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Item has been given to the courier", "2017-02-11 18:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Item is packed and will dispatch soon", "2017-02-11 09:30", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order is being readied for dispatch", "2017-02-11 08:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order processing initiated", "2017-02-10 15:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order confirmed by seller", "2017-02-10 14:30", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order placed successfully", "2017-02-10 14:00", OrderStatus.COMPLETED));
    }

    public class ParseStrFromByte extends AsyncTask<byte[], Void, Void> {
        @Override
        protected Void doInBackground(byte[]... params) {

            String str = null;

            //str = new String(params[0], "UTF-8");
            str = new String(params[0]);

            Document doc = Jsoup.parse(str);
            Elements elements = doc.select("div.eventItem.uk-clearfix");

            Log.d("event-list.uk-active", elements.get(2).toString());

            Calendar calendar = Calendar.getInstance();
            Date dateCalendar = calendar.getTime();
            String full = new SimpleDateFormat("dd.MM.yyyy").format(dateCalendar);
            for (int i = 0; i < elements.size(); i++) {
                String date = elements.get(i).select("div.eventItem-date").text();
                String title = elements.get(i).select("div.eventItem-title").text();
                String place = elements.get(i).select("div.eventItem-place").text();
                String format = elements.get(i).select("div.eventItem-format").text();
                mDataList.add(new TimeLineModel(date, title, place, format, OrderStatus.COMPLETED));
                if(date.contains(full)) positionForScroll = i;
                full = "";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.INVISIBLE);
            mTimeLineAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(positionForScroll);
            super.onPostExecute(aVoid);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Menu
        switch (item.getItemId()) {
            //When home is clicked
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (mOrientation != null)
            savedInstanceState.putSerializable(MainActivity.EXTRA_ORIENTATION, mOrientation);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MainActivity.EXTRA_ORIENTATION)) {
                mOrientation = (Orientation) savedInstanceState.getSerializable(MainActivity.EXTRA_ORIENTATION);
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
