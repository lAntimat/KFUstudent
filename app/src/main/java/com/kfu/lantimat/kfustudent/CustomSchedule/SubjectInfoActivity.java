package com.kfu.lantimat.kfustudent.CustomSchedule;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Schedule;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;
import com.kfu.lantimat.kfustudent.R;

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
    }
}
