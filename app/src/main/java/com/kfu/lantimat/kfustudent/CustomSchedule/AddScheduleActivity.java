package com.kfu.lantimat.kfustudent.CustomSchedule;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.kfu.lantimat.kfustudent.utils.CreateDialog;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddScheduleActivity extends AppCompatActivity {
    private TextView tvStartTIme, tvEndTime, tvSubjectType;
    private ConstraintLayout clRepeat, clSubjectType;

    private ArrayList<String> arSubjects = new ArrayList<>();
    private ArrayList<String> arTeachers = new ArrayList<>();
    private ArrayList<String> arCampuses = new ArrayList<>();
    private ArrayList<String> arCabs = new ArrayList<>();
    AutoCompleteTextView actvSubjectName, actvTeacher, actvCampus, actvCab;
    private Schedule schedule;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    MaterialDialog dialog;

    TimePickerDialog.OnTimeSetListener t;
    TimePickerDialog.OnTimeSetListener t2;

    Calendar dateAndTime = Calendar.getInstance();
    Calendar dateAndTime2 = Calendar.getInstance();

    public int repeatDay = -1;
    public int repeatWeek = -1;

    public LocalDate startDate;
    public LocalDate endDate;

    int subjectPosition;
    int weekendPosition;
    int dayPosition;
    boolean isEdit;
    Subject subject;
    String subjectType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        tvStartTIme = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvSubjectType = findViewById(R.id.tvSubjectType);

        clRepeat = findViewById(R.id.cl3);

        clRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment();
            }
        });

        clSubjectType = findViewById(R.id.cl4);

        clSubjectType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(AddScheduleActivity.this)
                        .title("Выберите тип пары")
                        .items(R.array.dialog_list_subject_type)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                subjectType = getResources().getStringArray(R.array.dialog_list_subject_type)[which];
                                tvSubjectType.setText(subjectType);
                                return true;
                            }
                        })
                        .positiveText("Выбрать")
                        .show();
            }
        });

        initAutoCompleteTextView();
        addDateToAutoCompleteTextView();
        //loadSchedule();
        initTimePickers();
        initTimeTextViewClickListeners();

        schedule = getIntent().getParcelableExtra("Schedule");
        if(schedule==null) addSubjectInFirstTime();

        subjectPosition = getIntent().getIntExtra("subject", -1);
        weekendPosition = getIntent().getIntExtra("week", -1);
        dayPosition = getIntent().getIntExtra("day", -1);
        isEdit = getIntent().getBooleanExtra("isEdit", false);

        if(dayPosition!=-1) {
            subject = schedule.getArWeekends().get(weekendPosition).getArDays().get(dayPosition).getSubjects().get(subjectPosition);
            updateUI(subject);
        }

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
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()
                            ) {
                        arTeachers.add(doc.get("name").toString());
                    }
                }
            }
        });

        db.collection("Subjects").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()
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
                        if (task.getResult().exists()) {
                            schedule = task.getResult().toObject(Schedule.class);
                        } else {
                            addSubjectInFirstTime();
                        }
                    }
                });
    }

    private void addSubject(Subject subject, int repeatDay, int repeatWeek, LocalDate startDate, LocalDate endDate) {

        if (schedule != null) {
            int i;
            int end = 56;

            i = startDate.getWeekOfWeekyear();
            end = endDate.getWeekOfWeekyear();

            //В зависимости от типа недели, выбираем неделю для начало отсчета
            if (repeatWeek == CustomScheduleConstants.ALL_WEEK) {

            }
            else if (repeatWeek == CustomScheduleConstants.ODD_WEEK) {
                if ((i & 1) == 0) {
                    //четная

                } else {
                    //не четная
                    //Если неделя не четная, то это хорошо, но мы минусем -1, потому что массив с 0;
                   i--;
                }
            } else {
                if ((i & 1) == 0) {
                    //четная
                    //Если неделя четная, то это хорошо, но мы минусем -1, потому что массив с 0;
                    i--;

                } else {
                    //не четная
                }
            }

            for (; i < end; ) {
                //Если четная или нечетная неделя, то плюсуем 2, иначе 1
                schedule.getArWeekends().get(i).getArDays().get(repeatDay).getSubjects().add(subject);

                if (repeatDay == CustomScheduleConstants.ALL_WEEK) i++;
                else i = i + 2;
            }

            db.collection("Schedule").document("2141115").set(schedule).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dialog.dismiss();
                    finish();
                }
            });
        }

    }

    private void addNewWords(String subjectName, String teacherName) {
        if (!arSubjects.contains(subjectName)) {

            Map<String, Object> map = new HashMap<>();
            map.put("name", subjectName);
            db.collection("Subjects").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {

                }
            });
        }

        if (!arTeachers.contains(teacherName)) {
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

        if (TextUtils.isEmpty(actvSubjectName.getText().toString())) {
            Toast.makeText(this, "Введите название предмета", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(actvTeacher.getText().toString())) {
            Toast.makeText(this, "Введите ФИО преподавателя", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(actvCampus.getText().toString())) {
            Toast.makeText(this, "Введите название учебный корпус", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(actvCab.getText().toString())) {
            Toast.makeText(this, "Введите номер аудитории", Toast.LENGTH_SHORT).show();
            return;
        }

        if (repeatWeek == -1) {
            Toast.makeText(this, "Введите неделю для повтора", Toast.LENGTH_SHORT).show();
            return;
        }

        if (repeatDay == -1) {
            Toast.makeText(this, "Выберите день предмета", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(subjectType)) {
            Toast.makeText(this, "Выберите тип пары", Toast.LENGTH_SHORT).show();
            return;
        }

        SubjectToSchedule toSchedule = new SubjectToSchedule();
        toSchedule.addOnSuccesListener(new SubjectToSchedule.OnSuccessListener() {
            @Override
            public void onSuccess() {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("Subject", subject);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        subject = new Subject(new Date(dateAndTime.getTimeInMillis()), new Date(dateAndTime2.getTimeInMillis()), startDate.toDate(), endDate.toDate(), actvSubjectName.getText().toString(), subjectType, actvCampus.getText().toString(), actvCab.getText().toString(), actvTeacher.getText().toString(), repeatDay, repeatWeek);
        //addSubject(subject, repeatDay, repeatWeek, startDate, endDate);
        if(isEdit) toSchedule.addingMethod(schedule, subject, subjectPosition, SubjectToSchedule.EDIT);
        else toSchedule.addingMethod(schedule, subject, -1, SubjectToSchedule.ADD);
        addNewWords(actvSubjectName.getText().toString(), actvTeacher.getText().toString());
        dialog = CreateDialog.createPleaseWaitDialog(AddScheduleActivity.this);

    }

    private void initTimePickers() {

        dateAndTime.set(Calendar.HOUR_OF_DAY, 8);
        dateAndTime.set(Calendar.MINUTE, 0);

        dateAndTime2.set(Calendar.HOUR_OF_DAY, 9);
        dateAndTime2.set(Calendar.MINUTE, 30);

        Date date = new Date(dateAndTime.getTimeInMillis());
        tvStartTIme.setText(getFormattedTime(date));

        Date date2 = new Date(dateAndTime2.getTimeInMillis());
        tvEndTime.setText(getFormattedTime(date2));

        t = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateAndTime.set(Calendar.MINUTE, minute);
                Date date = new Date(dateAndTime.getTimeInMillis());
                String formattedTime = getFormattedTime(date);
                tvStartTIme.setText(formattedTime);
            }
        };

        t2 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dateAndTime2.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateAndTime2.set(Calendar.MINUTE, minute);
                Date date = new Date(dateAndTime2.getTimeInMillis());
                String formattedTime = getFormattedTime(date);
                tvEndTime.setText(formattedTime);
            }
        };
    }

    private String getFormattedTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(date);
    }

    private void initTimeTextViewClickListeners() {

        tvStartTIme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddScheduleActivity.this, t,
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE),
                        true)
                        .show();
            }
        });


        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddScheduleActivity.this, t2,
                        dateAndTime2.get(Calendar.HOUR_OF_DAY),
                        dateAndTime2.get(Calendar.MINUTE),
                        true)
                        .show();
            }
        });

    }

    private void showFragment() {
        long start = -1;
        long end = -1;
        if(startDate!=null) start = startDate.toDate().getTime();
        if(endDate!=null) end = endDate.toDate().getTime();
        AddScheduleFragment addScheduleFragment = AddScheduleFragment.newInstance(repeatDay, repeatWeek, start, end);
        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
        fm.add(R.id.container, addScheduleFragment);
        fm.addToBackStack("addSchedule");
        fm.commit();

    }

    private void updateUI(Subject subject) {
        actvSubjectName.setText(subject.getSubjectName());
        actvTeacher.setText(subject.getTeacherName());
        actvCampus.setText(subject.getCampusNumber());
        actvCab.setText(subject.getCabNumber());

        repeatDay = subject.getRepeatDay();
        repeatWeek = subject.getRepeatWeek();

        dateAndTime.setTime(subject.getStartTime());
        dateAndTime2.setTime(subject.getEndTime());

        startDate = new LocalDate(subject.getStartDate());
        endDate = new LocalDate(subject.getEndDate());

        subjectType = subject.getSubjectType();
        tvSubjectType.setText(subjectType);

    }

}
