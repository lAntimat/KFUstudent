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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Day;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.HomeWorks;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Schedule;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Weekend;
import com.kfu.lantimat.kfustudent.KFURestClient;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.utils.CheckAuth;
import com.kfu.lantimat.kfustudent.utils.CreateDialog;
import com.kfu.lantimat.kfustudent.utils.KfuUser;
import com.kfu.lantimat.kfustudent.utils.User;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class AddScheduleActivity extends AppCompatActivity {

    private static final String TAG = "AddScheduleActivity";
    private String USER_ID;

    private TextView tvStartTIme, tvEndTime, tvSubjectType;
    private ConstraintLayout clRepeat, clSubjectType;

    private ArrayList<String> arSubjects = new ArrayList<>();
    private ArrayList<String> arTeachers = new ArrayList<>();
    private ArrayList<String> arCampuses = new ArrayList<>();
    private ArrayList<String> arCabs = new ArrayList<>();
    AutoCompleteTextView actvSubjectName, actvTeacher, actvCampus, actvCab;
    ArrayAdapter<String> subjectsAdapter, teachersAdapter, campusAdapter, cabAdapters;
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
    int dayPosition = 0;
    boolean isEdit;
    boolean isImport;
    Subject subjectFromIntent;
    Subject subject;
    String subjectType;
    private HomeWorks homeworks;
    public ArrayList<Date> arCustomDays;
    public boolean isCustomDay = false;

    //Toolbar back button click
    @Override
    public boolean onSupportNavigateUp() {
        backClick(null);
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MyMaterialTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        tvStartTIme = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvSubjectType = findViewById(R.id.tvSubjectType);
        //getScheduleFromSite();

        if (!CheckAuth.isAuth()) {
            Toast.makeText(getApplicationContext(), "Вы не авторизованы!", Toast.LENGTH_SHORT).show();
            finish();
        } else {

            USER_ID = KfuUser.getLogin(this);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Добавить занятие");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            initButtons();

            initAutoCompleteTextView();
            addDateToAutoCompleteTextView();
            //loadSchedule();
            initTimePickers();
            initTimeTextViewClickListeners();

            subjectFromIntent = getIntent().getParcelableExtra(CustomScheduleConstants.SUBJECT_MODEL);
            isEdit = getIntent().getBooleanExtra("isEdit", false);
            isImport = getIntent().getBooleanExtra(CustomScheduleConstants.IS_IMPORT, false);
            dayPosition = getIntent().getIntExtra(CustomScheduleConstants.DAY_POSITION, 0);

            //schedule = getIntent().getParcelableExtra("Schedule");

            /*if (subjectFromIntent == null) {
                //Это нужно на случай если данные не спарсились
                if (KfuUser.getGroup(getApplicationContext()) == null) {
                    CheckAuth.getUserInfo(new CheckAuth.UserInfoCallback() {
                        @Override
                        public void onSuccess(User user) {
                        }
                    });
                }
            }*/

            if (isEdit) {
                //subject = schedule.getArWeekends().get(weekendPosition).getArDays().get(dayPosition).getSubjects().get(subjectPosition);
                updateUI(subjectFromIntent);
                Log.d(TAG, "if isEdit");
            } else if(isImport) {
                //subject = getIntent().getParcelableExtra(CustomScheduleConstants.SUBJECT_MODEL);
                updateUI(subjectFromIntent);
                Log.d(TAG, "if isImport");
            } else {
                repeatDay = dayPosition;
            }
        }
    }

    private void initButtons() {
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
    }

    private void initAutoCompleteTextView() {
        //Creating the instance of ArrayAdapter containing list of fruit names
        subjectsAdapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, arSubjects);
        //Getting the instance of AutoCompleteTextView
        actvSubjectName = (AutoCompleteTextView) findViewById(R.id.tvSubjectName);
        actvSubjectName.setThreshold(1);//will start working from first character
        actvSubjectName.setAdapter(subjectsAdapter);//setting the adapter data into the AutoCompleteTextView

        //Creating the instance of ArrayAdapter containing list of fruit names
        teachersAdapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, arTeachers);
        //Getting the instance of AutoCompleteTextView
        actvTeacher = (AutoCompleteTextView) findViewById(R.id.tvTeacherName);
        actvTeacher.setThreshold(1);//will start working from first character
        actvTeacher.setAdapter(teachersAdapter);//setting the adapter data into the AutoCompleteTextView

        //Creating the instance of ArrayAdapter containing list of fruit names
        campusAdapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, arCampuses);
        //Getting the instance of AutoCompleteTextView
        actvCampus = (AutoCompleteTextView) findViewById(R.id.tvCampNumber);
        actvCampus.setThreshold(1);//will start working from first character
        actvCampus.setAdapter(campusAdapter);//setting the adapter data into the AutoCompleteTextView

        //Creating the instance of ArrayAdapter containing list of fruit names
        cabAdapters = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, arCabs);
        //Getting the instance of AutoCompleteTextView
        actvCab = (AutoCompleteTextView) findViewById(R.id.tvCabNumber);
        actvCab.setThreshold(1);//will start working from first character
        actvCab.setAdapter(cabAdapters);//setting the adapter data into the AutoCompleteTextView
    }

    private void addDateToAutoCompleteTextView() {
        Log.d(TAG, "AddDateToAutoComplete");
        String group = KfuUser.getGroup(this);
        if(group!=null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection(CustomScheduleConstants.SCHEDULE).document(group).collection(CustomScheduleConstants.TEACHERS).limit(100).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult()
                                ) {
                            arTeachers.add(doc.get("name").toString());
                        }
                        teachersAdapter.notifyDataSetChanged();
                    }
                }
            });

            db.collection(CustomScheduleConstants.SCHEDULE).document(group).collection(CustomScheduleConstants.SUBJECTS_NAME).limit(100).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult()
                                ) {
                            arSubjects.add(doc.get("name").toString());

                        }
                        subjectsAdapter.notifyDataSetChanged();
                    }
                }
            });

            db.collection(CustomScheduleConstants.SCHEDULE).document(group).collection(CustomScheduleConstants.CAMPUSES).limit(100).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult()
                                ) {
                            arCampuses.add(doc.get("name").toString());
                        }
                        campusAdapter.notifyDataSetChanged();
                    }
                }
            });

            db.collection(CustomScheduleConstants.SCHEDULE).document(group).collection(CustomScheduleConstants.CAB_NUMBERS).limit(100).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    getScheduleFromSite();
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult()
                                ) {
                            arCabs.add(doc.get("name").toString());
                        }
                        cabAdapters.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public void getScheduleFromSite() {
        KFURestClient.get("SITE_STUDENT_SH_PR_AC.shedule?p_menu=1", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                parseScheduleFromSite(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void parseScheduleFromSite(byte[] responseBody) {
        String str = null;
        try {
            str = new String(responseBody, "windows-1251");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Document doc = Jsoup.parse(str);
        List<String> subjects  = doc.select("select").get(2).select("option").eachText();
        List<String> teachers  = doc.select("select").get(3).select("option").eachText();
        List<String> campus  = doc.select("select").get(4).select("option").eachText();

        Log.d(TAG, "parseScheduleFromSite");

        for (String s:subjects
             ) {
            addNewSubjects(s);
        }
        for (String s:teachers
             ) {
            addNewTeachers(s);
        }
        /*for (String s:campus
             ) {
            addNewCampuses(s);
        }*/
    }

    private void addNewWords(String subjectName, String teacherName, String campusName, String cabNumber) {
       addNewSubjects(subjectName);
       addNewTeachers(teacherName);
       addNewCampuses(campusName);
       addNewCabs(cabNumber);

    }

    private void addNewSubjects(String s) {
        String group = KfuUser.getGroup(this);
        if (group != null) {
            if (!arSubjects.contains(s)) {

                Map<String, Object> map = new HashMap<>();
                map.put("name", s);
                db.collection(CustomScheduleConstants.SCHEDULE).document(group).collection(CustomScheduleConstants.SUBJECTS_NAME).add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "new subject name added");
                    }
                });
            }
        }
    }

    private void addNewTeachers(String s) {
        String group = KfuUser.getGroup(this);
        if (group != null) {
            if (!arTeachers.contains(s)) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", s);
                db.collection(CustomScheduleConstants.SCHEDULE).document(group).collection(CustomScheduleConstants.TEACHERS)
                        .add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "new teacher name added");
                    }
                });
            }
        }
    }

    private void addNewCampuses(String s) {
        String group = KfuUser.getGroup(this);
        if (group != null) {
            if (!arCampuses.contains(s)) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", s);
                db.collection(CustomScheduleConstants.SCHEDULE).document(group).collection(CustomScheduleConstants.CAMPUSES)
                        .add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "new campus name added");
                    }
                });
            }
        }
        }

    private void addNewCabs(String s) {
        String group = KfuUser.getGroup(this);
        if (group != null) {
            if (!arCabs.contains(s)) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", s);
                db.collection(CustomScheduleConstants.SCHEDULE).document(group).collection(CustomScheduleConstants.CAB_NUMBERS)
                        .add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "new cab name added");
                    }
                });
            }
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


        if (TextUtils.isEmpty(subjectType)) {
            Toast.makeText(this, "Выберите тип пары", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isCustomDay) {
            if (repeatWeek == -1) {
                Toast.makeText(this, "Введите неделю для повтора", Toast.LENGTH_SHORT).show();
                return;
            }

            if (repeatDay == -1) {
                Toast.makeText(this, "Выберите день предмета", Toast.LENGTH_SHORT).show();
                return;
            }

            if(startDate==null) {
                Toast.makeText(this, "Выберите дату начало занятий", Toast.LENGTH_SHORT).show();
                return;
            }

            if(endDate==null) {
                Toast.makeText(this, "Выберите дату окончания занятий", Toast.LENGTH_SHORT).show();
                return;
            }

            if((startDate.plusWeeks(2).toDate().getTime() > endDate.toDate().getTime())) {
                Toast.makeText(this, "Минимальный период две недели. Воспользуйтесь произвольной датой.", Toast.LENGTH_SHORT).show();
                return;
            }

        } else {
            if(arCustomDays==null) {
                Toast.makeText(this, "Добавьте произвольные дни", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (arCustomDays.size() == 1) {
                    Toast.makeText(this, "Добавьте произвольные дни", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        SubjectToSchedule2 toSchedule = new SubjectToSchedule2(this);
        toSchedule.addOnSuccesListener(new SubjectToSchedule2.OnSuccessListener() {
            @Override
            public void onSuccess() {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra(CustomScheduleConstants.SUBJECT_MODEL, subject);
                setResult(RESULT_OK, intent);
                String str;
                if(!isEdit) str = "Расписание успешно добавлено!";
                else str = "Расписание успешно обновлено!";
                Toast.makeText(getApplicationContext(),str, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        //Если добавляем период
        if(!isCustomDay) {
            subject = new Subject(new Date(dateAndTime.getTimeInMillis()), new Date(dateAndTime2.getTimeInMillis()), startDate.toDate(), endDate.toDate(), actvSubjectName.getText().toString(), subjectType, actvCampus.getText().toString(), actvCab.getText().toString(), actvTeacher.getText().toString(), repeatDay, repeatWeek);
            //addSubject(subject, repeatDay, repeatWeek, startDate, endDate);
            if (isEdit) {
                subject.setId(subjectFromIntent.getId());
                subject.setArHomeWorks(subjectFromIntent.getArHomeWorks());
                toSchedule.edit(subject);
            } else toSchedule.add(subject);
        } else { //иначе, если выбраны произвольные даты
            subject = new Subject(new Date(dateAndTime.getTimeInMillis()), new Date(dateAndTime2.getTimeInMillis()), actvSubjectName.getText().toString(), subjectType, actvCampus.getText().toString(), actvCab.getText().toString(), actvTeacher.getText().toString(), arCustomDays);
            //addSubject(subject, repeatDay, repeatWeek, startDate, endDate);
            if (isEdit) {
                subject.setId(subjectFromIntent.getId());
                subject.setArHomeWorks(subjectFromIntent.getArHomeWorks());
                toSchedule.edit(subject);
            } else toSchedule.add(subject);
        }
        addNewWords(actvSubjectName.getText().toString(), actvTeacher.getText().toString(), actvCampus.getText().toString(), actvCab.getText().toString());
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
        actvCampus.setText(subject.getCampusNumber());
        actvCab.setText(subject.getCabNumber());
        if(!isImport) actvTeacher.setText(subject.getTeacherName());

        //set date to Calendar
        dateAndTime.setTime(subject.getStartTime());
        dateAndTime2.setTime(subject.getEndTime());

        //set Date to TextViews
        tvStartTIme.setText(getFormattedTime(subject.getStartTime()));
        tvEndTime.setText(getFormattedTime(subject.getEndTime()));


        if(subject.getArCustomDates() == null || subject.getArCustomDates().isEmpty()) {
            repeatDay = subject.getRepeatDay();
            repeatWeek = subject.getRepeatWeek();

            if(subject.getStartDate()!=null) {
                startDate = new LocalDate(subject.getStartDate());
                endDate = new LocalDate(subject.getEndDate());
            }

            isCustomDay = false;

        } else {
            arCustomDays = subject.getArCustomDates();
            isCustomDay = true;
        }

        subjectType = subject.getSubjectType();
        tvSubjectType.setText(subjectType);

        if(isEdit) {
            getSupportActionBar().setTitle("Редактирование");
        } else getSupportActionBar().setTitle("Добавить занятие");

    }

    public void backClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("Subject", subject);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

}
