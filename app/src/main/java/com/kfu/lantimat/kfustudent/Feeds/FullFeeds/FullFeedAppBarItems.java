package com.kfu.lantimat.kfustudent.Feeds.FullFeeds;

/**
 * Created by lAntimat on 03.12.2017.
 */

public class FullFeedAppBarItems {

    public static final int IMAGE = 0;
    public static final int VIDEO = 1;
    final static String youTube = "1";
    final static String vimeo = "3";
    String youTubeText = "http://youtu.be/";
    String vimeoUrl = "https://vimeo.com/";

    int type;
    String videoType;
    String videoData;
    String str;

    public FullFeedAppBarItems(int type, String str) {
        this.type = type;
        this.str = str;
    }

    public FullFeedAppBarItems(int type, String videoType, String videoData, String str) {
        this.type = type;
        this.videoType = videoType;
        this.videoData = videoData;
        this.str = str;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getVideo() {
        if(videoType.equals(youTube)) return youTubeText + videoData;
        else if(videoType.equals(vimeo)) return vimeoUrl + videoData;
        else return "";
    }
}
