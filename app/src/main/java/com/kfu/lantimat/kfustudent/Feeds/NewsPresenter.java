package com.kfu.lantimat.kfustudent.Feeds;

import java.util.ArrayList;

/**
 * Created by lAntimat on 24.11.2017.
 */

public class NewsPresenter {

    private NewsView view;
    private Repository repository;
    private int page = 1;
    private boolean isLoading = false;

    public NewsPresenter(NewsView view) {
        this.view = view;
    }

    public void loadDate() {
        if(!isLoading) {
            view.showLoading();
            isLoading = true;
            Repository.getNews(page, new Repository.NewsCallback() {
                @Override
                public void onSuccess(ArrayList<News> ar) {
                    view.hideLoading();
                    view.showNews(ar);
                    page++;
                    isLoading = false;
                }

                @Override
                public void onFailure(Throwable error) {
                    view.showError("Ошибка загрузки данных" + error.getLocalizedMessage());
                }
            });
        }
    }
}
