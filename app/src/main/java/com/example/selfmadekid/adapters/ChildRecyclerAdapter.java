package com.example.selfmadekid.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.selfmadekid.R;
import com.example.selfmadekid.data.AppData;
import com.example.selfmadekid.data.ChildContainer;
import com.example.selfmadekid.data.ChildTask;
import com.example.selfmadekid.data.OneTimeTask;
import com.example.selfmadekid.data.RepetitiveTask;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ChildRecyclerAdapter extends RecyclerView.Adapter<ChildRecyclerAdapter.ViewHolder>  {

    private LayoutInflater mInflater;
    private TaskRecyclerAdapter.ItemClickListener mClickListener;

    private Context context;
    private ArrayList<ChildContainer> container;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    private final int ONE_TIME_TYPE_ITEM = 2;
    private final int ONE_TIME_TYPE_HEADER = 3;


// data is passed into the constructor
    public ChildRecyclerAdapter(Context context, SparseArray<ChildContainer> array) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.container = asList(array);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = mInflater.inflate(R.layout.select_child_item, parent, false);
        return new ChildRecyclerAdapter.ViewHolder(v);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return container.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView child_item_text;

        ViewHolder(View itemView) {
            super(itemView);
            child_item_text = itemView.findViewById(R.id.child_item_text);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    ChildContainer getItem(int id) {
        ArrayList tmp = new ArrayList<Object>();
        return container.get(id);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int type = getItemViewType(position);
        ChildContainer item = getItem(position);
        holder.child_item_text.setText(item.getName()  + " " + item.getPatronymic());


    }


    // allows clicks events to be caught
    void setClickListener(TaskRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public static <C> ArrayList<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        ArrayList<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

}

