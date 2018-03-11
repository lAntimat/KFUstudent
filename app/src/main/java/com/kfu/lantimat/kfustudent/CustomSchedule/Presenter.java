package com.kfu.lantimat.kfustudent.CustomSchedule;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.Batch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Day;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Schedule;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Weekend;
import com.kfu.lantimat.kfustudent.utils.KfuUser;
import com.kfu.lantimat.kfustudent.utils.User;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.util.ArrayList;

/**
 * Created by lAntimat on 11.02.2018.
 */

public class Presenter implements CustomScheduleMVP.presenter {

    private static String TAG = "CustomSchedulePresenter";
    private Context context;
    private CustomScheduleMVP.View view;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private LocalDate localDate;
    private Schedule schedule;
    private boolean isOfflineMode = false;

    private String group;

    public Presenter(Context context) {
        localDate = new LocalDate(DateTimeZone.getDefault());
        this.context = context;
        group = KfuUser.getGroup(context);
    }

    @Override
    public void attachVIew(CustomScheduleMVP.View view) {
        this.view = view;
        view.updateDataTextView(localDate);
    }

    @Override
    public void detachView() {


    }

    @Override
    public void getData() {
        view.showLoading();
        //getUser();
        getSchedule();
    }

    private void getUser() {
        String userId = KfuUser.getLogin(context);
        Log.d(TAG, "getUser. userId = " + userId);
        if(userId!=null) {
            db.collection(CustomScheduleConstants.USERS).document(userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            //getSchedule(user);
                        }
                    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    view.hideLoading();
                    view.showError(e.getLocalizedMessage());
                }
            });
        }
    }

    private void getSchedule() {
        db.collection("Schedule").document(group)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        view.hideLoading();
                        if (documentSnapshot.exists()) {
                            schedule = documentSnapshot.toObject(Schedule.class);
                            view.showData(schedule.getArWeekends().get(localDate.getWeekOfWeekyear() - 1));
                            view.updateDataTextView(localDate);

                            if(documentSnapshot.getMetadata().isFromCache()) {
                                view.onOfflineMode(true);
                                isOfflineMode = true;
                            } else {
                                view.onOfflineMode(false);
                                isOfflineMode = false;
                            }
                        } else { //Документ с расписанием не создан
                            view.firstOpenSchedule();
                        }
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.hideLoading();
                view.showError(e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void addData() {
    }

    @Override
    public void nextWeek() {
        if (schedule != null) {
            localDate = localDate.plusWeeks(1);
            view.showData(schedule.getArWeekends().get(localDate.getWeekOfWeekyear() - 1));
            view.updateDataTextView(localDate);
        }
    }

    @Override
    public void prevWeek() {
        if (schedule != null) {
            localDate = localDate.minusWeeks(1);
            view.showData(schedule.getArWeekends().get(localDate.getWeekOfWeekyear() - 1));
            view.updateDataTextView(localDate);
        }
    }

    @Override
    public void recyclerItemClick(int position, int day) {
        Intent intent = new Intent(context, SubjectInfoActivity.class);
        intent.putExtra("Schedule", schedule);
        intent.putExtra("subject", position);
        intent.putExtra("week", localDate.getWeekOfWeekyear() - 1);
        intent.putExtra("day", day);
        intent.putExtra("isOffline", isOfflineMode);
        view.openSubjectInfo(intent);
    }

    @Override
    public void fabCLick() {
        view.openAddSubject(schedule);
    }

    private void addTestData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<String> homeWorks = new ArrayList<>();
        homeWorks.add("Сделать что то");
        homeWorks.add("И еще что то");

        ArrayList<Subject> arSubjects = new ArrayList<>();
        //arSubjects.add(new Subject("Высшая математика", homeWorks,"415", "Матвеев Семен Николаевич"));
        //arSubjects.add(new Subject("Высшая математика", homeWorks,"415", "Матвеев Семен Николаевич"));

        ArrayList<Day> arDays = new ArrayList<>();
        arDays.add(new Day(arSubjects));
        arDays.add(new Day(arSubjects));

        Weekend weekend = new Weekend(arDays);

        ArrayList<Weekend> arWeekends = new ArrayList<>();
        arWeekends.add(weekend);

        Schedule schedule = new Schedule("2141115", arWeekends);

        db.collection("Schedule").add(schedule);
    }
}
