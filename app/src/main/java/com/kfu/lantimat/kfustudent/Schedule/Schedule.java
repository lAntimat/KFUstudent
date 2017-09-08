package com.kfu.lantimat.kfustudent.Schedule;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GabdrakhmanovII on 04.09.2017.
 */

public class Schedule {


    private String date = "";
    private String time = "";
    private String subjectName = "";
    private String place = "";

    public Schedule() {
    }

    public Schedule(String str) {
        /*Pattern datePattern = Pattern.compile(">(.*) <\\/font>");

        Matcher dateMatcher = datePattern.matcher(str.replaceAll("", ""));
        if (dateMatcher.find()) date = dateMatcher.group(1);

        Pattern schedulePattern = Pattern.compile("nowrap> (.*)<\\/td>|<td class=\"table_td\" width=\"180\" style=\"\"> (.*)<\\/td>|<td class=\"table_td\">(.*)<\\/td>");

        Matcher sheduleMatcher = schedulePattern.matcher(str);

        while (sheduleMatcher.find()) {
             time.add(sheduleMatcher.group(1));
            if(sheduleMatcher.find()) subjectName.add(sheduleMatcher.group(2));
            if(sheduleMatcher.find()) place.add(sheduleMatcher.group(3));
        }*/
    }

    public String getDate() {
        if(date.contains("//")) {
            return date.substring(date.indexOf("//") + 3);
        } else return "";
    }



    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getPlace() {
        return place;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }


    public void setPlace(String place) {
        this.place = place;
    }
}
