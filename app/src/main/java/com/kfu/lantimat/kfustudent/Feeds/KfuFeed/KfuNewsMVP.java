package com.kfu.lantimat.kfustudent.Feeds.KfuFeed;

import com.kfu.lantimat.kfustudent.Feeds.Feed;
import com.kfu.lantimat.kfustudent.LoadingView;

import java.util.ArrayList;


/**
 * Created by lAntimat on 24.11.2017.
 */

public interface KfuNewsMVP {

    interface View extends LoadingView {
        void showData(ArrayList<KfuNews> ar);

        void startFeedActivity(String url, String img);
    }

    interface Presenter {
        void loadData();
        void refreshData();
    }

}
