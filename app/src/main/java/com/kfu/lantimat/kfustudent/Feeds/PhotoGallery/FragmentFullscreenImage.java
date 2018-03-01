package com.kfu.lantimat.kfustudent.Feeds.PhotoGallery;

/**
 * Created by GabdrakhmanovII on 04.09.2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kfu.lantimat.kfustudent.R;
import com.kfu.lantimat.kfustudent.utils.WrapContentViewPager;

import java.util.ArrayList;

public class FragmentFullscreenImage extends Fragment implements PhotoGalleryActivity.FragmentFullSizeImageListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "position";

    RecyclerView recyclerView;
    FullscreenImageAdapter adapter;
    ArrayList<PhotoGalleryItem> ar;
    TextView textView;
    ImageView imageView;
    ProgressBar progressBar;
    PhotoGalleryPresenter presenter;
    String url;
    WrapContentViewPager viewPager;
    int viewPagerPosition = 0;

    public FragmentFullscreenImage() {
        // Required empty public constructor
    }

    public static FragmentFullscreenImage newInstance(int position, ArrayList<PhotoGalleryItem> ar) {

        FragmentFullscreenImage fragment = new FragmentFullscreenImage();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, ar);
        args.putInt(ARG_PARAM2, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ar = new ArrayList<>();

        if (getArguments() != null) {
            ar = getArguments().getParcelableArrayList(ARG_PARAM1);
            viewPagerPosition = getArguments().getInt(ARG_PARAM2);
        }

        presenter = ((PhotoGalleryActivity) getActivity()).presenter;
        ((PhotoGalleryActivity) getActivity()).registerFullSizeImageListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_photo_full_size, null);

        textView = v.findViewById(R.id.textView);
        imageView = v.findViewById(R.id.imageView);
        progressBar = v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        adapter = new FullscreenImageAdapter(getContext(), ar);
        initViewPager(v);

        url = getActivity().getIntent().getStringExtra("url");

        return v;
    }

    private void initViewPager(View v) {
        viewPager = v.findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(viewPagerPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPagerPosition = position;
                textView.setText(position + 1 + "/" + viewPager.getAdapter().getCount());
                if(position + 3 == viewPager.getAdapter().getCount()) presenter.loadMore();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        textView.setText(viewPagerPosition + 1 + "/" + viewPager.getAdapter().getCount());
    }


    private void emptyPic() {
        if (ar.size() == 0) {
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAdd(ArrayList<PhotoGalleryItem> ar) {
        this.ar.clear();
        this.ar.addAll(ar);
        adapter.notifyDataSetChanged();
        textView.setText(viewPagerPosition + 1 + "/" + viewPager.getAdapter().getCount());
    }

    @Override
    public void setPosition(int position) {
        viewPagerPosition = position;
        viewPager.setCurrentItem(position);
    }
}
