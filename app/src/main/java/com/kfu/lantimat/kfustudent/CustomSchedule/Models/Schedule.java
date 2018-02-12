package com.kfu.lantimat.kfustudent.CustomSchedule.Models;

import java.util.ArrayList;

/**
 * Created by lAntimat on 11.02.2018.
 */

public class Schedule {
    String scheduleId;
    String groupNumber;
    ArrayList<Weekend> arWeekends;

    public Schedule() {
    }

    public Schedule(String groupNumber, ArrayList<Weekend> arWeekends) {
        this.groupNumber = groupNumber;
        this.arWeekends = arWeekends;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public ArrayList<Weekend> getArWeekends() {
        return arWeekends;
    }

    public void setArWeekends(ArrayList<Weekend> arWeekends) {
        this.arWeekends = arWeekends;
    }
}
