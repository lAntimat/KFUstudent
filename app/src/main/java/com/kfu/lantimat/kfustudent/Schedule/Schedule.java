package com.kfu.lantimat.kfustudent.Schedule;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GabdrakhmanovII on 04.09.2017.
 */

public class Schedule {


    private String date = "";
    private ArrayList<String> time = new ArrayList<>();
    private ArrayList<String> subjectName = new ArrayList<>();
    private ArrayList<String> place = new ArrayList<>();


    public Schedule(String str) {
        Pattern datePattern = Pattern.compile(">(.*) <\\/font>");

        Matcher dateMatcher = datePattern.matcher(str.replaceAll("", ""));
        if (dateMatcher.find()) date = dateMatcher.group(1);

        Pattern schedulePattern = Pattern.compile("nowrap> (.*)<\\/td>|<td class=\"table_td\" width=\"180\" style=\"\"> (.*)<\\/td>|<td class=\"table_td\">(.*)<\\/td>");

        Matcher sheduleMatcher = schedulePattern.matcher(str);

        while (sheduleMatcher.find()) {
             time.add(sheduleMatcher.group(1));
            if(sheduleMatcher.find()) subjectName.add(sheduleMatcher.group(2));
            if(sheduleMatcher.find()) place.add(sheduleMatcher.group(3));
        }
    }

    public String getDate() {
        if(date.contains("//")) {
            return date.substring(date.indexOf("//") + 3);
        } else return "";
    }

    public String getSchedule() {
        if (time.size()>0 &subjectName.size()>0 & place.size()>0) {
            String str = "";
            for (int i = 0; i <time.size() ; i++) {
                str += time.get(i) + "\n" + subjectName.get(i) + "\n" + place.get(i) + "\n\n";
            }
            return str;
        } else return "Пар нет";
    }
}
