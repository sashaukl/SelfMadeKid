package com.example.selfmadekid.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.selfmadekid.MainActivity;
import com.example.selfmadekid.R;
import com.example.selfmadekid.data.ChildContainer;

import java.util.ArrayList;

public class ChildRecyclerAdapter extends RecyclerView.Adapter<ChildRecyclerAdapter.ViewHolder>  {

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private Context context;
    private ArrayList<ChildContainer> container;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    private final int ONE_TIME_TYPE_ITEM = 2;
    private final int ONE_TIME_TYPE_HEADER = 3;
    private View selectView;
    private TextView selectedText;


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
        Integer childID;
        View mView;

        ViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            child_item_text = itemView.findViewById(R.id.child_item_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (childID != null){
                        ((MainActivity) context).setSelectedChildID(childID);
                        changeSelectedChild(v);
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
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
        holder.childID = item.getId();
        if ( holder.childID == ((MainActivity) context).getSelectedChildID() ){
           changeSelectedChild(holder.mView);
        }

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

    public static <C> ArrayList<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        ArrayList<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

}

