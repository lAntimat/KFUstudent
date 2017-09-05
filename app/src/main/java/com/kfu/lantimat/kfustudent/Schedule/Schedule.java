package com.kfu.lantimat.kfustudent.Schedule;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GabdrakhmanovII on 04.09.2017.
 */

public class Schedule {


    String date;
    ArrayList<String> time;
    ArrayList<String> subjectName;
    ArrayList<String> place;


    public Schedule(String str) {
        Pattern datePattern = Pattern.compile("<font class=\"table_h1\">(.*) <\\/font>\n" +
                "<table");

        Matcher dateMatcher = datePattern.matcher(str.replaceAll("", ""));
        if (dateMatcher.find()) date = dateMatcher.group(4);

        Pattern schedulePattern = Pattern.compile("nowrap=\"\">(.*)<\\/td><td class=\"table_td\" width=\"180\" style=\"\">  (.*) <\\/td><td class=\"table_td\">(.*)<\\/td><\\/tr>");

        Matcher sheduleMatcher = schedulePattern.matcher(str.replaceAll("", ""));

        while (sheduleMatcher.find()) {
            time.add(dateMatcher.group(1));
            subjectName.add(dateMatcher.group(2));
            place.add(dateMatcher.group(3));
        }
    }

    public String getDate() {
        return date;
    }

    public String getSchedule() {
        if (time != null) {
            return time.get(0) + "\n" + subjectName.get(0) + "\n" + place.get(0) + "\n";
        } else return "Робит";
    }
}
