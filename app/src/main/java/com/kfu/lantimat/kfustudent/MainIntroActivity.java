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

        /*addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.md_amber_800)
                        .buttonsColor(R.color.colorAccent)
                        .image(R.drawable.intro_1)
                        .title("Что за приложение?")
                        .description("Это приложения для удобного доступа к личнмоу кабинета сайта КФУ. Пока что это первая версия (бета), поэтому сильно не ругайся, если будут мелкие ошибки :)")
                        .build()
                );*/

        addSlide(new SimpleSlide.Builder()
                .title("Что за приложение?")
                .description("Это приложения для удобного доступа к личному кабинету сайта КФУ. Пока что это первая версия (бета), в которой возможны незначительные неполадки :) ")
                .image(R.drawable.logo)
                .background(R.color.md_cyan_800)
                //.backgroundDark(R.color.background_dark_1)
                .scrollable(false)
                //.permission(Manifest.permission.CAMERA)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Раписание")
                .description("Cмотри расписание в удобном виде и не опаздывай на пары ;)")
                .image(R.drawable.calendar_flat)
                .background(R.color.md_teal_800)
                //.backgroundDark(R.color.background_dark_1)
                .scrollable(false)
                //.permission(Manifest.permission.CAMERA)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Успеваемость")
                .description("И не забывай смотреть за баллами")
                .image(R.drawable.study_flat)
                .background(R.color.md_indigo_800)
                //.backgroundDark(R.color.background_dark_1)
                .scrollable(false)
                //.permission(Manifest.permission.CAMERA)
                .build());
    }
}
