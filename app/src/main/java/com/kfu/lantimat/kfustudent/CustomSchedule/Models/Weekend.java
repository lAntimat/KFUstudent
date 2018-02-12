package com.kfu.lantimat.kfustudent.CustomSchedule.Models;

import java.util.ArrayList;

/**
 * Created by lAntimat on 11.02.2018.
 */

public class Weekend {
    int weekNumber;
    ArrayList<Day> arDays;

    public Weekend() {
    }

    public Weekend(ArrayList<Day> arDays) {
        this.arDays = arDays;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public ArrayList<Day> getArDays() {
        return arDays;
    }

    public void setArDays(ArrayList<Day> arDays) {
        this.arDays = arDays;
    }
}
