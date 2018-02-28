package com.kfu.lantimat.kfustudent.Feeds.FullNews;

/**
 * Created by lAntimat on 03.12.2017.
 */

public class FullNewsItem {

    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    public static final int VIDEO = 2;

    int type;
    String str;

    public FullNewsItem(int type, String str) {
        this.type = type;
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
}
