package com.kfu.lantimat.kfustudent.Feeds.PhotoGallery;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.kfu.lantimat.kfustudent.Feeds.Repository;
import com.kfu.lantimat.kfustudent.LoadingView;
import com.kfu.lantimat.kfustudent.R;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryActivity extends AppCompatActivity implements PhotoGalleryView {

       //Spinner spinner;

    //private Toolbar toolbar;
    private TabLayout tabLayout;
    public ViewPager viewPager;
    ViewPagerAdapter adapter;
    Toolbar toolbar;
    ProgressBar progressBar;
    public PhotoGalleryPresenter presenter;
    public PhotoGalleryFragment photoGalleryFragment;

    FragmentPhotoGalleryListener photoGalleryListener;
    FragmentFullSizeImageListener fullSizeImageListener;

    static FragmentFullscreenImage fragmentFullscreenImage;

    @Override
    public void showLoading() {
        if(photoGalleryListener!=null) photoGalleryListener.showLoading();
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
        photoGalleryListener.hideLoading();
    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void showPhotos(ArrayList<PhotoGalleryItem> arPhotos) {
        photoGalleryListener.showPhotos(arPhotos);
        if(fullSizeImageListener!=null) fullSizeImageListener.onAdd(arPhotos);
    }

    @Override
    public void openFullSizePhoto(int position, ArrayList<PhotoGalleryItem> ar) {
        //viewPager.setCurrentItem(1);
        initFullsizeFragment(position, ar);
        /*if(fullSizeImageListener!=null) {
            fullSizeImageListener.onAdd(ar);
            fullSizeImageListener.setPosition(position);
        }*/
    }

    @Override
    public void setLoadedCount(int loadedCount, int size) {
        photoGalleryListener.setLoadedCount(loadedCount, size);
    }

    @Override
    public void showToolbarLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAdd(ArrayList<PhotoGalleryItem> ar) {
        fullSizeImageListener.onAdd(ar);
    }

    @Override
    public void setPosition(int position) {
        fullSizeImageListener.setPosition(position);
    }

    public interface FragmentPhotoGalleryListener extends LoadingView {

        void onAdd(ArrayList<PhotoGalleryItem> ar);

        void setPosition(int position);

        void showPhotos(ArrayList<PhotoGalleryItem> arPhotos);

        void openFullSizePhoto(int position);

        void setLoadedCount(int loadedCount, int size);
    }

    public void registerPhotoGalleryListener(FragmentPhotoGalleryListener listener) {
        photoGalleryListener = listener;
    }

    public interface FragmentFullSizeImageListener {

        void onAdd(ArrayList<PhotoGalleryItem> ar);

        void setPosition(int position);
    }

    public void registerFullSizeImageListener(FragmentFullSizeImageListener listener) {
        fullSizeImageListener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MyMaterialTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        //FrameLayout v = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        //getLayoutInflater().inflate(R.layout.activity_photo, v);

        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Фотогалерея");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Repository.setActivity(this);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setupWithViewPager(viewPager);

        initViewPager();

        presenter = new PhotoGalleryPresenter(this);

        String url = getIntent().getStringExtra("url");
        presenter.loadDate(url);

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

    @Override
    public void onBackPressed() {
        //if(getFragmentManager().getBackStackEntryCount() > 0) getFragmentManager().popBackStack();
        super.onBackPressed();
    }

    private void initFullsizeFragment(int position, ArrayList<PhotoGalleryItem> ar) {
        fragmentFullscreenImage = FragmentFullscreenImage.newInstance(position, ar);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.content_frame, fragmentFullscreenImage)
                .addToBackStack("list")
                .commit();
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        //ft.hide(fragmentFullscreenImage);
    }

    public void initViewPager() {
        photoGalleryFragment = new PhotoGalleryFragment();

        adapter.addFragment(photoGalleryFragment, "Фото");
        //adapter.addFragment(new FragmentFullscreenImage(), "FullScreen");
        adapter.notifyDataSetChanged();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


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

        @Override
        public int getItemPosition(Object object) {

            return super.getItemPosition(object);
        }



        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
