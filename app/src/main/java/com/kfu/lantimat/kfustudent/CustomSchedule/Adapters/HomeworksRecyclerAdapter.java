package com.kfu.lantimat.kfustudent.CustomSchedule.Adapters;

import android.net.wifi.hotspot2.pps.HomeSp;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kfu.lantimat.kfustudent.CustomSchedule.Models.HomeWorks;
import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;
import com.kfu.lantimat.kfustudent.Marks.Mark;
import com.kfu.lantimat.kfustudent.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by GabdrakhmanovII on 31.07.2017.
 */

public class HomeworksRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private  ArrayList<String> mList;


    public HomeworksRecyclerAdapter(ArrayList<String> itemList) {
        this.mList = itemList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_simple_item, parent, false);
        return new SimpleViewHolder(view);


    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SimpleViewHolder) holder).mName.setText(position + 1 + ". " + mList.get(position));

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
            return Mark.SCORE_TYPE;
        }
        return 0;
    }




    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        public SimpleViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }
}
