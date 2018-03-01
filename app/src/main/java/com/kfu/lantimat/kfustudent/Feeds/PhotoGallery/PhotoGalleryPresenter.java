package com.kfu.lantimat.kfustudent.Feeds.PhotoGallery;

import com.kfu.lantimat.kfustudent.Feeds.Repository;
import com.kfu.lantimat.kfustudent.utils.Constants;

import java.util.ArrayList;


/**
 * Created by lAntimat on 24.11.2017.
 */

public class PhotoGalleryPresenter {

    private PhotoGalleryView view;
    private Repository repository;
    private int page = 1;
    private boolean isLoading = false;
    private boolean isNoMore = false;
    private String loadMoreUrl = Constants.urlStudProf;

    public PhotoGalleryPresenter(PhotoGalleryView view) {
        this.view = view;
    }

    public void loadDate(String url) {
        if(!isLoading) {
            view.showLoading();
            isLoading = true;
            Repository.webViewGetImages(url, new Repository.PhotoGalleryCallback() {

                @Override
                public void onSuccess(ArrayList<PhotoGalleryItem> ar) {
                    view.hideLoading();
                    view.showPhotos(ar);
                    page++;
                    isLoading = false;
                }

                @Override
                public void loadCount(int loaded, int size) {
                    if(loaded >= size) isNoMore = true;
                    else isNoMore = false;
                    view.setLoadedCount(loaded, size);
                }

                @Override
                public void onFailure(Throwable error) {
                    isLoading = false;
                    view.hideLoading();
                    view.showError("Ошибка загрузки данных" + error.getLocalizedMessage());
                }
            });
        }
    }

    public void loadMore() {
        if(!isLoading & !isNoMore) {
            view.showToolbarLoading();
            Repository.webViewGetMore();
        }
    }

    public void openFullSizeImage(int position, ArrayList<PhotoGalleryItem> ar) {
        view.openFullSizePhoto(position, ar);
    }
}
