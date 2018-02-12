package com.kfu.lantimat.kfustudent.CustomSchedule.Models;

import java.util.ArrayList;

/**
 * Created by lAntimat on 11.02.2018.
 */

public class Day {
    int dayNumber;
    ArrayList<Subject> subjects;

    public Day() {
    }

    public Day(ArrayList<Subject> subjects) {
        this.subjects = subjects;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<Subject> subjects) {
        this.subjects = subjects;
    }
}
