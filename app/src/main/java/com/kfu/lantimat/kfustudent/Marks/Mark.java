package com.kfu.lantimat.kfustudent.Marks;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

/**
 * Created by GabdrakhmanovII on 04.09.2017.
 */

public class Mark {

    public static final int RATING_TYPE=0;
    public static final int SCORE_TYPE=1;
    public static final int PRACTICE_TYPE=2;
    public static final int COURSEWORK_TYPE=4;
    public static final int TWO_ROW_TYPE=5;

    int mViewType = -1;

    String semester;
    String name;
    String score;
    String type;
    String receivedScore;
    String date = "";
    String finalScore;
    String finalMark;

    String score2;
    String type2;
    String receivedScore2;
    String date2 = "";
    String finalScore2;
    String finalMark2;

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

    public Mark(int viewType, String str, String str2) {
        mViewType = viewType;
        Pattern pattern = Pattern.compile("\">(.*)<\\/");
        Matcher matcher = pattern.matcher(str.replaceAll("&nbsp;", ""));

        Pattern pattern2 = Pattern.compile("\">(.*)<\\/");
        Matcher matcher2 = pattern2.matcher(str2.replaceAll("&nbsp;", ""));

        switch (viewType) {
            case TWO_ROW_TYPE:
                if (matcher.find()) semester = matcher.group(1);
                if (matcher.find()) name = matcher.group(1);
                if (matcher.find()) score = matcher.group(1);
                if (matcher.find()) type = matcher.group(1);
                if (matcher.find()) receivedScore = matcher.group(1);
                if (matcher.find()) date = matcher.group(1);
                if (matcher.find()) finalScore = matcher.group(1);
                if (matcher.find()) finalMark = matcher.group(1);

                if (matcher2.find()) type2 = matcher2.group(1);
                if (matcher2.find()) receivedScore2 = matcher2.group(1);
                if (matcher2.find()) date2 = matcher2.group(1);
                if (matcher2.find()) finalScore2 = matcher2.group(1);
                if (matcher2.find()) finalMark2 = matcher2.group(1);
                break;
        }

    }

    public String getSemester() {
        return semester;
    }

    public Integer getSemesterInt() {
        try {
            if(semester!=null) return Integer.parseInt(semester);
        } catch (Exception e) {
            return 1000;
        }
        return 1000;
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

    public boolean containsNumber(String string) {
        return string.matches(".*\\d+.*");
    }

    public int getmViewType() {
        return mViewType;
    }

    public String getNameString() {
        switch (mViewType) {
            case RATING_TYPE: return "" + semester + " семестр";
            case SCORE_TYPE:  return name;
            case TWO_ROW_TYPE:  return name;
            case PRACTICE_TYPE:  return name + " практика" ;
            case COURSEWORK_TYPE:  return "Курсовая работа";
        }
        return "";
    }

    public CharSequence getTestString() {
        switch (mViewType) {
            case RATING_TYPE:
                try {
                    if(Integer.parseInt(placeInGroup) < 4)
                        return "поздравляю, ваш рейтинг в группе " + placeInGroup + "\nместо в институте " + placeInInstitute + "\nсеместровый рейтинг " + semesterRating;
                    else return "Ваш рейтинг в группе " + placeInGroup + "\nместо в институте " + placeInInstitute + "\nсеместровый рейтинг " + semesterRating;
                } catch (Exception e) {
                    return "Ваш рейтинг в группе " + placeInGroup + "\nместо в институте " + placeInInstitute + "\nсеместровый рейтинг " + semesterRating;
                }

            case SCORE_TYPE:  return type + "\nполученный балл " + score + "\nбалл за работу в семестре " + receivedScore + " (" + date + ")\nитоговая оценка: " + finalScore + " (" + finalMark + ")";
            case PRACTICE_TYPE:  return score + " " + date;
            case COURSEWORK_TYPE:  return name + "\n" + score + " " + date;
            case TWO_ROW_TYPE:
                SpannableString span1 = new SpannableString(type);
                span1.setSpan(new AbsoluteSizeSpan(18), 0, type.length(), SPAN_INCLUSIVE_INCLUSIVE);

                return span1 + "\nполученный балл " + score + "\nбалл за работу в семестре " + receivedScore + " (" + date + ")\nитоговая оценка: " + finalScore + " (" + finalMark + ")"
                    + "\n" + type2 +"\nполученный балл " + score + "\nбалл за работу в семестре " + receivedScore2 + " (" + date2 + ")\nитоговая оценка: " + finalScore2 + " (" + finalMark2 + ")";

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
