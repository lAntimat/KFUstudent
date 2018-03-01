package com.kfu.lantimat.kfustudent.Feeds;

import com.kfu.lantimat.kfustudent.R;

import java.util.ArrayList;

/**
 * Created by lAntimat on 24.11.2017.
 */

public class FeedPresenter {

    private FeedView view;
    private Repository repository;
    private int page = 1;
    private boolean isLoading = false;
    private static boolean isOnRefresh = false;

    public FeedPresenter(FeedView view) {
        this.view = view;
    }

    public void loadDate() {
        if(!isLoading) {
            if(!isOnRefresh) view.showLoading();
            isLoading = true;
            Repository.getFeeds(page, new Repository.FeedCallback() {
                @Override
                public void onSuccess(ArrayList<Feed> ar) {
                    view.hideLoading();
                    view.showFeeds(ar);
                    page++;
                    isLoading = false;
                    isOnRefresh = false;
                }

                @Override
                public void onFailure(Throwable error) {
                    view.hideLoading();
                    isLoading = false;
                    isOnRefresh = false;
                    view.showError(R.string.error_connect + error.getLocalizedMessage());
                }
            });
        }
    }

    public void refreshDate() {
            page = 1;
            isOnRefresh = true;
            loadDate();

    }
}
