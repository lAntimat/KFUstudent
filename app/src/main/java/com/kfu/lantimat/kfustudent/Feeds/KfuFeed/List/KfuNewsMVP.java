package com.kfu.lantimat.kfustudent.Feeds.KfuFeed.List;

import android.content.Context;
import android.content.Intent;

import com.kfu.lantimat.kfustudent.LoadingView;

import java.util.ArrayList;


/**
 * Created by lAntimat on 24.11.2017.
 */

public interface KfuNewsMVP {

    interface View extends LoadingView {
        void showData(ArrayList<KfuNews> ar);
        void startFeedActivity(Intent intent);
    }

    interface Presenter {
        void attachView(Context context, View view);
        void detachView();
        void loadData();
        void refreshData();
        void recyclerClick(int position);
    }

}
