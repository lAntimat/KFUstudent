package com.kfu.lantimat.kfustudent.CustomSchedule.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kfu.lantimat.kfustudent.CustomSchedule.Models.Subject;
import com.kfu.lantimat.kfustudent.Marks.Mark;
import com.kfu.lantimat.kfustudent.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by GabdrakhmanovII on 31.07.2017.
 */

public class CustomScheduleRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private  ArrayList<Subject> mList;


    public CustomScheduleRecyclerAdapter(ArrayList<Subject> itemList) {
        this.mList = itemList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case Mark.RATING_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_mark, parent, false);
                return new RatingViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_custom_schedule, parent, false);
                return new SimpleViewHolder(view);
        }

        /*view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_mark, parent, false);
        return new MarksRecyclerAdapter.SimpleViewHolder(view);*/

    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        Subject subject = mList.get(position);
        String date = sf.format(subject.getStartTime()) + " - " + sf.format(mList.get(position).getEndTime());
        String time = date;
        String name = subject.getSubjectName() + " (" + subject.getSubjectType() + ")" ;
        String place = subject.getCabNumber() + " (" + subject.getCampusNumber() + ")";
        String teacher = subject.getTeacherName();

            switch (getItemViewType(position)) {
                case Mark.RATING_TYPE:

                    //((RatingViewHolder) holder).mImg.setImageResource(getImageByNumber(mList.get(position).getSemesterInt()));
                    break;
                default:
                    ((SimpleViewHolder) holder).mTime.setText(time);
                    ((SimpleViewHolder) holder).mName.setText(name);
                    ((SimpleViewHolder) holder).mPlace.setText(place);
                    ((SimpleViewHolder) holder).mTeacher.setText(teacher);
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
            return Mark.SCORE_TYPE;
        }
        return 0;
    }




    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mTime;
        private TextView mPlace;
        private TextView mTeacher;
        public SimpleViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tvName);
            mTime = (TextView) itemView.findViewById(R.id.tvTime);
            mPlace = (TextView) itemView.findViewById(R.id.tvDesc);
            mTeacher = (TextView) itemView.findViewById(R.id.tvTeacher);
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
