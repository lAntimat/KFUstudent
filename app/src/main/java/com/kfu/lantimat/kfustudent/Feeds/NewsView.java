package com.kfu.lantimat.kfustudent.Feeds;

import com.kfu.lantimat.kfustudent.LoadingView;

import java.util.ArrayList;

/**
 * Created by lAntimat on 24.11.2017.
 */

public interface NewsView extends LoadingView {

    void showNews(ArrayList<News> ar);

    void startNewsActivity(String url);

}
