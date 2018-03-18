package com.kfu.lantimat.kfustudent.CustomSchedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kfu.lantimat.kfustudent.CustomSchedule.Adapters.HomeworksRecyclerAdapter;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Schedule;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;
import com.kfu.lantimat.kfustudent.ItemClickSupport;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.utils.CreateDialog;
import com.kfu.lantimat.kfustudent.utils.KfuUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SubjectInfoActivity extends AppCompatActivity {

    public static final String TAG = "SubjectACtivity";
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
    int dayPosition;
    Subject subject;

    private ArrayList<String> arHomeWorks = new ArrayList<>();

    FloatingActionMenu fam;
    FloatingActionButton fabDelete, fabEdit, fabAdd;

    private RecyclerView recyclerView;
    private HomeworksRecyclerAdapter adapter;
    private MaterialDialog dialog;
    private EditText dialogEditText;
    private boolean isOfflineMode = false;

    //Toolbar back button click
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MyMaterialTheme);
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_subject_info);
        initView();
        initRecyclerView();

        //schedule = getIntent().getParcelableExtra("Schedule");
        subject = getIntent().getParcelableExtra(CustomScheduleConstants.SUBJECT_MODEL);
        //if(subject.getArHomeWorks()!=null) arHomeWorks.addAll(subject.getArHomeWorks());
        dayPosition = getIntent().getIntExtra("day", -1);
        isOfflineMode = getIntent().getBooleanExtra("isOffline", false);

        if(isOfflineMode) {
            fam.hideMenu(false);
        } else fam.showMenu(true);

        //subject = schedule.getArWeekends().get(weekendPosition).getArDays().get(dayPosition).getSubjects().get(subjectPosition);
        //getHomeWorks(subject.getSubjectName());
        updateUI(subject);

        getHomeWorks();


    }

    private void updateUI(Subject subject) {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SimpleDateFormat sf = new SimpleDateFormat("HH:mm", new Locale("ru", "RU"));

        String time = sf.format(subject.getStartTime()) + " - " + sf.format(subject.getEndTime());

        tvTime.setText(time);
        tvSubject.setText(subject.getSubjectName());
        tvCab.setText(subject.getCabNumber());
        tvTeacher.setText(subject.getTeacherName());
        tvSubjectType.setText(subject.getSubjectType());
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

        fam = findViewById(R.id.menu);

        fabDelete = findViewById(R.id.delete);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fam.close(false);
                final MaterialDialog dialog = CreateDialog.createPleaseWaitDialog(SubjectInfoActivity.this);
                SubjectToSchedule2 toSchedule = new SubjectToSchedule2(SubjectInfoActivity.this);
                toSchedule.addOnSuccesListener(new SubjectToSchedule2.OnSuccessListener() {
                    @Override
                    public void onSuccess() {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Занятие успешно удалено!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

                toSchedule.delete(subject);
            }
        });
        fabEdit = findViewById(R.id.edit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fam.close(false);
                Intent intent = new Intent(SubjectInfoActivity.this, AddScheduleActivity.class);
                intent.putExtra(CustomScheduleConstants.SUBJECT_MODEL, subject);
                intent.putExtra("isEdit", true);
                startActivityForResult(intent, 10);

            }
        });

        fabAdd= findViewById(R.id.add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fam.close(false);
                showHomeWorkDialog("", -1);
            }
        });
    }

    private void showHomeWorkDialog(String homeWorkText, final int homeworkPosition) {
        boolean wrapInScrollView = true;
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Добавить задание")
                .customView(R.layout.add_home_work_dialog, wrapInScrollView)
                .positiveText("Готово")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        if(homeworkPosition!=-1) {
                            arHomeWorks.set(homeworkPosition, dialogEditText.getText().toString());
                        } else arHomeWorks.add(dialogEditText.getText().toString());

                        adapter.notifyDataSetChanged();
                        addHomeworksToFirebase();
                    }
                })
                .show();
        dialogEditText = dialog.getCustomView().findViewById(R.id.editText);
        dialogEditText.setText(homeWorkText);
    }

    private void addHomeworksToFirebase() {
        String group = KfuUser.getGroup(this);
        if(group==null) {
            Toast.makeText(this, "Проблемы с номером группы. Переавторизуйтесь", Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
            subject.setArHomeWorks(arHomeWorks);
            db.collection("Schedule").document(group).collection(CustomScheduleConstants.SUBJECTS).document(subject.getId()).set(subject)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "homework setted");

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SubjectInfoActivity.this, R.string.connected_error, Toast.LENGTH_LONG).show();
                }
            });
    }

    private void getHomeWorks() {
        String group = KfuUser.getGroup(this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(CustomScheduleConstants.SCHEDULE).document(group).collection(CustomScheduleConstants.SUBJECTS).document(subject.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Subject subject = documentSnapshot.toObject(Subject.class);
                        arHomeWorks.clear();
                        arHomeWorks.addAll(subject.getArHomeWorks());
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void initRecyclerView() {
        adapter = new HomeworksRecyclerAdapter(arHomeWorks);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, final int position, View v) {
                new MaterialDialog.Builder(SubjectInfoActivity.this)
                        .items(R.array.dialog_list_homeworks)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        arHomeWorks.remove(position);
                                        adapter.notifyDataSetChanged();
                                        addHomeworksToFirebase();
                                        break;
                                    case 1:
                                        showHomeWorkDialog(arHomeWorks.get(position), position);
                                        break;
                                }
                            }
                        })
                        .show();
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data.hasExtra(CustomScheduleConstants.SUBJECT_MODEL)) {
                subject = data.getParcelableExtra(CustomScheduleConstants.SUBJECT_MODEL);
                setResult(RESULT_OK);
                updateUI(subject);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
