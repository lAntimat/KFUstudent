package com.kfu.lantimat.kfustudent.CustomSchedule;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Day;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Schedule;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Weekend;
import com.kfu.lantimat.kfustudent.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddScheduleActivity extends AppCompatActivity {

    private ArrayList<String> arSubjects = new ArrayList<>();
    private ArrayList<String> arTeachers = new ArrayList<>();
    private ArrayList<String> arCampuses = new ArrayList<>();
    private ArrayList<String> arCabs = new ArrayList<>();
    AutoCompleteTextView actvSubjectName, actvTeacher, actvCampus, actvCab;
    private Schedule schedule;
    FirebaseFirestore db  = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        initAutoCompleteTextView();
        addDateToAutoCompleteTextView();
        loadSchedule();
    }

    private void initAutoCompleteTextView() {
        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> subjectsAdapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, arSubjects);
        //Getting the instance of AutoCompleteTextView
        actvSubjectName = (AutoCompleteTextView) findViewById(R.id.tvSubjectName);
        actvSubjectName.setThreshold(1);//will start working from first character
        actvSubjectName.setAdapter(subjectsAdapter);//setting the adapter data into the AutoCompleteTextView

        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> teachersAdapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, arTeachers);
        //Getting the instance of AutoCompleteTextView
        actvTeacher = (AutoCompleteTextView) findViewById(R.id.tvTeacherName);
        actvTeacher.setThreshold(1);//will start working from first character
        actvTeacher.setAdapter(teachersAdapter);//setting the adapter data into the AutoCompleteTextView

        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> campusAdapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, arCampuses);
        //Getting the instance of AutoCompleteTextView
        actvCampus = (AutoCompleteTextView) findViewById(R.id.tvCampNumber);
        actvCampus.setThreshold(1);//will start working from first character
        actvCampus.setAdapter(campusAdapter);//setting the adapter data into the AutoCompleteTextView

        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> cabAdapters = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, arCabs);
        //Getting the instance of AutoCompleteTextView
        actvCab = (AutoCompleteTextView) findViewById(R.id.tvCabNumber);
        actvCab.setThreshold(1);//will start working from first character
        actvCab.setAdapter(cabAdapters);//setting the adapter data into the AutoCompleteTextView
    }

    private void addDateToAutoCompleteTextView() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Teachers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (DocumentSnapshot doc:task.getResult()
                         ) {
                        arTeachers.add(doc.get("name").toString());
                    }
                }
            }
        });

        db.collection("Subjects").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (DocumentSnapshot doc:task.getResult()
                            ) {
                        arSubjects.add(doc.get("name").toString());
                    }
                }
            }
        });

    }

    private void addSubjectInFirstTime() {

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

        schedule = new Schedule("2141115", arWeekends);

        db.collection("Schedule").document("2141115").set(schedule);
    }

    private void loadSchedule() {
        db.collection("Schedule").document("2141115")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()) {
                            schedule = task.getResult().toObject(Schedule.class);
                        } else {
                            addSubjectInFirstTime();
                        }
                    }
                });
    }

    private void addSubjectInFirstTime(Subject subject, int repeatDay, int repeatWeek ) {

        if(schedule!=null) {

            for (int i = 0; i < 28; i ++) {
                schedule.getArWeekends().get(i).getArDays().get(repeatDay).getSubjects().add(subject);
            }

            db.collection("Schedule").document("2141115").set(schedule).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }

    }

    private void addNewWords(String subjectName, String teacherName) {
        if(!arSubjects.contains(subjectName)) {

            Map<String, Object> map = new HashMap<>();
            map.put("name", subjectName);
            db.collection("Subjects").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {

                }
            });
        }

        if(!arTeachers.contains(teacherName)) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", teacherName);
            db.collection("Teachers").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {

                }
            });
        }

        }

    public void addBtnClick(View view) {
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        Subject subject = new Subject(date, date, actvSubjectName.getText().toString(), null, actvCampus.getText().toString(), actvCab.getText().toString(), actvTeacher.getText().toString());
        addSubjectInFirstTime(subject, 2, 1);
        addNewWords(actvSubjectName.getText().toString(), actvTeacher.getText().toString());

    }

    private void showFragment() {

    }

}
