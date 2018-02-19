package com.kfu.lantimat.kfustudent.CustomSchedule.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lAntimat on 11.02.2018.
 */

public class Subject implements Parcelable {

    Date startTime;
    Date endTime;
    String subjectName;
    ArrayList<String> homeWorks;
    String campusNumber;
    String cabNumber;
    String teacherName;

    int repeatDay = -1;
    int repeatWeek = -1;

    public Subject() {
    }

    public Subject(Date startTime, Date endTime, String subjectName, ArrayList<String> homeWorks, String campusNumber, String cabNumber, String teacherName, int repeatDay, int repeatWeek) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.subjectName = subjectName;
        this.homeWorks = homeWorks;
        this.campusNumber = campusNumber;
        this.cabNumber = cabNumber;
        this.teacherName = teacherName;
        this.repeatDay = repeatDay;
        this.repeatWeek = repeatWeek;
    }

    public int getRepeatDay() {
        return repeatDay;
    }

    public void setRepeatDay(int repeatDay) {
        this.repeatDay = repeatDay;
    }

    public int getRepeatWeek() {
        return repeatWeek;
    }

    public void setRepeatWeek(int repeatWeek) {
        this.repeatWeek = repeatWeek;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCampusNumber() {
        return campusNumber;
    }

    public void setCampusNumber(String campusNumber) {
        this.campusNumber = campusNumber;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public ArrayList<String> getHomeWorks() {
        return homeWorks;
    }

    public void setHomeWorks(ArrayList<String> homeWorks) {
        this.homeWorks = homeWorks;
    }

    public String getCabNumber() {
        return cabNumber;
    }

    public void setCabNumber(String cabNumber) {
        this.cabNumber = cabNumber;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.startTime != null ? this.startTime.getTime() : -1);
        dest.writeLong(this.endTime != null ? this.endTime.getTime() : -1);
        dest.writeString(this.subjectName);
        dest.writeStringList(this.homeWorks);
        dest.writeString(this.campusNumber);
        dest.writeString(this.cabNumber);
        dest.writeString(this.teacherName);
        dest.writeInt(this.repeatDay);
        dest.writeInt(this.repeatWeek);
    }

    protected Subject(Parcel in) {
        long tmpStartTime = in.readLong();
        this.startTime = tmpStartTime == -1 ? null : new Date(tmpStartTime);
        long tmpEndTime = in.readLong();
        this.endTime = tmpEndTime == -1 ? null : new Date(tmpEndTime);
        this.subjectName = in.readString();
        this.homeWorks = in.createStringArrayList();
        this.campusNumber = in.readString();
        this.cabNumber = in.readString();
        this.teacherName = in.readString();
        this.repeatDay = in.readInt();
        this.repeatWeek = in.readInt();
    }

    public static final Parcelable.Creator<Subject> CREATOR = new Parcelable.Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel source) {
            return new Subject(source);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };
}
