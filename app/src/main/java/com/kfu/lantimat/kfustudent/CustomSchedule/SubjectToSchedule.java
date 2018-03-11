package com.kfu.lantimat.kfustudent.CustomSchedule;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.HomeWorks;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Schedule;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;
import com.kfu.lantimat.kfustudent.utils.KfuUser;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by GabdrakhmanovII on 19.02.2018.
 */

public class SubjectToSchedule {

    public final static String ADD = "1";
    public final static String EDIT = "2";
    public final static String DELETE = "3";

    private Context context;
    private String group;

    OnSuccessListener listener;

    public interface OnSuccessListener {
        void onSuccess();
    }

    public SubjectToSchedule(Context context) {
        this.context = context;
        group = KfuUser.getGroup(context);
    }

    public void addOnSuccesListener(OnSuccessListener listener) {
        this.listener = listener;
    }

    public void edit(Schedule schedule, Subject subject, int subjectPosition, HomeWorks homeWorks) {
        if (subject.getArCustomDates() != null)
            customDaysAddingMethod(schedule, subject, subjectPosition, EDIT, homeWorks);
        else addingMethod(schedule, subject, subjectPosition, EDIT, homeWorks);
    }

    public void add(Schedule schedule, Subject subject) {
        if (subject.getArCustomDates() != null)
            customDaysAddingMethod(schedule, subject, -1, ADD, null);
        else addingMethod(schedule, subject, -1, ADD, null);
    }

    public void delete(Schedule schedule, Subject subject, int subjectPosition, HomeWorks homeWorks) {
        if (subject.getArCustomDates() != null)
            customDaysAddingMethod(schedule, subject, subjectPosition, DELETE, homeWorks);
        else addingMethod(schedule, subject, subjectPosition, DELETE, homeWorks);
    }


    public void addingMethod(final Schedule schedule, Subject subject, int subjectPosition, String methodType, HomeWorks homeWorks) {
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
                if ((startDate.getWeekOfWeekyear() & 1) == 0) {
                    //четная (но пользователь выбрал нечетную, поэтому делаем +1 week)
                    startDate = startDate.plusWeeks(1);
                } else {
                    //не четная
                }
            } else {
                if ((startDate.getWeekOfWeekyear() & 1) == 0) {
                    //четная


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
                        schedule.getArWeekends().get(i).getArDays().get(subject.getRepeatDay()).getSubjects().remove(subjectPosition);
                        schedule.getArWeekends().get(i).getArDays().get(subject.getRepeatDay()).getSubjects().add(subject);
                        break;
                    case DELETE:
                        schedule.getArWeekends().get(i).getArDays().get(subject.getRepeatDay()).getSubjects().remove(subjectPosition);
                        break;
                }

                if (subject.getRepeatWeek() == CustomScheduleConstants.ALL_WEEK) {
                    startDate = startDate.plusWeeks(1);
                } else {
                    startDate = startDate.plusWeeks(2);
                }
            }

            if (methodType.equals(EDIT)) {
                if (homeWorks != null)
                    changeHomeworksSubjectName(subject, homeWorks, new OnSuccessListener() {
                        @Override
                        public void onSuccess() {
                            addToFirestore(schedule);
                        }
                    });
            } else if (methodType.equals(DELETE)) {
                deleteHomeworks(subject, homeWorks, new OnSuccessListener() {
                    @Override
                    public void onSuccess() {
                        addToFirestore(schedule);
                    }
                });
            } else {
                addToFirestore(schedule);
            }

        }
    }

    public void customDaysAddingMethod(final Schedule schedule, Subject subject, int subjectPosition, String methodType, HomeWorks homeWorks) {
        if (schedule != null) {

            for (int i = 0; i < subject.getArCustomDates().size() - 1; i++) {

                LocalDate customDate = new LocalDate(subject.getArCustomDates().get(i).getTime(), DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Paris")));
                int week = customDate.getWeekOfWeekyear() - 1;
                int day = customDate.getDayOfWeek() - 1;
                switch (methodType) {
                    case ADD:
                        schedule.getArWeekends().get(week).getArDays().get(day).getSubjects().add(subject);
                        break;
                    case EDIT:
                        schedule.getArWeekends().get(week).getArDays().get(day).getSubjects().remove(subjectPosition);
                        schedule.getArWeekends().get(week).getArDays().get(day).getSubjects().add(subject);
                        break;
                    case DELETE:
                        schedule.getArWeekends().get(week).getArDays().get(day).getSubjects().remove(subjectPosition);
                        break;
                }
            }

            if (methodType.equals(EDIT)) {
                if (homeWorks != null)
                    changeHomeworksSubjectName(subject, homeWorks, new OnSuccessListener() {
                        @Override
                        public void onSuccess() {
                            addToFirestore(schedule);
                        }
                    });

            } else if (methodType.equals(DELETE)) {
                deleteHomeworks(subject, homeWorks, new OnSuccessListener() {
                    @Override
                    public void onSuccess() {
                        addToFirestore(schedule);
                    }
                });
            } else {
                addToFirestore(schedule);
            }

        }
    }


    private void addToFirestore(Schedule schedule) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Schedule").document(group).set(schedule).addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onSuccess();
            }
        });
    }

    private void changeHomeworksSubjectName(Subject subject, HomeWorks homeWorks, final OnSuccessListener callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (homeWorks.getId() != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("subjectName", subject.getSubjectName());
            db.collection("Schedule").document(group).collection("homeworks").document(homeWorks.getId()).update(map).addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    callback.onSuccess();
                }
            });
        }
    }

    private void deleteHomeworks(Subject subject, HomeWorks homeWorks, final OnSuccessListener callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (homeWorks.getId() != null) {
            db.collection("Schedule").document(group).collection("homeworks").document(homeWorks.getId()).delete().addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    callback.onSuccess();
                }
            });
        }


    }
}
