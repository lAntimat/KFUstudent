package com.kfu.lantimat.kfustudent.Marks;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.kfu.lantimat.kfustudent.R;

import java.util.ArrayList;

/**
 * Created by GabdrakhmanovII on 31.07.2017.
 */

public class MarksRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private  ArrayList<Mark> mList;
    private int anchorPosition = -1;


    public MarksRecyclerAdapter(ArrayList<Mark> itemList) {
        this.mList = itemList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case Mark.RATING_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_mark_rating, parent, false);
                return new RatingViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_mark, parent, false);
                return new SimpleViewHolder(view);
        }

        /*view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_mark, parent, false);
        return new MarksRecyclerAdapter.SimpleViewHolder(view);*/

    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String itemText = mList.get(position).getNameString();
        CharSequence data = mList.get(position).getTestString();

            switch (getItemViewType(position)) {
                case Mark.RATING_TYPE:
                    ((RatingViewHolder) holder).mTitle.setText(itemText);
                    ((RatingViewHolder) holder).mDesc.setText(data);
                    ((RatingViewHolder) holder).mImg.setImageResource(getImageByNumber(mList.get(position).getSemesterInt()));
                    if(position%2==0) anchorPosition = position;
                    break;
                default:
                    ((SimpleViewHolder) holder).mTitle.setText(itemText);
                    ((SimpleViewHolder) holder).mDesc.setText(data);
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
            return mList.get(position).getmViewType();
        }
        return 0;
    }

    public int returnAnchorPosition() {
        return anchorPosition;
    }

    private int getImageByNumber(int i) {
        switch (i) {
            case 1: return R.drawable.material_1;
            case 2: return R.drawable.material_2;
            case 3: return R.drawable.material_3;
            case 4: return R.drawable.material_4;
            case 5: return R.drawable.material_5;
            case 6: return R.drawable.material_6;
            case 7: return R.drawable.material_7;
            case 8: return R.drawable.material_8;
            case 9: return R.drawable.material_9;
        }
        return 0;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mDesc;
        public SimpleViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tvName);
            mDesc = (TextView) itemView.findViewById(R.id.tvDesc);
        }
    }
    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mDesc;
        private ImageView mImg;
        public RatingViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tvName);
            mDesc = (TextView) itemView.findViewById(R.id.tvDesc);
            mImg = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
