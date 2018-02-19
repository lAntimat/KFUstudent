package com.kfu.lantimat.kfustudent.CustomSchedule.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by lAntimat on 11.02.2018.
 */

public class Weekend implements Parcelable {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.weekNumber);
        dest.writeTypedList(this.arDays);
    }

    protected Weekend(Parcel in) {
        this.weekNumber = in.readInt();
        this.arDays = in.createTypedArrayList(Day.CREATOR);
    }

    public static final Parcelable.Creator<Weekend> CREATOR = new Parcelable.Creator<Weekend>() {
        @Override
        public Weekend createFromParcel(Parcel source) {
            return new Weekend(source);
        }

        @Override
        public Weekend[] newArray(int size) {
            return new Weekend[size];
        }
    };
}
