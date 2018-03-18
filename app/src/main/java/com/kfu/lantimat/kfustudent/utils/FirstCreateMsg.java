package com.kfu.lantimat.kfustudent.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.kfu.lantimat.kfustudent.MainIntroActivity;
import com.kfu.lantimat.kfustudent.Schedule.ScheduleActivity;

/**
 * Created by lAntimat on 18.03.2018.
 */

public class FirstCreateMsg {

    public static void openIntro(Context context) {
        SharedPreferences sp = context.getSharedPreferences("scheduleIntro",
                Context.MODE_PRIVATE);
        // проверяем, первый ли раз открывается программа
        boolean hasVisited = sp.getBoolean("hasVisited", false);

        if (!hasVisited) {
            // выводим нужную активность
            context.startActivity(new Intent(context, MainIntroActivity.class));
            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("hasVisited", true);
            e.apply(); // не забудьте подтвердить изменения

        }
    }
}
