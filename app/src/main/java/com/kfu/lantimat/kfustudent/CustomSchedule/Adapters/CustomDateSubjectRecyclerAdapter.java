package com.kfu.lantimat.kfustudent.CustomSchedule.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kfu.lantimat.kfustudent.Marks.Mark;
import com.kfu.lantimat.kfustudent.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by GabdrakhmanovII on 31.07.2017.
 */

public class CustomDateSubjectRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private  ArrayList<Date> mList;


    public CustomDateSubjectRecyclerAdapter(ArrayList<Date> itemList) {
        this.mList = itemList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_add_days_item, parent, false);
        return new SimpleViewHolder(view);


    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == mList.size() - 1) {
            ((SimpleViewHolder) holder).mName.setText("Добавить");
            ((SimpleViewHolder) holder).btnPlus.setImageResource(R.drawable.ic_plus_black_24dp);
        } else {
            SimpleDateFormat sf = new SimpleDateFormat("d MMMM", new Locale("ru","RU"));
            ((SimpleViewHolder) holder).mName.setText(sf.format(mList.get(position)));
            ((SimpleViewHolder) holder).btnPlus.setImageResource(R.drawable.ic_delete_black_24dp);
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
            return Mark.SCORE_TYPE;
        }
        return 0;
    }




    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private ImageButton btnPlus;
        public SimpleViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tvTitle);
            btnPlus = itemView.findViewById(R.id.imageButton);
        }
    }
}
