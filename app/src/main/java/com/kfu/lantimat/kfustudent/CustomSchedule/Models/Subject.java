package com.kfu.lantimat.kfustudent.CustomSchedule.Models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lAntimat on 11.02.2018.
 */

public class Subject {
    Date startTime;
    Date endTime;
    String subjectName;
    ArrayList<String> homeWorks;
    String campusNumber;
    String cabNumber;
    String teacherName;

    public Subject() {
    }

    public Subject(Date startTime, Date endTime, String subjectName, ArrayList<String> homeWorks, String campusNumber, String cabNumber, String teacherName) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.subjectName = subjectName;
        this.homeWorks = homeWorks;
        this.campusNumber = campusNumber;
        this.cabNumber = cabNumber;
        this.teacherName = teacherName;
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
}
