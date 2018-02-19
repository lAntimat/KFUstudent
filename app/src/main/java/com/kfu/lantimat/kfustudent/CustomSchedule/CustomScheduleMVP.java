package com.kfu.lantimat.kfustudent.CustomSchedule;

import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Schedule;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Weekend;

import org.joda.time.LocalDate;

import java.util.Date;

/**
 * Created by lAntimat on 11.02.2018.
 */

public interface CustomScheduleMVP {

    interface View {
    void showData(Weekend weekend);
    void updateDataTextView(LocalDate localDate);
    void openSubjectInfo(Schedule schedule, int position, int day);
    }

    interface presenter {
        void attachVIew(View view);
        void detachView();
        void getData(int week);
        void addData();
        void nextWeek();
        void prevWeek();
        void recyclerItemClick(int position, int day);
    }

}
