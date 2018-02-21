package com.kfu.lantimat.kfustudent.CustomSchedule;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.HomeWorks;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Schedule;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;

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

        schedule.getArWeekends().get(i).getArDays().get(subject.getRepeatDay()).getSubjects().remove(subjectPosition);
        schedule.getArWeekends().get(i).getArDays().get(subject.getRepeatDay()).getSubjects().add(subject);



    }

    public void deleteSubject(Subject subject) {

    }

    public void edit(Schedule schedule, Subject subject, int subjectPosition, HomeWorks homeWorks) {
        addingMethod(schedule, subject, subjectPosition, EDIT, homeWorks);
    }

    public void add(Schedule schedule, Subject subject) {
        addingMethod(schedule, subject, -1, ADD, null);
    }

    public void delete(Schedule schedule, Subject subject, int subjectPosition) {
        addingMethod(schedule, subject, subjectPosition, DELETE, null);
    }


    public void addingMethod(Schedule schedule, Subject subject, int subjectPosition, String methodType, HomeWorks homeWorks) {
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
                }
                else {
                    startDate = startDate.plusWeeks(2);
                }
            }

            if(methodType.equals(EDIT)) {
                if(homeWorks!=null) changeHomeworksSubjectName(subject, homeWorks);
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

    private void changeHomeworksSubjectName(Subject subject, HomeWorks homeWorks) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(homeWorks.getId()!=null) {
            Map<String, Object> map = new HashMap<>();
            map.put("subjectName", subject.getSubjectName());
            db.collection("Schedule").document("2141115").collection("homeworks").document(homeWorks.getId()).update(map).addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    listener.onSuccess();
                }
            });
        }
    }


}
