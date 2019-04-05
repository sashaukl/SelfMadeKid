package com.example.selfmadekid.adapters;


import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.selfmadekid.R;
import com.example.selfmadekid.data.AppData;
import com.example.selfmadekid.data.ChildTask;
import com.example.selfmadekid.data.OneTimeTask;
import com.example.selfmadekid.data.RepetitiveTask;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;


public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder> {


    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private Context context;
    private ArrayList<RepetitiveTask> container;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    private final int ONE_TIME_TYPE_ITEM = 2;
    private final int ONE_TIME_TYPE_HEADER = 3;
    private int selectedChildId;

    private int repetitiveSize;
    private int oneTimeSize;

    private int dayOfTheWeek;



    // data is passed into the constructor
    public TaskRecyclerAdapter(Context context, DayOfWeek dayOfTheWeek, ArrayList<RepetitiveTask> arrayList, int selectedChildID) {
        this.context = context;
        this.selectedChildId = selectedChildID;
        this.dayOfTheWeek = dayOfTheWeek.getValue();
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
            case ONE_TIME_TYPE_HEADER:
                v = mInflater.inflate(R.layout.header_one_time_task, parent, false);
                break;
            case ONE_TIME_TYPE_ITEM:
                v = mInflater.inflate(R.layout.one_time_task_item, parent, false);
                break;
            case TYPE_ITEM:
            default:
                v = mInflater.inflate(R.layout.child_item, parent, false);
                break;
        }
        return new ViewHolder(v);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int type = getItemViewType(position);
        System.out.println(holder.mHeaderText);
        ChildTask childTask;
        switch (type) {
            case TYPE_HEADER:
                //TimeHolder timeHolder = container.get(position).getTimeHolders(dayOfTheWeek);
                String str = container.get(position).getStartHour() + ":00";
                holder.mHeaderText.setText(str) ;
            case TYPE_ITEM:
                childTask = getItem(position);
                holder.child_item_text.setText(childTask.getTaskText());
                Drawable drawable = context.getDrawable(R.drawable.progress_bar_circle);
                holder.progressBar.setMax(childTask.getNecessaryProgress()); // Maximum Progress
                holder.progressBar.setSecondaryProgress(childTask.getNecessaryProgress()); // Secondary Progress
                holder.progressBar.setProgressDrawable(drawable);
                holder.progressBar.setProgress(getItem(position).getCurrentProgress());
                holder.mProgressText.setText(childTask.getCurrentProgress() + "/" + childTask.getNecessaryProgress());
                break;
            case ONE_TIME_TYPE_HEADER:
            case ONE_TIME_TYPE_ITEM:
                childTask = getItem(position);
                holder.child_item_text.setText(childTask.getTaskText());
                LocalDate date = ((OneTimeTask) getItem(position)).getDeadlineDate();
                holder.mUntilDate.setText(context.getString(R.string.do_until_date, date.getDayOfMonth(),date.getMonthValue(),date.getYear() ));
                break;
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        int size = 0;
        if (container != null ){
            repetitiveSize = container.size();
            size += repetitiveSize;
        }else{
            repetitiveSize = 0;
        }
        if (AppData.getChildren().get(selectedChildId)!= null){
            oneTimeSize = AppData.getChildren().get(selectedChildId).getOneTimeTaskContainer().size();
            size += oneTimeSize;
        }else {
            oneTimeSize = 0;
        }
        return size;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView child_item_text;
        TextView mHeaderText;
        TextView mUntilDate;
        ProgressBar progressBar;
        TextView mProgressText;

        ViewHolder(View itemView) {
            super(itemView);
            mHeaderText = itemView.findViewById(R.id.header_schedule_text);
            child_item_text = itemView.findViewById(R.id.child_item_text);
            progressBar = itemView.findViewById(R.id.child_progress_bar);
            mUntilDate = itemView.findViewById(R.id.child_item_until_date);
            mProgressText = itemView.findViewById(R.id.progress_text);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    ChildTask getItem(int id) {
        if (id<repetitiveSize)
        {
            if (container!=null){
                return container.get(id);
            }
        }
        else {
            return AppData.getChildren().get(selectedChildId).getOneTimeTaskContainer().get(id-repetitiveSize);
        }

        return null;
    }


    @Override
    public int getItemViewType(int position) {
        if (position < repetitiveSize) {
            if (container!=null) {
                if (!(position == 0) && (container.get(position).getStartHour()) == container.get(position - 1).getStartHour()) {
                    return TYPE_ITEM;
                }else{
                    return TYPE_HEADER;
                }
            }
        }
        if (position == repetitiveSize){
            return ONE_TIME_TYPE_HEADER;
        }

        return ONE_TIME_TYPE_ITEM;
    }

    private boolean isIdentType (int position ){

        if (container!=null) {
            //TimeHolder timeHolder = container.get(position).getTimeHolders(dayOfTheWeek);
            //TimeHolder prevTime = container.get(position).getTimeHolders(dayOfTheWeek);
            //if (timeHolder != null){
                if (!(position == 0) && (container.get(position).getStartHour()) == container.get(position - 1).getStartHour()) {
                    return true;
                }
            //}
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
