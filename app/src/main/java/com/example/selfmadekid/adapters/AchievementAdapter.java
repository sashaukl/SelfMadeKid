package com.example.selfmadekid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.selfmadekid.R;

import java.util.ArrayList;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder>  {

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private Context context;
    private ArrayList<String> container;
    private View selectView;



    // data is passed into the constructor
    public AchievementAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.container = list;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.achievements_item, parent, false);
        return new ViewHolder(v);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return container.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView child_item_text;
        View mView;

        ViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            child_item_text = itemView.findViewById(R.id.child_item_text);
        }

        @Override
        public void onClick(View view) {
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return container.get(id);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int type = getItemViewType(position);
        holder.child_item_text.setText(getItem(position));


    }

    private void changeSelectedChild(View newChild){
        if (selectView != null){
            ((TextView) selectView.findViewById(R.id.child_item_text)).setTextColor(context.getColor(R.color.textPrimaryColor));
        }
        selectView = newChild;
        ((TextView) selectView.findViewById(R.id.child_item_text)).setTextColor(context.getColor(R.color.colorAccent));
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

