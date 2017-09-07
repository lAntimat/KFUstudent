package com.kfu.lantimat.kfustudent.Marks;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GabdrakhmanovII on 04.09.2017.
 */

public class Mark {

    public static final int RATING_TYPE=0;
    public static final int SCORE_TYPE=1;
    public static final int PRACTICE_TYPE=2;
    public static final int COURSEWORK_TYPE=4;

    int mViewType = -1;

    String semester;
    String name;
    String score;
    String type;
    String receivedScore;
    String date = "";
    String finalScore;
    String finalMark;

    String semesterRating;
    String placeInGroup;
    String placeInInstitute;

    public Mark(int viewType, String str) {
        mViewType = viewType;
        Pattern pattern = Pattern.compile("\">(.*)<\\/");
        Matcher matcher = pattern.matcher(str.replaceAll("&nbsp;", ""));

        switch (viewType) {
            case RATING_TYPE:
                if (matcher.find()) semester = matcher.group(1);
                if (matcher.find()) semesterRating = matcher.group(1);
                if (matcher.find()) placeInGroup = matcher.group(1);
                if (matcher.find()) placeInInstitute = matcher.group(1);
                break;
            case SCORE_TYPE:
                if (matcher.find()) semester = matcher.group(1);
                if (matcher.find()) name = matcher.group(1);
                if (matcher.find()) score = matcher.group(1);
                if (matcher.find()) type = matcher.group(1);
                if (matcher.find()) receivedScore = matcher.group(1);
                if (matcher.find()) date = matcher.group(1);
                if (matcher.find()) finalScore = matcher.group(1);
                if (matcher.find()) finalMark = matcher.group(1);
                break;
            case PRACTICE_TYPE:
                if (matcher.find()) name = matcher.group(1);
                if (matcher.find()) score = matcher.group(1);
                break;

            case COURSEWORK_TYPE:
                if (matcher.find()) name = matcher.group(1);
                if (matcher.find()) score = matcher.group(1);
                break;

        }

    }

    public String getSemester() {
        return semester;
    }

    public Integer getSemesterInt() {
        if(semester!=null) return Integer.parseInt(semester);
        else return 1000;
    }

    public String getName() {
        return name;
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

    public int getmViewType() {
        return mViewType;
    }

    public String getNameString() {
        switch (mViewType) {
            case RATING_TYPE: return "" + semester + " семестр";
            case SCORE_TYPE:  return name;
            case PRACTICE_TYPE:  return name + " практика" ;
            case COURSEWORK_TYPE:  return "Курсовая работа";
        }
        return "";
    }

    public String getTestString() {
        switch (mViewType) {
            case RATING_TYPE:
                try {
                    if(Integer.parseInt(placeInGroup) < 4)
                        return "Поздравляю, ваш рейтинг в группе " + placeInGroup + "\nместо в институте " + placeInInstitute + "\nсеместровый рейтинг " + semesterRating;
                    else return "Ваш рейтинг в группе " + placeInGroup + "\nместо в институте " + placeInInstitute + "\nсеместровый рейтинг " + semesterRating;
                } catch (Exception e) {
                    return "Ваш рейтинг в группе " + placeInGroup + "\nместо в институте " + placeInInstitute + "\nсеместровый рейтинг " + semesterRating;
                }

            case SCORE_TYPE:  return "полученный балл " + score + "\nбалл за работу в семестре " + receivedScore + " " + date + "\nитоговая оценка: " + finalScore + " (" + finalMark + ")";
            case PRACTICE_TYPE:  return score + " " + date;
            case COURSEWORK_TYPE:  return name + "\n" + score + " " + date;
        }
        return "";
    }

    public static final Comparator<Mark> COMPARE_BY_SEMESTER = new Comparator<Mark>() {
        @Override
        public int compare(Mark lhs, Mark rhs) {
            return lhs.getSemesterInt() - rhs.getSemesterInt();
        }
    };
}
