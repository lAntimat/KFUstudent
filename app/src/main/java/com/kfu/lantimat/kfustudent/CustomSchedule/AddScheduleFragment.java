package com.kfu.lantimat.kfustudent.CustomSchedule;

/**
 * Created by GabdrakhmanovII on 04.09.2017.
 */

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.Schedule.Schedule;
import com.kfu.lantimat.kfustudent.Schedule.ScheduleActivity;
import com.kfu.lantimat.kfustudent.Schedule.ScheduleRecyclerAdapter;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddScheduleFragment extends Fragment {

    private static final String ARG_PARAM_REPEAT_DAY = "repeatDay";
    private static final String ARG_PARAM_REPEAD_WEEK = "repeatWeek";
    private static final String ARG_PARAM_START_DATE = "startDate";
    private static final String ARG_PARAM_END_DATE = "endDate";

    DatePickerDialog.OnDateSetListener d;
    DatePickerDialog.OnDateSetListener d2;

    Calendar dateAndTime = Calendar.getInstance(Locale.UK);
    Calendar dateAndTime2 = Calendar.getInstance(Locale.UK);


    TextView tvStartDate;
    TextView tvEndDate;

    static AddScheduleActivity customScheduleActivity;

    public AddScheduleFragment() {
        // Required empty public constructor
    }

    public static AddScheduleFragment newInstance(int repeatDay, int repeatWeek, long startDate, long endDate) {

        AddScheduleFragment fragment = new AddScheduleFragment();
        Bundle args = new Bundle();
            args.putInt(ARG_PARAM_REPEAT_DAY, repeatDay);
            args.putInt(ARG_PARAM_REPEAD_WEEK, repeatWeek);
            args.putLong(ARG_PARAM_START_DATE, startDate);
            args.putLong(ARG_PARAM_END_DATE, endDate);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customScheduleActivity = ((AddScheduleActivity) getActivity());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add_schedule_options, null);
        tvStartDate = v.findViewById(R.id.tvStartDate);
        tvEndDate = v.findViewById(R.id.tvEndDate);

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        customScheduleActivity.setSupportActionBar(toolbar);
        customScheduleActivity.getSupportActionBar().setTitle("Повторения");
        customScheduleActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        customScheduleActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);


        initRadioGroup(v);
        initRadioGroupWeek(v);
        initDatePickers();
        initDateTextViewClickListeners();
        return v;

    }

    private void initRadioGroup(View view) {
        RadioGroup radioGroup = view.findViewById(R.id.RGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.Pn) {
                    customScheduleActivity.repeatDay = CustomScheduleConstants.MONDAY;
                } else if (checkedId == R.id.Bt) {
                    customScheduleActivity.repeatDay = CustomScheduleConstants.TUESDAY;
                } else if (checkedId == R.id.Cp) {
                    customScheduleActivity.repeatDay = CustomScheduleConstants.WEDNESDAY;
                } else if (checkedId == R.id.Cht) {
                    customScheduleActivity.repeatDay = CustomScheduleConstants.THURSDAY;
                } else if (checkedId == R.id.Pt) {
                    customScheduleActivity.repeatDay = CustomScheduleConstants.FRIDAY;
                } else if (checkedId == R.id.Sb) {
                    customScheduleActivity.repeatDay = CustomScheduleConstants.SATURDAY;
                } else if (checkedId == R.id.Bs) {
                    customScheduleActivity.repeatDay = CustomScheduleConstants.SUNDAY;
                }
            }

        });

        int check = getArguments().getInt(ARG_PARAM_REPEAT_DAY, -1);
        if (check != -1) {
                switch (check) {
                    case CustomScheduleConstants.MONDAY:
                        radioGroup.check(R.id.Pn);
                        break;
                    case CustomScheduleConstants.TUESDAY:
                        radioGroup.check(R.id.Bt);
                        break;
                    case CustomScheduleConstants.WEDNESDAY:
                        radioGroup.check(R.id.Cp);
                        break;
                    case CustomScheduleConstants.THURSDAY:
                        radioGroup.check(R.id.Cht);
                        break;
                    case CustomScheduleConstants.FRIDAY:
                        radioGroup.check(R.id.Pt);
                        break;
                    case CustomScheduleConstants.SATURDAY:
                        radioGroup.check(R.id.Sb);
                        break;
                    case CustomScheduleConstants.SUNDAY:
                        radioGroup.check(R.id.Bs);
                        break;
                }
        }
    }

    private void initRadioGroupWeek(View view) {


        RadioGroup radioGroup = view.findViewById(R.id.RGroupRepeatType);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.all) {
                    customScheduleActivity.repeatWeek = CustomScheduleConstants.ALL_WEEK;
                } else if (checkedId == R.id.evenWeek) {
                    customScheduleActivity.repeatWeek = CustomScheduleConstants.EVEN_WEEK;
                } else if (checkedId == R.id.oddWeek) {
                    customScheduleActivity.repeatWeek = CustomScheduleConstants.ODD_WEEK;
                }
            }

        });

        int check = getArguments().getInt(ARG_PARAM_REPEAD_WEEK, -1);
        if (check != -1) {
            switch (check) {
                case CustomScheduleConstants.ALL_WEEK:
                    radioGroup.check(R.id.all);
                    break;
                case CustomScheduleConstants.EVEN_WEEK:
                    radioGroup.check(R.id.evenWeek);
                    break;
                case CustomScheduleConstants.ODD_WEEK:
                    radioGroup.check(R.id.oddWeek);
                    break;

            }
        }
    }

    private void initDatePickers() {

        long start = getArguments().getLong(ARG_PARAM_START_DATE, -1);
        long end = getArguments().getLong(ARG_PARAM_END_DATE, -1);
        if (start != -1) dateAndTime.setTimeInMillis(start);
        else {
            dateAndTime.set(Calendar.MONTH, 1);
            dateAndTime.set(Calendar.DAY_OF_MONTH, 1);
        }
        if (end != -1) dateAndTime2.setTimeInMillis(end);
        else {
            dateAndTime2.set(Calendar.MONTH, 6);
            dateAndTime2.set(Calendar.DAY_OF_MONTH, 31);
        }

        Date date = new Date(dateAndTime.getTimeInMillis());
        tvStartDate.setText(formatDate(date));
        customScheduleActivity.startDate = new LocalDate(dateAndTime);

        Date date2 = new Date(dateAndTime2.getTimeInMillis());
        tvEndDate.setText(formatDate(date2));
        customScheduleActivity.endDate = new LocalDate(dateAndTime2);


        d = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                dateAndTime.set(Calendar.YEAR, i);
                dateAndTime.set(Calendar.MONTH, i1);
                dateAndTime.set(Calendar.DAY_OF_MONTH, i2);
                Date date = new Date(dateAndTime.getTimeInMillis());
                tvStartDate.setText(formatDate(date));
                customScheduleActivity.startDate = new LocalDate(dateAndTime);
            }
        };

        d2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                dateAndTime2.set(Calendar.YEAR, i);
                dateAndTime2.set(Calendar.MONTH, i1);
                dateAndTime2.set(Calendar.DAY_OF_MONTH, i2);
                Date date = new Date(dateAndTime2.getTimeInMillis());
                tvEndDate.setText(formatDate(date));
                customScheduleActivity.endDate = new LocalDate(dateAndTime2);

            }
        };
    }

    private void initDateTextViewClickListeners() {

        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), d,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });


        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), d2,
                        dateAndTime2.get(Calendar.YEAR),
                        dateAndTime2.get(Calendar.MONTH),
                        dateAndTime2.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

    }

    private String formatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM", new Locale("ru", "RU"));
        return simpleDateFormat.format(date);
    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}
