package com.kfu.lantimat.kfustudent.Feeds.PhotoGallery;

import com.kfu.lantimat.kfustudent.LoadingView;

import java.util.ArrayList;

/**
 * Created by lAntimat on 24.11.2017.
 */

public interface PhotoGalleryView extends LoadingView {

    void showPhotos(ArrayList<PhotoGalleryItem> arPhotos);

    void openFullSizePhoto(int position, ArrayList<PhotoGalleryItem> ar);

    void setLoadedCount(int loadedCount, int size);

    void showToolbarLoading();

    void onAdd(ArrayList<PhotoGalleryItem> ar);

    void setPosition(int position);
}
