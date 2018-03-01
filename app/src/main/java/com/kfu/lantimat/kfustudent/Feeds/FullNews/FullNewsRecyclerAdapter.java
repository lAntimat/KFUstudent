package com.kfu.lantimat.kfustudent.Feeds.FullNews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.kfu.lantimat.kfustudent.R;
import com.squareup.picasso.Picasso;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;

/**
 * Created by GabdrakhmanovII on 31.07.2017.
 */

public class FullNewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<FullNewsItem> mList;

    public FullNewsRecyclerAdapter(Context context, ArrayList<FullNewsItem> itemList) {
        this.context = context;
        this.mList = itemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case FullNewsItem.TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_full_feed_text, parent, false);
                return new TextViewHolder(view);
            case FullNewsItem.IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_full_feed_image, parent, false);
                return new ImageViewHolder(view);
            case FullNewsItem.VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_full_feed_video, parent, false);
                return new VideoViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_full_feed_text, parent, false);
                return new TextViewHolder(view);
        }

        /*view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_mark, parent, false);
        return new MarksRecyclerAdapter.ImageViewHolder(view);*/

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        switch (getItemViewType(position)) {
            case FullNewsItem.TEXT:
                ((TextViewHolder) holder).htmlTextView.setHtml(mList.get(position).getStr());
                break;
            case FullNewsItem.IMAGE:
                Picasso.with(context).load(mList.get(position).getStr()).fit().placeholder(R.drawable.studprofoper).into(((ImageViewHolder) holder).imageView);
                break;
            case FullNewsItem.VIDEO:
                String video_html = mList.get(position).getStr();
                ((VideoViewHolder) holder).webView.loadUrl(video_html);
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
            return mList.get(position).getType();
        }
        return 0;
    }


    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        HtmlTextView htmlTextView;
        public TextViewHolder(View itemView) {
            super(itemView);
            htmlTextView = itemView.findViewById(R.id.html_text);

        }
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        WebView webView;
        public VideoViewHolder(View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.webView);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            //webSettings.setLoadWithOverviewMode(true);
            //webSettings.setUseWideViewPort(true);

        }
    }
}
