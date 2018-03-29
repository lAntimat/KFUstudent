package com.kfu.lantimat.kfustudent.CustomSchedule.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lAntimat on 11.02.2018.
 */

public class Subject implements Parcelable, Comparable<Subject> {

    String id;
    Date startTime;
    Date endTime;
    Date startDate;
    Date endDate;
    String subjectName;
    String subjectType;
    String campusNumber;
    String cabNumber;
    String teacherName;
    ArrayList<Date> arCustomDates;
    ArrayList<String> arHomeWorks;

    int repeatDay = -1;
    int repeatWeek = -1;

    public Subject() {
    }

    public Subject(Date startTime, Date endTime, Date startDate, Date endDate, String subjectName, String subjectType, String campusNumber, String cabNumber, String teacherName, int repeatDay, int repeatWeek) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.subjectName = subjectName;
        this.subjectType = subjectType;
        this.campusNumber = campusNumber;
        this.cabNumber = cabNumber;
        this.teacherName = teacherName;
        this.repeatDay = repeatDay;
        this.repeatWeek = repeatWeek;
    }

    public Subject(Date startTime, Date endTime, String subjectName, String subjectType, String campusNumber, String cabNumber, String teacherName, ArrayList<Date> arCustomDates) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.subjectName = subjectName;
        this.subjectType = subjectType;
        this.campusNumber = campusNumber;
        this.cabNumber = cabNumber;
        this.teacherName = teacherName;
        this.arCustomDates = arCustomDates;
    }

    public ArrayList<String> getArHomeWorks() {
        return arHomeWorks;
    }

    public void setArHomeWorks(ArrayList<String> arHomeWorks) {
        this.arHomeWorks = arHomeWorks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public ArrayList<Date> getArCustomDates() {
        return arCustomDates;
    }

    public void setArCustomDates(ArrayList<Date> arCustomDates) {
        this.arCustomDates = arCustomDates;
    }

    private Date getTimeWithoutData(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.YEAR, 1970);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }



    @Override
    public int compareTo(Subject o) {
        return getTimeWithoutData(getStartTime()).compareTo(getTimeWithoutData(o.getStartTime()));
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeLong(this.startTime != null ? this.startTime.getTime() : -1);
        dest.writeLong(this.endTime != null ? this.endTime.getTime() : -1);
        dest.writeLong(this.startDate != null ? this.startDate.getTime() : -1);
        dest.writeLong(this.endDate != null ? this.endDate.getTime() : -1);
        dest.writeString(this.subjectName);
        dest.writeString(this.subjectType);
        dest.writeString(this.campusNumber);
        dest.writeString(this.cabNumber);
        dest.writeString(this.teacherName);
        dest.writeList(this.arCustomDates);
        dest.writeStringList(this.arHomeWorks);
        dest.writeInt(this.repeatDay);
        dest.writeInt(this.repeatWeek);
    }

    protected Subject(Parcel in) {
        this.id = in.readString();
        long tmpStartTime = in.readLong();
        this.startTime = tmpStartTime == -1 ? null : new Date(tmpStartTime);
        long tmpEndTime = in.readLong();
        this.endTime = tmpEndTime == -1 ? null : new Date(tmpEndTime);
        long tmpStartDate = in.readLong();
        this.startDate = tmpStartDate == -1 ? null : new Date(tmpStartDate);
        long tmpEndDate = in.readLong();
        this.endDate = tmpEndDate == -1 ? null : new Date(tmpEndDate);
        this.subjectName = in.readString();
        this.subjectType = in.readString();
        this.campusNumber = in.readString();
        this.cabNumber = in.readString();
        this.teacherName = in.readString();
        this.arCustomDates = new ArrayList<Date>();
        in.readList(this.arCustomDates, Date.class.getClassLoader());
        this.arHomeWorks = in.createStringArrayList();
        this.repeatDay = in.readInt();
        this.repeatWeek = in.readInt();
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
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
