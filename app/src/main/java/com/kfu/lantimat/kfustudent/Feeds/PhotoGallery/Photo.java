package com.kfu.lantimat.kfustudent.Feeds.PhotoGallery;

import java.util.ArrayList;

/**
 * Created by lAntimat on 04.12.2017.
 */

public class Photo {
    private String title;
    private String subTitle;
    private String data;
    private ArrayList<String> mImgUrls;
    private String albumUrl;
    private String countOfPhotos;
    private String countOfVisit;
    private String countOfComment;

    public Photo(String title, String shortDescription, String mData, ArrayList<String> mImgUrls, String mAlbumUrl, String mCountOfPhotos, String mCountOfVisit, String countOfComment) {
        this.title = title;
        this.subTitle = shortDescription;
        this.data = mData;
        this.mImgUrls = mImgUrls ;
        this.albumUrl = mAlbumUrl;
        this.countOfPhotos = mCountOfPhotos;
        this.countOfVisit = mCountOfVisit;
        this.countOfComment = countOfComment;
    }

    public ArrayList<String> getmImgUrls() {
        return mImgUrls;
    }

    public void setmImgUrls(ArrayList<String> mImgUrls) {
        this.mImgUrls = mImgUrls;
    }

    public String getCountOfComment() {
        return countOfComment;
    }

    public void setCountOfComment(String countOfComment) {
        this.countOfComment = countOfComment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ArrayList<String> getImgUrls() {
        return mImgUrls;
    }

    public void setImgUrl(ArrayList<String> mImgUrl) {
        this.mImgUrls = mImgUrl;
    }

    public String getAlbumUrl() {
        return albumUrl;
    }

    public void setAlbumUrl(String albumUrl) {
        this.albumUrl = albumUrl;
    }

    public String getCountOfPhotos() {
        return countOfPhotos;
    }

    public void setCountOfPhotos(String countOfPhotos) {
        this.countOfPhotos = countOfPhotos;
    }

    public String getCountOfVisit() {
        return countOfVisit;
    }

    public void setCountOfVisit(String countOfVisit) {
        this.countOfVisit = countOfVisit;
    }
}
