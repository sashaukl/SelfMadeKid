package com.example.selfmadekid.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;

import androidx.recyclerview.widget.RecyclerView;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.selfmadekid.R;
import com.example.selfmadekid.data.ChildTask;

import java.util.ArrayList;


public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder> {


    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private Context context;
    private ArrayList<ChildTask> container;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;


    // data is passed into the constructor
    public TaskRecyclerAdapter(Context context, ArrayList<ChildTask> arrayList) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.container = arrayList;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        System.out.println(viewType);
        switch (viewType) {
            case TYPE_HEADER:
                v = mInflater.inflate(R.layout.header_schedule, parent, false);
                break;
            default:
                v = mInflater.inflate(R.layout.child_item, parent, false);
        }

        return new ViewHolder(v);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int type = getItemViewType(position);
        System.out.println(holder.mHeaderText);
        switch (type) {

            case TYPE_HEADER:
                String str = container.get(position).getHourToStartTask() + ":00";
                holder.mHeaderText.setText(str) ;

            case TYPE_ITEM:
                ChildTask childTask = getItem(position);
                holder.child_item_text.setText(childTask.getTaskText());
                //holder.progressBar.
                //Resources res = context.getResources();
                Drawable drawable = context.getDrawable(R.drawable.progress_bar_circle);
                holder.progressBar.setSecondaryProgress(childTask.getNecessaryProgress()); // Secondary Progress
                holder.progressBar.setMax(childTask.getNecessaryProgress()); // Maximum Progress
                holder.progressBar.setProgressDrawable(drawable);
                holder.progressBar.setProgress(getItem(position).getCurrentProgress());
                holder.mProgressText.setText(childTask.getCurrentProgress() + "/" + childTask.getNecessaryProgress());
                int duration = childTask.getDuration();
                if (duration%10 == 1){
                    if (duration%100!=11){
                        holder.mDuration.setText(context.getString(R.string.task_duration_2, duration));
                    }
                }else if(duration%10>=2 && duration%10<=4){
                    holder.mDuration.setText(context.getString(R.string.task_duration_3, duration));
                }else{
                    holder.mDuration.setText(context.getString(R.string.task_duration_1, duration));
                }

                break;
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if (container != null ){
            return container.size();
        }else{
            return 0;
        }
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView child_item_text;
        TextView mHeaderText;
        TextView mDuration;
        ProgressBar progressBar;
        TextView mProgressText;

        ViewHolder(View itemView) {
            super(itemView);
            mHeaderText = itemView.findViewById(R.id.header_schedule_text);
            child_item_text = itemView.findViewById(R.id.child_item_text);
            progressBar = itemView.findViewById(R.id.child_progress_bar);
            mDuration = itemView.findViewById(R.id.child_item_duration);
            mProgressText = itemView.findViewById(R.id.progress_text);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    ChildTask getItem(int id) {
        if (container!=null){
            return container.get(id);
        }
        return null;
    }


    @Override
    public int getItemViewType(int position) {

        if (isIdentType(position)) return TYPE_ITEM;
            return TYPE_HEADER;
    }

    private boolean isIdentType (int position ){

        if (container!=null) {
            if (!(position == 0) && (container.get(position).getHourToStartTask()) == (container.get(position - 1).getHourToStartTask())) {
                return true;
            }
        }
        return false;
    }




    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
