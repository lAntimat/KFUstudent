package com.kfu.lantimat.kfustudent.Marks;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.kfu.lantimat.kfustudent.R;

import java.util.ArrayList;

/**
 * Created by GabdrakhmanovII on 31.07.2017.
 */

public class MarksRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int SWITCH_TYPE = 0;
    public static final int HISTORY_TYPE = 1;

    private  ArrayList<Mark> mList;

    public MarksRecyclerAdapter(ArrayList<Mark> itemList) {
        this.mList = itemList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        /*switch (viewType) {
            case SWITCH_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
                return new ControlRecyclerAdapter.CityViewHolder(view);
            case HISTORY_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
                return new ControlRecyclerAdapter.EventViewHolder(view);
        }*/

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_mark, parent, false);
        return new MarksRecyclerAdapter.SimpleViewHolder(view);

    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String itemText = mList.get(position).getNameString();
        String data = mList.get(position).getTestString();
        /*ControlItemsModel object = mList.get(position);
        if (object != null) {
            switch (object.getType()) {
                case SWITCH_TYPE:
                    ((ControlRecyclerAdapter.CityViewHolder) holder).mTitle.setText(object.getName());
                    break;
                case HISTORY_TYPE:
                    ((ControlRecyclerAdapter.EventViewHolder) holder).mTitle.setText(object.getName());
                    ((ControlRecyclerAdapter.EventViewHolder) holder).mDescription.setText(object.getDescription());
                    break;
            }
        }*/

        ((SimpleViewHolder) holder).mTitle.setText(itemText);
        ((SimpleViewHolder) holder).mDesc.setText(data);
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
            //ControlItemsModel object = mList.get(position);
            //if (object != null) {
            // return object.getType();
            //  }
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
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mDescription;
        public EventViewHolder(View itemView) {
            super(itemView);
            //mTitle = (TextView) itemView.findViewById(R.id.textView);
            //mDescription = (TextView) itemView.findViewById(R.id.textView2);
        }
    }

}
