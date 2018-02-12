package com.kfu.lantimat.kfustudent.CustomSchedule;

import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Schedule;

/**
 * Created by lAntimat on 11.02.2018.
 */

public interface CustomScheduleMVP {

    interface View {
    void showData(Schedule schedule);
    }

    interface presenter {
        void attachVIew(View view);
        void detachView();
        void getData(int week);
        void addData();
    }

}
