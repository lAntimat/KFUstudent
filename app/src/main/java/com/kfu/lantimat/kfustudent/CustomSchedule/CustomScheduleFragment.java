package com.kfu.lantimat.kfustudent.CustomSchedule;

/**
 * Created by GabdrakhmanovII on 04.09.2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kfu.lantimat.kfustudent.CustomSchedule.Adapters.CustomScheduleRecyclerAdapter;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Day;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;
import com.kfu.lantimat.kfustudent.ItemClickSupport;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.Schedule.ScheduleRecyclerAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class CustomScheduleFragment extends Fragment implements
        CustomScheduleActivity.UpdateableFragment {

    private final String ARG_PARAM1 = "param1";

    RecyclerView recyclerView;
    CustomScheduleRecyclerAdapter scheduleRecyclerAdapter;
    ArrayList<Subject> arSubjects;
    int day;
    //@BindView(R.id.textView)
    TextView textView;
    //@BindView(R.id.imageView)
    ImageView imageView;
    //Unbinder unbinder;
    //@BindView(R.id.progressBar)
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;

    public CustomScheduleFragment() {
        // Required empty public constructor
    }

    public CustomScheduleFragment newInstance(int day) {

        CustomScheduleFragment fragment = new CustomScheduleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, day);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            day = getArguments().getInt(ARG_PARAM1);
        }

        arSubjects = new ArrayList<>();
        scheduleRecyclerAdapter = new CustomScheduleRecyclerAdapter(arSubjects);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));
        recyclerView.setAdapter(scheduleRecyclerAdapter);

        scheduleRecyclerAdapter.setOnDotsClickListener(new CustomScheduleRecyclerAdapter.OnDotsClickListener() {
            @Override
            public void onClick(int position) {
                showOptionsDialog(position);
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_schedule, null);
        //unbinder = ButterKnife.bind(this, v);

        textView = v.findViewById(R.id.textView);
        imageView = v.findViewById(R.id.imageView);
        progressBar = v.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        initRecyclerView();

        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_refresh_colors));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((CustomScheduleActivity)getActivity()).presenter.getData();
            }
        });


        //addDataToAdapter(string);
        /*String marksCashStr = SharedPreferenceHelper.getSharedPreferenceString(getContext(), "marks" + string, "-1"); //Достаем из памяти строку с успеваемостью;
        if (!marksCashStr.equalsIgnoreCase("-1")) getScheduleFromCash(marksCashStr);
        getSchedule();*/

        return v;

    }

    @Override
    public void update(Day day, int dayNumber) {
        if(this.day==dayNumber) addDataToAdapter(day.getSubjects());
    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private Date getTimeWithoutData(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.YEAR, 1970);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private void addDataToAdapter(ArrayList<Subject> arSubjects) {

        this.arSubjects.clear();

        Collections.sort(arSubjects, new Comparator<Subject>() {
            public int compare(Subject o1, Subject o2) {
                return getTimeWithoutData(o1.getStartTime()).compareTo(getTimeWithoutData(o2.getStartTime()));
            }
        });

        this.arSubjects.addAll(arSubjects);

        //Collections.reverse(arSubjects);
        progressBar.setVisibility(View.INVISIBLE);
        scheduleRecyclerAdapter.notifyDataSetChanged();
        recyclerView.invalidate();

        swipeRefreshLayout.setRefreshing(false);
        emptyPic();

    }

    private void emptyPic() {
        if (arSubjects.size() == 0) {
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }
    }

    private void showOptionsDialog(final int position) {
        new MaterialDialog.Builder(getActivity())
                .items(R.array.dialog_list_my_schedule)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        ((CustomScheduleActivity)getActivity()).presenter.recyclerItemClick(arSubjects.get(position));
                    }
                })
                .show();
    }

    @Override
    public void onStop() {
        //if(loadScheduleFromCash!=null) loadScheduleFromCash.cancel(true);
        //if(parseSchedule!=null) parseSchedule.cancel(true);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("ScheduleFragment", "destroyVIew");
        //unbinder.unbind();
    }



}
