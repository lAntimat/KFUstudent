package com.kfu.lantimat.kfustudent.Feeds.PhotoGallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kfu.lantimat.kfustudent.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by GabdrakhmanovII on 31.07.2017.
 */

public class PhotoGalleryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<PhotoGalleryItem> mList;

    public PhotoGalleryRecyclerAdapter(Context context, ArrayList<PhotoGalleryItem> itemList) {
        this.context = context;
        this.mList = itemList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_photo_gallery_item, parent, false);
        return new NormalViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String img = mList.get(position).getPhotoUrl();
        Picasso.with(context).load(img).fit().centerCrop().placeholder(R.drawable.studprofoper).into(((NormalViewHolder) holder).img);

    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return 0;
    }

    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        public NormalViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageView);
        }
    }
}
