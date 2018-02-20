package com.kfu.lantimat.kfustudent.CustomSchedule.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by GabdrakhmanovII on 20.02.2018.
 */

public class HomeWorks implements Parcelable {

    String id;
    String subjectName;
    ArrayList<String> arHomeworks;

    public HomeWorks() {
    }

    public HomeWorks(String subjectName, ArrayList<String> arHomeworks) {
        this.subjectName = subjectName;
        this.arHomeworks = arHomeworks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public ArrayList<String> getArHomeworks() {
        return arHomeworks;
    }

    public void setArHomeworks(ArrayList<String> arHomeworks) {
        this.arHomeworks = arHomeworks;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.subjectName);
        dest.writeStringList(this.arHomeworks);
    }

    protected HomeWorks(Parcel in) {
        this.id = in.readString();
        this.subjectName = in.readString();
        this.arHomeworks = in.createStringArrayList();
    }

    public static final Creator<HomeWorks> CREATOR = new Creator<HomeWorks>() {
        @Override
        public HomeWorks createFromParcel(Parcel source) {
            return new HomeWorks(source);
        }

        @Override
        public HomeWorks[] newArray(int size) {
            return new HomeWorks[size];
        }
    };
}
