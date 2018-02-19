package com.kfu.lantimat.kfustudent.CustomSchedule;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Schedule;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by GabdrakhmanovII on 19.02.2018.
 */

public class SubjectToSchedule {

    public final static String ADD = "1";
    public final static String EDIT = "2";
    public final static String DELETE = "3";

    OnSuccessListener listener;

    public interface OnSuccessListener {
        void onSuccess();
    }

    public SubjectToSchedule() {
    }

    public void addOnSuccesListener(OnSuccessListener listener) {
        this.listener = listener;
    }

    public void addNewSubject(int i, Schedule schedule, Subject subject) {
        schedule.getArWeekends().get(i).getArDays().get(subject.getRepeatDay()).getSubjects().add(subject);
    }

    public void editSubject(int i, Schedule schedule, Subject subject, int subjectPosition) {

        ArrayList<String> tempHomeWorks = schedule.getArWeekends().get(i).getArDays().get(subject.getRepeatDay()).getSubjects().get(subjectPosition).getHomeWorks();
        subject.setHomeWorks(tempHomeWorks);
        schedule.getArWeekends().get(i).getArDays().get(subject.getRepeatDay()).getSubjects().remove(subjectPosition);
        schedule.getArWeekends().get(i).getArDays().get(subject.getRepeatDay()).getSubjects().add(subject);



    }

    public void deleteSubject(Subject subject) {

    }

    public void addingMethod(Schedule schedule, Subject subject, int subjectPosition, String methodType) {
        if (schedule != null) {

            LocalDate startDate = new LocalDate(subject.getStartDate().getTime(), DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Paris")));
            LocalDate endDate = new LocalDate(subject.getEndDate().getTime(), DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Paris")));

            int i;
            int end = 56;

            i = startDate.getWeekOfWeekyear();
            end = endDate.getWeekOfWeekyear();

            /*if(end == 1 & endDate.getMonthOfYear() == 12) {
                end = 53;
            }*/

            //В зависимости от типа недели, выбираем неделю для начало отсчета
            if (subject.getRepeatWeek() == CustomScheduleConstants.ALL_WEEK) {

            } else if (subject.getRepeatWeek() == CustomScheduleConstants.ODD_WEEK) {
                if ((i & 1) == 0) {
                    //четная
                    startDate = startDate.plusWeeks(1);
                } else {
                    //не четная
                    //Если неделя не четная, то это хорошо, но мы минусем -1, потому что массив с 0;
                }
            } else {
                if ((i & 1) == 0) {
                    //четная
                    //Если неделя четная, то это хорошо, но мы минусем -1, потому что массив с 0;


                } else {
                    //не четная
                    startDate = startDate.plusWeeks(1);

                }
            }

            for (; startDate.isBefore(endDate); ) {
                //Если четная или нечетная неделя, то плюсуем 2, иначе 1

                i = startDate.getWeekOfWeekyear() - 1;

                switch (methodType) {
                    case ADD:
                        schedule.getArWeekends().get(i).getArDays().get(subject.getRepeatDay()).getSubjects().add(subject);
                        break;
                    case EDIT:
                        ArrayList<String> tempHomeWorks = schedule.getArWeekends().get(i).getArDays().get(subject.getRepeatDay()).getSubjects().get(subjectPosition).getHomeWorks();
                        subject.setHomeWorks(tempHomeWorks);
                        schedule.getArWeekends().get(i).getArDays().get(subject.getRepeatDay()).getSubjects().remove(subjectPosition);
                        schedule.getArWeekends().get(i).getArDays().get(subject.getRepeatDay()).getSubjects().add(subject);
                        break;
                    case DELETE:
                        schedule.getArWeekends().get(i).getArDays().get(subject.getRepeatDay()).getSubjects().remove(subjectPosition);
                        break;
                }

                if (subject.getRepeatDay() == CustomScheduleConstants.ALL_WEEK) {
                    startDate = startDate.plusWeeks(1);
                }
                else {
                    startDate = startDate.plusWeeks(2);
                }
            }

            addToFirestore(schedule);

        }
    }

    private void addToFirestore(Schedule schedule) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Schedule").document("2141115").set(schedule).addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onSuccess();
            }
        });
    }


}
