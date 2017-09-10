package com.kfu.lantimat.kfustudent;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;


/**
 * Created by GabdrakhmanovII on 07.09.2017.
 */

public class MainIntroActivity extends IntroActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SimpleSlide.Builder()
                .title("Привет!")
                .description("Это приложения для удобного доступа к личному кабинету сайта КФУ. Пока что это первая версия (бета), в которой возможны незначительные неполадки :) ")

                .background(R.color.accent)
                .backgroundDark(R.color.primary_dark)
                .scrollable(true)
                //.permission(Manifest.permission.CAMERA)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Раcписание")
                .description("Cмотри расписание в удобном виде и не пропускай пары ;)")
                .image(R.drawable.calendar_flat_intro)
                .background(R.color.md_teal_800)
                .backgroundDark(R.color.primary_dark)

                .scrollable(false)
                //.permission(Manifest.permission.CAMERA)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Успеваемость")
                .description("А так же не забывай следить за баллами и не набирать долгов. Удачной учебы!")
                .image(R.drawable.study_circle_flat)
                .background(R.color.md_cyan_800)
                .backgroundDark(R.color.primary_dark)
                .scrollable(false)
                //.permission(Manifest.permission.CAMERA)
                .build());
    }
}
