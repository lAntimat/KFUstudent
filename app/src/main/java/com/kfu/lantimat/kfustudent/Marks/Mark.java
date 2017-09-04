package com.kfu.lantimat.kfustudent.Marks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GabdrakhmanovII on 04.09.2017.
 */

public class Mark {
    String course;
    String subject;
    String score;
    String type;
    String receivedScore;
    String date;
    String finalScore;
    String finalMark;

    public Mark(String str) {
        Pattern pattern = Pattern.compile("\">(.*)<\\/");
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()) course = matcher.group(1);
        if(matcher.find()) subject = matcher.group(1);
        if(matcher.find()) score = matcher.group(1);
        if(matcher.find()) type = matcher.group(1);
        if(matcher.find()) receivedScore = matcher.group(1);
        if(matcher.find()) date = matcher.group(1);
        if(matcher.find()) finalScore = matcher.group(1);
        if(matcher.find()) finalMark = matcher.group(1);

    }

    public String getCourse() {
        return course;
    }

    public String getSubject() {
        return subject;
    }

    public String getScore() {
        return score;
    }

    public String getType() {
        return type;
    }

    public String getReceivedScore() {
        return receivedScore;
    }

    public String getDate() {
        return date;
    }

    public String getFinalScore() {
        return finalScore;
    }

    public String getFinalMark() {
        return finalMark;
    }

    public String getTestString() {
        return subject + " - полученный балл " + score + "\n Балл за работу в семестре " + score + "\n Итоговая оценка: " + finalMark;
    }
}
