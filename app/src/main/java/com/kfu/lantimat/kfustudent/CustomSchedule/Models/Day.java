package com.kfu.lantimat.kfustudent.CustomSchedule.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by lAntimat on 11.02.2018.
 */

public class Day implements Parcelable {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.dayNumber);
        dest.writeList(this.subjects);
    }

    protected Day(Parcel in) {
        this.dayNumber = in.readInt();
        this.subjects = new ArrayList<Subject>();
        in.readList(this.subjects, Subject.class.getClassLoader());
    }

    public static final Parcelable.Creator<Day> CREATOR = new Parcelable.Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel source) {
            return new Day(source);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };
}
