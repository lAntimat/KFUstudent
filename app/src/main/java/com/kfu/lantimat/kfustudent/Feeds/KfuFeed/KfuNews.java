package com.kfu.lantimat.kfustudent.Feeds.KfuFeed;

/**
 * Created by GabdrakhmanovII on 04.09.2017.
 */

public class KfuNews {

    public static final int FIXED_TYPE = 0;
    public static final int NORMAL_TYPE = 1;

    public String title;
    public String subTitle;
    public String date;
    public String image;
    public String url;
    public String visitCount;
    public String commentCount;
    public boolean withVideo;
    public int viewType;

    public KfuNews(String title, String subTitle, String date, String image, String url, String visitCount, boolean withVideo) {
        this.title = title;
        this.subTitle = subTitle;
        this.date = date;
        this.image = image;
        this.url = url;
        this.visitCount = visitCount;
        this.withVideo = withVideo;
    }

    public int getViewType() {
        return viewType;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(String visitCount) {
        this.visitCount = visitCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isWithVideo() {
        return withVideo;
    }

    public void setWithVideo(boolean withVideo) {
        this.withVideo = withVideo;
    }
}
