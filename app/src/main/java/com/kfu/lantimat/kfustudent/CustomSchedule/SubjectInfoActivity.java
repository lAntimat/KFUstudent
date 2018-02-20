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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kfu.lantimat.kfustudent.CustomSchedule.Adapters.HomeworksRecyclerAdapter;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.HomeWorks;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Schedule;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;
import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.utils.CreateDialog;

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
    int subjectPosition;
    int weekendPosition;
    int dayPosition;
    Subject subject;

    private HomeWorks homeWorks;
    private ArrayList<String> ar = new ArrayList<>();

    FloatingActionMenu fam;
    FloatingActionButton fabDelete, fabEdit, fabAdd;

    private RecyclerView recyclerView;
    private HomeworksRecyclerAdapter adapter;
    private MaterialDialog dialog;
    private EditText dialogEditText;

    //Toolbar back button click
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_subject_info);
        initView();
        initRecyclerView();

        schedule = getIntent().getParcelableExtra("Schedule");
        subjectPosition = getIntent().getIntExtra("subject", -1);
        weekendPosition = getIntent().getIntExtra("week", -1);
        dayPosition = getIntent().getIntExtra("day", -1);

        subject = schedule.getArWeekends().get(weekendPosition).getArDays().get(dayPosition).getSubjects().get(subjectPosition);
        getHomeWorks(subject.getSubjectName());
        updateUI(subject);


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
                SubjectToSchedule toSchedule = new SubjectToSchedule();
                toSchedule.addOnSuccesListener(new SubjectToSchedule.OnSuccessListener() {
                    @Override
                    public void onSuccess() {
                        dialog.dismiss();
                    }
                });

                toSchedule.delete(schedule, subject, subjectPosition);
            }
        });
        fabEdit = findViewById(R.id.edit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fam.close(false);
                Intent intent = new Intent(SubjectInfoActivity.this, AddScheduleActivity.class);
                intent.putExtra("Schedule", schedule);
                intent.putExtra("subject", subjectPosition);
                intent.putExtra("week", weekendPosition);
                intent.putExtra("day", dayPosition);
                intent.putExtra("homeworks", homeWorks);
                intent.putExtra("isEdit", true);
                startActivityForResult(intent, 10);

            }
        });

        fabAdd= findViewById(R.id.add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fam.close(false);
                addHomeWorkDialog();
            }
        });
    }

    private void addHomeWorkDialog() {
        boolean wrapInScrollView = true;
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Добавить задание")
                .customView(R.layout.add_home_work_dialog, wrapInScrollView)
                .positiveText("Готово")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialogEditText = dialog.getCustomView().findViewById(R.id.editText);
                        ar.add(dialogEditText.getText().toString());
                        if(homeWorks!=null) {
                            homeWorks.setArHomeworks(ar);
                        } else homeWorks = new HomeWorks(subject.getSubjectName(), ar);
                        adapter.notifyDataSetChanged();
                        addHomeworksToFirebase();
                    }
                })
                .show();
    }

    private void addHomeworksToFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(homeWorks.getId()!=null) {
            db.collection("Schedule").document("2141115").collection("homeworks").document(homeWorks.getId()).set(homeWorks).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "homework setted");

                }
            });
        } else {
            db.collection("Schedule").document("2141115").collection("homeworks").add(homeWorks).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "homework added");
                    homeWorks.setId(documentReference.getId());
                }
            });
        }
    }

    private void initRecyclerView() {
        adapter = new HomeworksRecyclerAdapter(ar);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void getHomeWorks(String subjectName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Schedule").document("2141115").collection("homeworks")
                .whereEqualTo("subjectName", subjectName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        for (DocumentSnapshot doc : documentSnapshots.getDocuments()) {
                            homeWorks = doc.toObject(HomeWorks.class);
                            homeWorks.setId(doc.getId());
                            ar.clear();
                            ar.addAll(homeWorks.getArHomeworks());
                        }
                       adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        subject = data.getParcelableExtra("Subject");
        if (subject != null) {
            setResult(RESULT_OK);
            updateUI(subject);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
