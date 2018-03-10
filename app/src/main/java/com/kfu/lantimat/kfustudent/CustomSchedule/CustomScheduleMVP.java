package com.kfu.lantimat.kfustudent.CustomSchedule;

import android.content.Intent;

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
    void openSubjectInfo(Intent intent);
    void openAddSubject(Schedule schedule);
    void showLoading();
    void hideLoading();
    void showError(String str);
    void onOfflineMode(boolean isOfflineData);
    }

    interface presenter {
        void attachVIew(View view);
        void detachView();
        void getData();
        void addData();
        void nextWeek();
        void prevWeek();
        void recyclerItemClick(int position, int day);
        void fabCLick();
    }

}
