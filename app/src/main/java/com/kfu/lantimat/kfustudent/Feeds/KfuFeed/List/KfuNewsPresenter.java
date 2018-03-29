package com.kfu.lantimat.kfustudent.Feeds.KfuFeed.List;

import android.content.Context;
import android.content.Intent;

import com.kfu.lantimat.kfustudent.Feeds.FullFeeds.FullFeedsActivity;
import com.kfu.lantimat.kfustudent.Feeds.KfuFeed.full.FullKfuNewsActivity;
import com.kfu.lantimat.kfustudent.Feeds.Repository;
import com.kfu.lantimat.kfustudent.MediaKfuRestClient;
import com.kfu.lantimat.kfustudent.utils.Constants;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by lAntimat on 24.11.2017.
 */

public class KfuNewsPresenter implements KfuNewsMVP.Presenter {

    private Context context;
    private KfuNewsMVP.View view;
    private Repository repository;
    private int page = 0;
    private boolean isLoading = false;
    private static boolean isOnRefresh = false;
    private ArrayList<KfuNews> ar = new ArrayList<>();

    public KfuNewsPresenter() {
    }

    private void parseDate(byte[] bytes) {
        Document doc = null;//Здесь хранится будет разобранный html документ
        try {

            String str = new String(bytes, "utf-8");
            doc = Jsoup.parse(str);
        } catch (Exception e) {
            //Если не получилось считать
            e.printStackTrace();

        }

        //Если всё считалось, что вытаскиваем из считанного html документа заголовок
        if (doc != null) {
            // задаем с какого места, я выбрал заголовке статей


            Elements listItemRow = doc.select("div.newsItem");

            Elements title = listItemRow.select("a.boldLink");
            Elements shortDescribe = listItemRow.select("div.newsItem-text");
            Elements tvDate = listItemRow.select("div.newsItem-date");
            //picture = doc.select("img[src$=.jpg]");
            Elements picture = listItemRow.select("div.newsItem-photo");
            //profilePicture = doc.select("img");
            Elements feedUrl = listItemRow.select("a.newsItem-readmore");
            //[0]-верхняя/нижняя, [1]-дата
            //Elements dateWeek = doc.select("div.top-date-week");

            Elements countOfVisit = listItemRow.select("div.newsItem-views.viewsIco");

            // и в цикле захватываем все данные какие есть на странице
            for (int i = 0; i < title.size(); i++) {

                String visitCount = countOfVisit.get(i).text();

                String feedUrlString = feedUrl.get(i).attr("href");
                feedUrlString = MediaKfuRestClient.BASE_URL + feedUrlString;
                String pictureBig = picture.get(i).toString().replace("<div class=\"newsItem-photo\"> \n" +
                        " <a href=\"#\" style=\"background:url(/", "").replace("); background-size: cover\"></a> \n" +
                        "</div>", "");
                pictureBig = Constants.url_kfu_media + pictureBig;
                ar.add(new KfuNews(title.get(i).text(), shortDescribe.get(i).text(), tvDate.get(i).text(),
                        pictureBig, feedUrlString, visitCount, false));
            }

            view.showData(ar);
            view.hideLoading();
            page++;
            isLoading = false;
            isOnRefresh = false;
        }


    }

    @Override
    public void attachView(Context context, KfuNewsMVP.View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void detachView() {

    }

    @Override
    public void loadData() {
        if(!isLoading) {
            if(!isOnRefresh) view.showLoading();
            isLoading = true;
            RequestParams requestParams = new RequestParams();
            //requestParams.add();
            MediaKfuRestClient.get("news?page=" + page, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    parseDate(responseBody);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    view.showError("Ошибка " + statusCode);
                }
            });
        }
    }

    @Override
    public void refreshData() {
        page = 0;
        isOnRefresh = true;
        loadData();
    }

    @Override
    public void recyclerClick(int position) {
        Intent intent = new Intent(context, FullKfuNewsActivity.class);
        intent.putExtra("url", ar.get(position).getUrl());
        intent.putExtra("img", ar.get(position).getImage());
        view.startFeedActivity(intent);
    }
}
