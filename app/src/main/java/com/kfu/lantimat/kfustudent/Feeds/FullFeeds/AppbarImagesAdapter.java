package com.kfu.lantimat.kfustudent.Feeds.FullFeeds;

/**
 * Created by lAntimat on 03.12.2017.
 */
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kfu.lantimat.kfustudent.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AppbarImagesAdapter extends PagerAdapter {

    private ArrayList<FullFeedAppBarItems> images;
    private LayoutInflater inflater;
    private Context context;

    public AppbarImagesAdapter(Context context, ArrayList<FullFeedAppBarItems> images) {
        this.context = context;
        this.images=images;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View myImageLayout = inflater.inflate(R.layout.appbar_image_slide, view, false);
        ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.image);
        ImageView imageView = myImageLayout.findViewById(R.id.play);
        imageView.setVisibility(View.INVISIBLE);

        if(images.get(position).getType() == FullFeedAppBarItems.VIDEO) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(images.get(position).getVideo()));
                    context.startActivity(i);
                }
            });
        }
        Picasso.with(context).load(images.get(position).getStr()).into(myImage);
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
