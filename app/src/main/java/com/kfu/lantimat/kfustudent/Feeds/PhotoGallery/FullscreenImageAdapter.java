package com.kfu.lantimat.kfustudent.Feeds.PhotoGallery;

/**
 * Created by GabdrakhmanovII on 06.12.2017.
 */

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chuross.flinglayout.FlingLayout;
import com.kfu.lantimat.kfustudent.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;

public class FullscreenImageAdapter extends PagerAdapter {

    public static int fullScreenImagePosition;

    private Context context;
    private ArrayList<PhotoGalleryItem> ar;
    //private ImagesArrayList _imagesArrayList;
    private LayoutInflater inflater;
    ProgressBar progressBar;
    TextView textView;
    Context ctx;
    final String TAG = "FullScreenImageAdapter";

    // constructor
    public FullscreenImageAdapter(Context context, ArrayList<PhotoGalleryItem> ar) {
        this.context = context;
        this.ar = ar;

    }

    @Override
    public int getCount() {
        return this.ar.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView imgDisplay;
        Button btnClose;

        fullScreenImagePosition = position;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.viewpager_item_image, container,
                false);
        final FlingLayout flingLayout = viewLayout.findViewById(R.id.fling_layout);
        flingLayout.setBackgroundColor(Color.argb(Math.round(230), 0, 0, 0));

        flingLayout.setDismissListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                Toast.makeText(context, "dismiss!!", Toast.LENGTH_LONG).show();
                ((PhotoGalleryActivity) context).onBackPressed();
                return Unit.INSTANCE;
            }
        });
        flingLayout.setPositionChangeListener(new Function3<Integer, Integer, Float, Unit>() {
            @Override
            public Unit invoke(Integer top, Integer left, Float dragRangeRate) {
                flingLayout.setBackgroundColor(Color.argb(Math.round(230 * (1.0F - dragRangeRate)), 0, 0, 0));
                return Unit.INSTANCE;
            }
        });

        imgDisplay = viewLayout.findViewById(R.id.photo_view);

        imgDisplay.setOnScaleChangeListener(new OnScaleChangedListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
                flingLayout.setDragEnabled(scaleFactor <= 1F);
            }
        });

        textView = viewLayout.findViewById(R.id.textView);
        progressBar = viewLayout.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        //textView.setText(position + "/" + ar.size());

        Uri uri = Uri.parse(ar.get(position).getPhotoBigSizeUrl());
        Picasso.with(context)
                .load(uri)
                .into(imgDisplay, new PicassoCallback(progressBar));

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    final class PicassoCallback implements Callback {

        private ProgressBar progressBar;

        public PicassoCallback(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        @Override
        public void onSuccess() {
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onError() {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
