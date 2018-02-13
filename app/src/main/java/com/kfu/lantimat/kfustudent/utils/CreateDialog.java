package com.kfu.lantimat.kfustudent.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by GabdrakhmanovII on 09.02.2018.
 */

public class CreateDialog {
    public static MaterialDialog createPleaseWaitDialog(Context context) {
        return new MaterialDialog.Builder(context)
                .content("Пожалуйста, подождите!")
                .progress(true, 0)
                .show();
    }
}
