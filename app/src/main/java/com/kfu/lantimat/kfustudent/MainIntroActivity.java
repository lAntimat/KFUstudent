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

        setButtonCtaVisible(false);

        addSlide(new SimpleSlide.Builder()
                .title("Дорогой друг!")
                .description("Как тебе известно, расписание в личном кабинете сайта не всегда верное, поэтому есть раздел «Мое расписание»")
                .image(R.drawable.intro_1)
                .background(R.color.accent)
                .backgroundDark(R.color.primary_dark)
                .scrollable(true)
                //.permission(Manifest.permission.CAMERA)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Моё расписание")
                .description("Тут можно изменять, добавлять новые предметы и записывать домашнее задание. Все добавленные предметы появятся у твоих одногруппников")
                .image(R.drawable.intro_3)
                .background(R.color.accent)
                .backgroundDark(R.color.primary_dark)
                .scrollable(false)
                //.permission(Manifest.permission.CAMERA)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Раcписание ЛК")
                .description("Здесь можно посмотреть расписание с личного кабинета и, при желании, импортировать его в «Мое расписание»")
                .image(R.drawable.intro_2)
                .background(R.color.accent)
                .backgroundDark(R.color.primary_dark)
                .scrollable(false)
                //.permission(Manifest.permission.CAMERA)
                .build());


    }
}
