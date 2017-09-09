package com.kfu.lantimat.kfustudent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kfu.lantimat.kfustudent.Timeline.TimeLineActivity;

/**
 * Created by User on 009 09.09.17.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Start home activity
        startActivity(new Intent(SplashActivity.this, TimeLineActivity.class));

        // close splash activity
        finish();
    }
}