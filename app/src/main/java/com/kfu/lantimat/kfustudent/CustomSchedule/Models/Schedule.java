package com.kfu.lantimat.kfustudent.CustomSchedule.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by lAntimat on 11.02.2018.
 */

public class Schedule implements Parcelable {
    String scheduleId;
    String groupNumber;
    ArrayList<Weekend> arWeekends;
    ArrayList<HomeWorks> arHomeworks;

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.scheduleId);
        dest.writeString(this.groupNumber);
        dest.writeList(this.arWeekends);
    }

    protected Schedule(Parcel in) {
        this.scheduleId = in.readString();
        this.groupNumber = in.readString();
        this.arWeekends = new ArrayList<Weekend>();
        in.readList(this.arWeekends, Weekend.class.getClassLoader());
    }

    public static final Parcelable.Creator<Schedule> CREATOR = new Parcelable.Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel source) {
            return new Schedule(source);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };
}
