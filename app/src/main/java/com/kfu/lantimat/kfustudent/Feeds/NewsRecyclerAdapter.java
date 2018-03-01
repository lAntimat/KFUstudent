package com.kfu.lantimat.kfustudent.Feeds;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kfu.lantimat.kfustudent.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by GabdrakhmanovII on 31.07.2017.
 */

public class NewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<News> mList;

    public NewsRecyclerAdapter(Context context, ArrayList<News> itemList) {
        this.context = context;
        this.mList = itemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case Feed.FIXED_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_fixed_feed, parent, false);
                return new FixedFeedViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_normal_feed, parent, false);
                return new NormalFeedViewHolder(view);
        }

        /*view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_mark, parent, false);
        return new MarksRecyclerAdapter.ImageViewHolder(view);*/

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        String date = mList.get(position).getDate();
        String title = mList.get(position).getTitle();
        String subTitle = mList.get(position).getSubTitle();
        String image = mList.get(position).getImage();
        final String url = mList.get(position).getUrl();
        String visitCount = mList.get(position).getVisitCount();
        String commentCount = mList.get(position).getCommentCount();

        switch (getItemViewType(position)) {
            case Feed.NORMAL_TYPE:
                ((NormalFeedViewHolder) holder).title.setText(title);
                ((NormalFeedViewHolder) holder).subTitle.setText(subTitle);
                ((NormalFeedViewHolder) holder).date.setText(date);
                ((NormalFeedViewHolder) holder).visitCount.setText(visitCount);
                ((NormalFeedViewHolder) holder).commentCount.setText(commentCount);
                Picasso.with(context).load(image).fit().placeholder(R.drawable.bg).into(((NormalFeedViewHolder) holder).mImg);

                break;
            case Feed.FIXED_TYPE:
                ((FixedFeedViewHolder) holder).title.setText(title);
                ((FixedFeedViewHolder) holder).subTitle.setText(subTitle);
                ((FixedFeedViewHolder) holder).date.setText(date);
                Picasso.with(context).load(image).fit().placeholder(R.drawable.bg).into(((FixedFeedViewHolder) holder).mImg);
                break;
        }



    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mList != null) {
            return mList.get(position).getViewType();
        }
        return 0;
    }


    public static class FixedFeedViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView subTitle;
        private TextView date;
        private ImageView mImg;

        public FixedFeedViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvFeedTitle);
            subTitle = (TextView) itemView.findViewById(R.id.tvFeedSupportingText);
            date = (TextView) itemView.findViewById(R.id.tvFeedtextView);
            mImg = (ImageView) itemView.findViewById(R.id.ivFeedImage);
        }
    }

    public static class NormalFeedViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView subTitle;
        private TextView date;
        private TextView visitCount;
        private TextView commentCount;
        private ImageView mImg;
        private ImageView btnMore;

        public NormalFeedViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvFeedTitle);
            subTitle = (TextView) itemView.findViewById(R.id.tvFeedSupportingText);
            date = (TextView) itemView.findViewById(R.id.tvFeedtextView);
            visitCount = (TextView) itemView.findViewById(R.id.tvVisitCount);
            commentCount = (TextView) itemView.findViewById(R.id.tvCommentCount);
            mImg = (ImageView) itemView.findViewById(R.id.ivFeedImage);
            btnMore = itemView.findViewById(R.id.btnDots);
        }
    }
}
