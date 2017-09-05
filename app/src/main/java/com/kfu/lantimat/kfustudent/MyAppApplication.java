package com.kfu.lantimat.kfustudent;

import android.app.Application;

/**
 * Created by GabdrakhmanovII on 05.09.2017.
 */

public class MyAppApplication extends Application {

    private String mScheduleUrl;

    public String getScheduleUrl() {
        return mScheduleUrl;
    }

    public void setScheduleUrl(String url) {
        mScheduleUrl = url;
    }
}
