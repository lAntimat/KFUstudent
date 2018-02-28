package com.kfu.lantimat.kfustudent.Feeds;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kfu.lantimat.kfustudent.Feeds.PhotoGallery.Photo;
import com.kfu.lantimat.kfustudent.Feeds.PhotoGallery.PhotoGalleryItem;
import com.kfu.lantimat.kfustudent.SpRestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.kfu.lantimat.kfustudent.SpRestClient.urlStudProf;

/**
 * Created by lAntimat on 02.12.2017.
 */

public class Repository {

    public static Context context;
    public static Activity activity;
    private PhotoGalleryCallback photoGalleryCallback;
    private static WebView webView;
    private static String urlFeed = "/feed/index/?page=";
    private static String urlNews = "/news/index/?page=";
    private static String urlPhoto = "/photo/index/?page=";
    private static int feedPage;
    private static int newsPage;
    private static int photosPage;

    private static boolean isLoading = false;
    private static int gallerySize;

    static ArrayList<Feed> arFeed = new ArrayList<>();
    static ArrayList<News> arNews = new ArrayList<>();
    static ArrayList<Photo> arPhoto = new ArrayList<>();
    static ArrayList<PhotoGalleryItem> arPhotoGallery = new ArrayList<>();

    public static void setContext(Context mContext) {
        context = mContext;
    }

    public static void setActivity(Activity mActivity) {
        activity = mActivity;
    }

    public interface FeedCallback {
        void onSuccess(ArrayList<Feed> ar);

        void onFailure(Throwable error);
    }

    public interface NewsCallback {
        void onSuccess(ArrayList<News> ar);

        void onFailure(Throwable error);
    }

    public interface PhotosCallback {
        void onSuccess(ArrayList<Photo> ar);

        void onFailure(Throwable error);

    }

    public interface PhotoGalleryCallback {
        void onSuccess(ArrayList<PhotoGalleryItem> ar);
        void loadCount(int loaded, int size);
        void onFailure(Throwable error);

    }

    public static void getFeeds(int page, final FeedCallback callback) {
        feedPage = page;
        if(page==1) arFeed.clear();
        SpRestClient.get(urlFeed + page, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                parseFeeds(responseBody, callback);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public static void getNews(int page, final NewsCallback callback) {
        newsPage = page;

        SpRestClient.get(urlNews + page, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                parseNews(responseBody, callback);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public static void getPhotos(int page, final PhotosCallback callback) {
        photosPage = page;

        SpRestClient.get(urlPhoto + page, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                parsePhotos(responseBody, callback);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.onFailure(error);
            }
        });
    }

    private static void parseFeeds(byte[] bytes, FeedCallback callback) {
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


            Elements listItemRow = doc.select("article.list-item.row");
            Elements listItemFixed = doc.select("article.list-item-fixed");

            Elements title = listItemRow.select("h3");
            Elements shortDescribe = listItemRow.select("p.mt10");
            Elements tvDate = listItemRow.select("span.list-item-subtitle");
            //picture = doc.select("img[src$=.jpg]");
            Elements picture = listItemRow.select("img");
            //profilePicture = doc.select("img");
            Elements feedUrl = listItemRow.select("a[title]");
            //[0]-верхняя/нижняя, [1]-дата
            Elements dateWeek = doc.select("div.top-date-week");

            Elements countOfVisit = listItemRow.select("div.span10.list-item-body");


            if (feedPage == 1) {
                //Парсим закрепленную запись
                parseEventListItemFixed(listItemFixed);
            }

            // и в цикле захватываем все данные какие есть на странице
            for (int i = 0; i < title.size(); i++) {

                String[] counts = countOfVisit.get(i).text().split(" ");
                String visitCount = counts[1];
                String commentsCount = counts[0];

                String feedUrlString = feedUrl.get(i).attr("href");
                String pictureBig = picture.get(i).attr("src");
                if (pictureBig.contains("min"))
                    pictureBig = pictureBig.replace("_min", "");
                arFeed.add(new Feed(title.get(i).text(), shortDescribe.get(i).text(), tvDate.get(i).text(),
                        pictureBig, feedUrlString, visitCount, commentsCount, false, Feed.NORMAL_TYPE));

            }
        }

        callback.onSuccess(arFeed);
    }

    private static void parseEventListItemFixed(Elements _listItemFixed) {

        try {
            Elements _title = _listItemFixed.select("h3");
            Elements _shortDescribe = _listItemFixed.select("p");
            //Elements _tvDate = _listItemFixed.select("span.list-item-subtitle");
            //picture = doc.select("img[src$=.jpg]");
            Elements _picture = _listItemFixed.select("img");
            //profilePicture = doc.select("img");
            Elements _feedUrl = _listItemFixed.select("a[href$=.html]");

            String feedUrlString = _feedUrl.get(0).attr("href");
            arFeed.add(new Feed(_title.get(0).text(), _shortDescribe.get(0).text(), "Запись закреплена",
                    _picture.get(0).attr("src"), feedUrlString, "", "", false, Feed.FIXED_TYPE));
        } catch (Exception e) {

        }
    }

    private static void parseNews(byte[] bytes, NewsCallback callback) {
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


            Elements listItemRow = doc.select("article.list-item.row");

            for (int i = 0; i < listItemRow.size(); i++) {

                Elements title = listItemRow.get(i).select("h3");
                Elements shortDescribe = listItemRow.get(i).select("p.mt10");
                Elements tvDate = listItemRow.get(i).select("span.list-item-subtitle");
                Elements picture = listItemRow.get(i).select("img");
                Elements feedUrl = listItemRow.get(i).select("a[href]");
                Elements countOfVisit = listItemRow.get(i).select("div.span10.list-item-body");

                String[] counts = countOfVisit.text().split(" ");
                String visitCount = counts[1];
                String commentsCount = counts[0];
                String feedUrlString = feedUrl.attr("href");
                String pictureBig = picture.attr("src");

                if (pictureBig.contains("min"))
                    pictureBig = pictureBig.replace("_min", "");
                arNews.add(new News(title.text(), shortDescribe.text(), tvDate.text(),
                        pictureBig, feedUrlString, visitCount, commentsCount, false, Feed.NORMAL_TYPE));

            }
        }

        callback.onSuccess(arNews);
    }

    private static void parsePhotos(byte[] bytes, PhotosCallback callback) {
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


            Elements listItemRow = doc.select("article.row.photo-item");

            for (int i = 0; i < listItemRow.size(); i++) {

                String title = listItemRow.get(i).select("h3").text();
                Elements textRow = listItemRow.get(i).select("div.tbg.photo-item-description");
                Elements shortDescribe = textRow.select("p");
                String shortDescString = "";
                if (shortDescribe.size() > 0) {
                    shortDescString = shortDescribe.get(0).text();
                    if (!shortDescribe.select("em").isEmpty())
                        shortDescString += "\n\n" + shortDescribe.select("em").get(0).text();
                }
                String date = textRow.select("span").get(0).text();
                String visitCount = textRow.select("span").get(3).text();
                String photosCount = textRow.select("span").get(1).text();
                String commentsCount = textRow.select("span").get(2).text();
                String photoAlbumUrl = listItemRow.get(i).select("a").attr("href");

                ArrayList<String> arImages = new ArrayList<>();
                Elements images = listItemRow.get(i).select("div.row").select("div").select("img");
                for (Element url : images) {
                    arImages.add(url.attr("src"));
                }

                arPhoto.add(new Photo(title, shortDescString, date,
                        arImages, photoAlbumUrl, photosCount, visitCount, commentsCount));
            }

            callback.onSuccess(arPhoto);

        }

    }

    private static void parsePhotoGallery(Document doc, final PhotoGalleryCallback callback) {
        //Document doc = null;//Здесь хранится будет разобранный html документ
        try {

        //    String str = new String(bytes, "utf-8");
            //doc = Jsoup.parse(str);
        } catch (Exception e) {
            //Если не получилось считать
            e.printStackTrace();

        }

        //Если всё считалось, что вытаскиваем из считанного html документа заголовок
        if (doc != null) {
            arPhotoGallery.clear();
            Elements titleRow = doc.select("div.pull-right.list-item-icons");
            gallerySize = Integer.parseInt(titleRow.select("span").get(0).text());

            Elements listItemRow = doc.select("div.report-photos").select("a");
            for (int i = 0; i < listItemRow.size(); i++) {

                String imgUrl = listItemRow.get(i).select("img").attr("src");
                String imgUrlBig = imgUrl.replace("min_", "");

                arPhotoGallery.add(new PhotoGalleryItem(imgUrl, imgUrlBig));
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(arPhotoGallery);
                    if(arPhotoGallery.size() != 0) callback.loadCount(arPhotoGallery.size(), gallerySize);
                    isLoading = false;
                }
            });

        }

    }

    public static void webViewGetImages(String url, PhotoGalleryCallback callback){
        webView = new WebView(context);
        //webView.setWebViewClient(new MeWebViewClient());
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setJavaScriptEnabled(true);

        MyJavaScriptInterface myJavaScriptInterface = new MyJavaScriptInterface(callback);
        webView.addJavascriptInterface(myJavaScriptInterface, "HtmlHandler");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(webView, url);
                //webView.loadUrl("javascript:(function(){loadPhotos().click();})()");
                webView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                //parseFeed();
            }
        });

        webView.loadUrl(url);
    }

    public static void webViewGetMore() {

        if(arPhotoGallery.size() < gallerySize) {
            isLoading = true;
            webView.loadUrl("javascript:(function(){loadPhotos().click();})()");
            webView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                    "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
        }
    }

    public static class MyJavaScriptInterface {

        static PhotoGalleryCallback callback;
        public MyJavaScriptInterface(PhotoGalleryCallback mCallback) {
            callback = mCallback;
        }

        @JavascriptInterface
        public void handleHtml(String html) {
            arPhotoGallery.clear();
            // Use jsoup on this String here to search for your content.
            final Document doc = Jsoup.parse(html);
            parsePhotoGallery(doc, callback);

        }
    }


}


