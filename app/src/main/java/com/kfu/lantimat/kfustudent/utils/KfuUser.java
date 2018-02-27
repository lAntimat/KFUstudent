package com.kfu.lantimat.kfustudent.utils;

import android.content.Context;
import android.text.TextUtils;

import com.kfu.lantimat.kfustudent.SharedPreferenceHelper;

import static com.kfu.lantimat.kfustudent.LoginActivity.LOGIN;

/**
 * Created by lAntimat on 27.02.2018.
 */

public class KfuUser {

    public static String getLogin(Context context) {
        String login = SharedPreferenceHelper.getSharedPreferenceString(context, LOGIN, "");
        if(!TextUtils.isEmpty(login)) {
            return login;
        } else {
            return null;
        }
    }

    public static String getGroup(Context context) {
        String group = SharedPreferenceHelper.getSharedPreferenceString(context, CheckAuth.GROUP, "");
        if(!TextUtils.isEmpty(group)) {
            return group;
        } else {
            return null;
        }
    }

    public static String getName(Context context) {
        String group = SharedPreferenceHelper.getSharedPreferenceString(context, CheckAuth.FULL_NAME, "");
        if(!TextUtils.isEmpty(group)) {
            return group;
        } else {
            return null;
        }
    }

    public static String getImgUrl(Context context) {
        String group = SharedPreferenceHelper.getSharedPreferenceString(context, CheckAuth.IMG_URL, "");
        if(!TextUtils.isEmpty(group)) {
            return group;
        } else {
            return "";
        }
    }

}
