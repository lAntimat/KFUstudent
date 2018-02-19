package com.kfu.lantimat.kfustudent.CustomSchedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Schedule;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.utils.CreateDialog;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SubjectInfoActivity extends Activity {

    protected ImageView appBarImage;
    protected Toolbar toolbar;
    protected TextView tvTime;
    protected TextView tvSubject;
    protected TextView tvSubjectType;
    protected AppBarLayout appbar;
    protected TextView tvTeacher;
    protected TextView tvCab;
    protected TextView tvCabTitle;
    protected TextView tvTeacherTitle;

    Schedule schedule;
    int subjectPosition;
    int weekendPosition;
    int dayPosition;
    Subject subject;

    FloatingActionButton fabDelete, fabEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_subject_info);
        initView();

        schedule = getIntent().getParcelableExtra("Schedule");
        subjectPosition = getIntent().getIntExtra("subject", -1);
        weekendPosition = getIntent().getIntExtra("week", -1);
        dayPosition = getIntent().getIntExtra("day", -1);

        subject = schedule.getArWeekends().get(weekendPosition).getArDays().get(dayPosition).getSubjects().get(subjectPosition);

        SimpleDateFormat sf = new SimpleDateFormat("HH:mm", new Locale("ru","RU"));

        String time = sf.format(subject.getStartTime()) + " - " + sf.format(subject.getEndTime());

        tvTime.setText(time);
        tvSubject.setText(subject.getSubjectName());
        tvCab.setText(subject.getCabNumber());
        tvTeacher.setText(subject.getTeacherName());


    }

    private void initView() {
        appBarImage = (ImageView) findViewById(R.id.app_bar_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvSubject = (TextView) findViewById(R.id.tvSubject);
        tvSubjectType = (TextView) findViewById(R.id.tvSubjectType);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        tvTeacher = (TextView) findViewById(R.id.tvTeacher);
        tvCab = (TextView) findViewById(R.id.tvCab);
        tvCabTitle = (TextView) findViewById(R.id.tvCabTitle);
        tvTeacherTitle = (TextView) findViewById(R.id.tvTeacherTitle);

        fabDelete = findViewById(R.id.delete);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog dialog = CreateDialog.createPleaseWaitDialog(SubjectInfoActivity.this);
                SubjectToSchedule toSchedule = new SubjectToSchedule();
                toSchedule.addOnSuccesListener(new SubjectToSchedule.OnSuccessListener() {
                    @Override
                    public void onSuccess() {
                        dialog.dismiss();
                    }
                });

                toSchedule.addingMethod(schedule, subject, subjectPosition, SubjectToSchedule.DELETE);
            }
        });
        fabEdit = findViewById(R.id.edit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubjectInfoActivity.this, AddScheduleActivity.class);
                intent.putExtra("Schedule", schedule);
                intent.putExtra("subject", subjectPosition);
                intent.putExtra("week", weekendPosition);
                intent.putExtra("day", dayPosition);
                intent.putExtra("isEdit", true);
                startActivity(intent);
            }
        });
    }
}
