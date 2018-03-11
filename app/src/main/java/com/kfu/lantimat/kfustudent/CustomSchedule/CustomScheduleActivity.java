package com.kfu.lantimat.kfustudent.CustomSchedule;

import android.animation.Animator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.crash.FirebaseCrash;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Day;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Schedule;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Weekend;
import com.kfu.lantimat.kfustudent.MainActivity;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.Schedule.ScheduleActivity;
import com.kfu.lantimat.kfustudent.utils.CheckAuth;

import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CustomScheduleActivity extends MainActivity implements CustomScheduleMVP.View  {



    public static final String EVEN_WEEK = "evenWeek";
    public static final String ODD_WEEK = "oddWeek";
    public static String ODD_WEEK_START = "";
    public static String EVEN_WEEK_START = "";

    ProgressBar progressBar;
    Button buttonSignEmpty;
    TextView textViewEmpty, tvDate;
    //Spinner spinner;
    private ImageView ivBack, ivNext;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fab;

    private LocalDate localDate;

    //private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;
    String scheduleUrl;
    String nowWeek;
    int dayOfWeek;
    int selectedDayOfWeek = -1;
    int weekOfYear;
    private List<String> daysAr = new ArrayList<>(Arrays.asList("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"));

    Presenter presenter;

    @Override
    public void showData(Weekend weekend) {
        setScheduleToViewPager(weekend);
    }

    @Override
    public void updateDataTextView(LocalDate localDate) {
        setViewPagerDayNumber(localDate);

        SimpleDateFormat sf1 = new SimpleDateFormat("d MMMM", new Locale("ru","RU"));
        String date1 = sf1.format(localDate.withDayOfWeek(DateTimeConstants.MONDAY).toDate());
        String date2 = sf1.format(localDate.withDayOfWeek(DateTimeConstants.SUNDAY).toDate());
        String weekType = "";
        weekOfYear = localDate.getWeekOfWeekyear();
        if ((localDate.getWeekOfWeekyear() & 1) == 0) {
            weekType = "Четная неделя (Нижняя)";
        } else {
            weekType = "Нечетная неделя (Верхняя)";
        }
        tvDate.setText(date1 + " - " + date2 + "\n" + weekType);

    }

    private void setViewPagerDayNumber(LocalDate localDate) {
        if(adapter!=null) {
            LocalDate ldStartWeek = localDate.withDayOfWeek(DateTimeConstants.MONDAY);
            SimpleDateFormat sf2 = new SimpleDateFormat("d", new Locale("ru", "RU"));
            for (int i = 0; i < 7; i++) {
                adapter.setPageTitle(sf2.format(ldStartWeek.plusDays(i).toDate()), i);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void openSubjectInfo(Intent intent) {
        startActivityForResult(intent, 10);
    }

    @Override
    public void openAddSubject(Schedule schedule) {
        Intent intent = new Intent(getApplicationContext(), AddScheduleActivity.class);
        intent.putExtra("Schedule", schedule);
        intent.putExtra("day", viewPager.getCurrentItem());
        startActivityForResult(intent, 10);
    }

    @Override
    public void showLoading() {
        adapter.showLoading();
    }

    @Override
    public void hideLoading() {
        adapter.hideLoading();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onOfflineMode(boolean isOfflineData) {
        if(isOfflineData) {
            fab.hide();
            showSnackBar("Вы в оффлайн режиме. Редактирование и добавление предметов не возможно.");
        } else {
            fab.show();
        }
    }

    @Override
    public void firstOpenSchedule() {



        new MaterialDialog.Builder(this)
                .title("Поздравляю! Вы первый из вашей группы.")
                .content("Давай заполним расписание.")
                .positiveText("Заполнить в ручную")
                .negativeText("Импортировать из лк")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        fab.performClick();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        startActivity(new Intent(CustomScheduleActivity.this, ScheduleActivity.class));
                    }})
                .show();
    }

    public void showSnackBar(String str) {
        Snackbar.make(findViewById(R.id.coordinator), str, Snackbar.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fabAnimation() {
        int x = topLayout.getRight();
        int y = topLayout.getBottom();

        int startRadius = 0;
        int endRadius = (int) Math.hypot(topLayout.getWidth(), topLayout.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(fab, x, y, startRadius, endRadius);
        anim.start();
    }



    public interface UpdateableFragment {
        void update(Day day, int dayNumber);


        void showLoading();
        void hideLoading();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_schedule);

        FrameLayout v = (FrameLayout) findViewById(R.id.content_frame2); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_custom_schedule, v);

        AppBarLayout.LayoutParams toolbarParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        toolbarParams.setScrollFlags(-1);
        toolbar.requestLayout();

        getSupportActionBar().setTitle("Расписание");


        ivBack = findViewById(R.id.ivBack);
        ivNext = findViewById(R.id.ivNext);
        tvDate = findViewById(R.id.tvDate);
        textViewEmpty = (TextView) findViewById(R.id.textView);
        buttonSignEmpty = (Button) findViewById(R.id.btnSign);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //ButterKnife.bind(this);
        textViewEmpty.setVisibility(View.INVISIBLE);
        buttonSignEmpty.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.fabCLick();
            }
        });

        localDate = new LocalDate(DateTimeZone.getDefault());

        nowWeek = isEvenOrOddWeek();
        LocalDate newDate = new LocalDate();
        dayOfWeek = newDate.get(DateTimeFieldType.dayOfWeek()) - 1;

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        result.setSelection(11, false);
        initViewPager();
        initPrevNextBtn();

        presenter = new Presenter(this);
        presenter.attachVIew(this);

        if (CheckAuth.isAuth()) {
            presenter.getData();
        } else showNeedLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_OK) {
            //swipeRefreshLayout.setRefreshing(true);
            presenter.getData();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initPrevNextBtn() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.prevWeek();
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.nextWeek();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Menu
        switch (item.getItemId()) {
            //When home is clicked
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String isEvenOrOddWeek() {
        Calendar calendar = Calendar.getInstance();

        if ((calendar.get(Calendar.WEEK_OF_YEAR) & 1) == 0) {
            return EVEN_WEEK;
        } else {
            return ODD_WEEK;
        }
    }

    public void showNeedLogin() {
        progressBar.setVisibility(View.INVISIBLE);
        textViewEmpty.setVisibility(View.VISIBLE);
        buttonSignEmpty.setVisibility(View.VISIBLE);
        toolbar.setTitle("Расписание");
        fab.hide();
    }

    public void onFailureMethod() {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), "Ошибка соединения", Toast.LENGTH_SHORT).show();
        FirebaseCrash.report(new Exception("Ошибка соединения"));
    }

    public void initViewPager() {
        viewPager.setOffscreenPageLimit(7);

        adapter.addFragment(new CustomScheduleFragment().newInstance(0), "Пн");
        adapter.addFragment(new CustomScheduleFragment().newInstance(1), "Вт");
        adapter.addFragment(new CustomScheduleFragment().newInstance(2), "Ср");
        adapter.addFragment(new CustomScheduleFragment().newInstance(3), "Чт");
        adapter.addFragment(new CustomScheduleFragment().newInstance(4), "Пт");
        adapter.addFragment(new CustomScheduleFragment().newInstance(5), "Сб");
        adapter.addFragment(new CustomScheduleFragment().newInstance(6), "Вс");

        viewPager.setCurrentItem(dayOfWeek, true);

        setViewPagerDayNumber(localDate);
        adapter.notifyDataSetChanged();
    }

    private void setScheduleToViewPager(Weekend weekend) {


        adapter.update(weekend.getArDays().get(0), 0);
        adapter.update(weekend.getArDays().get(1), 1);
        adapter.update(weekend.getArDays().get(2), 2);
        adapter.update(weekend.getArDays().get(3), 3);
        adapter.update(weekend.getArDays().get(4), 4);
        adapter.update(weekend.getArDays().get(5), 5);
        adapter.update(weekend.getArDays().get(6), 6);

        progressBar.setVisibility(View.INVISIBLE);
        //viewPager.setAdapter(adapter);

        viewPager.invalidate();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        Day updateData;
        int day;
        boolean isLoading;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public void clear() {
            mFragmentList.clear();
            mFragmentTitleList.clear();
        }

        //call this method to update fragments in ViewPager dynamically
        public void update(Day day, int dayNumber) {
            this.updateData = day;
            this.day = dayNumber;
            notifyDataSetChanged();
        }

        public void showLoading() {
            isLoading = true;
            notifyDataSetChanged();
        }

        public void hideLoading() {
            isLoading = false;
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof UpdateableFragment) {
                if(updateData!=null) ((UpdateableFragment) object).update(updateData, day);
                if(isLoading) ((UpdateableFragment) object).showLoading();
                else ((UpdateableFragment) object).hideLoading();
            }
            //don't return POSITION_NONE, avoid fragment recreation.
            return super.getItemPosition(object);
        }



        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void setPageTitle(String str, int position) {
            String oldStr = mFragmentTitleList.get(position);
            mFragmentTitleList.set(position, str + "\n" + daysAr.get(position));
        }
    }

}
