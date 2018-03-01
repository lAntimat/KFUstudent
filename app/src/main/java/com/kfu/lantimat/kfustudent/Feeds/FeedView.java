package com.kfu.lantimat.kfustudent.Feeds;

import com.kfu.lantimat.kfustudent.LoadingView;

import java.util.ArrayList;


/**
 * Created by lAntimat on 24.11.2017.
 */

public interface FeedView extends LoadingView {

    void showFeeds(ArrayList<Feed> ar);

    void startFeedActivity(String url, String img);

}
