package com.kfu.lantimat.kfustudent.CustomSchedule;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Day;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.HomeWorks;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Schedule;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Weekend;
import com.kfu.lantimat.kfustudent.utils.KfuUser;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by GabdrakhmanovII on 19.02.2018.
 */

public class SubjectToSchedule2 {

    public final static String ADD = "1";
    public final static String EDIT = "2";
    public final static String DELETE = "3";

    private Context context;
    private String group;

    private Schedule schedule;
    private Weekend weekend;
    OnSuccessListener listener;

    public interface OnSuccessListener {
        void onSuccess();
    }

    public SubjectToSchedule2(Context context) {
        this.context = context;
        group = KfuUser.getGroup(context);
    }

    private Schedule createSchedule() {

        ArrayList<Subject> arSubjects = new ArrayList<>();

        ArrayList<Day> arDays = new ArrayList<>();
        arDays.add(new Day(arSubjects));
        arDays.add(new Day(arSubjects));
        arDays.add(new Day(arSubjects));
        arDays.add(new Day(arSubjects));
        arDays.add(new Day(arSubjects));
        arDays.add(new Day(arSubjects));
        arDays.add(new Day(arSubjects));

        Weekend weekend = new Weekend(arDays);

        ArrayList<Weekend> arWeekends = new ArrayList<>();
        for (int i = 0; i < 56; i++) {
            arWeekends.add(weekend);
        }
        return new Schedule("", arWeekends);
    }

    private Weekend createWeekend() {

        ArrayList<Day> arDays = new ArrayList<Day>();
        arDays.add(new Day(new ArrayList<Subject>()));
        arDays.add(new Day(new ArrayList<Subject>()));
        arDays.add(new Day(new ArrayList<Subject>()));
        arDays.add(new Day(new ArrayList<Subject>()));
        arDays.add(new Day(new ArrayList<Subject>()));
        arDays.add(new Day(new ArrayList<Subject>()));
        arDays.add(new Day(new ArrayList<Subject>()));

        Weekend weekend = new Weekend(arDays);

        return new Weekend(arDays);
    }


    public void addOnSuccesListener(OnSuccessListener listener) {
        this.listener = listener;
    }

    public void edit(Subject subject) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(CustomScheduleConstants.SCHEDULE).document(group).collection(CustomScheduleConstants.SUBJECTS).document(subject.getId()).set(subject).addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onSuccess();

            }
        });
    }

    public void add(Subject subject) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(CustomScheduleConstants.SCHEDULE).document(group).collection(CustomScheduleConstants.SUBJECTS).add(subject)
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        listener.onSuccess();

                    }
                });
    }

    public void delete(Subject subject) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(CustomScheduleConstants.SCHEDULE).document(group).collection(CustomScheduleConstants.SUBJECTS).document(subject.getId()).delete().addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onSuccess();
            }
        });
    }


    public Weekend getWeekend(ArrayList<Subject> arSubjects, LocalDate localDate) {
        weekend = createWeekend();
        for (int i = 0; i < arSubjects.size(); i++) {
            Subject subject = arSubjects.get(i);
            if (subject.getArCustomDates()!=null)
                subjectWithCustomDaysToWeek(weekend, subject, localDate);
            else subjectsToWeek(weekend, subject, localDate);
        }
        return weekend;
    }

    public void subjectsToWeek(Weekend weekend, Subject subject, LocalDate localDate) {

            LocalDate startDate = new LocalDate(subject.getStartDate().getTime(), DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Paris")));
            LocalDate endDate = new LocalDate(subject.getEndDate().getTime(), DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Paris")));

           if(localDate.isBefore(endDate) & localDate.isAfter(startDate)) {
               //Если предмет по четным и не четным дням.
               if(subject.getRepeatWeek()==CustomScheduleConstants.ALL_WEEK) {
                   weekend.getArDays().get(subject.getRepeatDay()).getSubjects().add(subject);
                   //Если предмет по четным/нечетным дням
               } else if ((localDate.getWeekOfWeekyear() & 1) == 0) {
                   //четная
                   if(subject.getRepeatWeek()==CustomScheduleConstants.EVEN_WEEK)
                       weekend.getArDays().get(subject.getRepeatDay()).getSubjects().add(subject);
                   //не четная
               } else {
                   if(subject.getRepeatWeek()==CustomScheduleConstants.ODD_WEEK)
                       weekend.getArDays().get(subject.getRepeatDay()).getSubjects().add(subject);
               }
           }
    }

    public void subjectWithCustomDaysToWeek(Weekend weekend, Subject subject, LocalDate localDate) {

            for (int i = 0; i < subject.getArCustomDates().size() - 1; i++) {

                LocalDate customDate = new LocalDate(subject.getArCustomDates().get(i).getTime(), DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Paris")));

                if (localDate.isBefore(customDate.withDayOfWeek(7)) & localDate.isAfter(customDate.withDayOfWeek(1))) {
                    int week = customDate.getWeekOfWeekyear() - 1;
                    int day = customDate.getDayOfWeek() - 1;
                    weekend.getArDays().get(day).getSubjects().add(subject);
                }
            }
    }


    private void addToFirestore(Schedule schedule) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(CustomScheduleConstants.SCHEDULE).document(group).set(schedule).addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

    private void deleteHomeworks(Subject subject, HomeWorks homeWorks, final OnSuccessListener callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (homeWorks.getId() != null) {
            db.collection(CustomScheduleConstants.SCHEDULE).document(group).collection("homeworks").document(homeWorks.getId()).delete().addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
    }
}
