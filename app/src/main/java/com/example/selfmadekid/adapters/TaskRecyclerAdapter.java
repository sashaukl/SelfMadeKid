package com.example.selfmadekid.adapters;


import android.content.Context;
import android.graphics.drawable.Animatable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.selfmadekid.R;
import com.example.selfmadekid.data.AppData;
import com.example.selfmadekid.data.ChildTask;
import com.example.selfmadekid.data.Goal;
import com.example.selfmadekid.data.OneTimeTask;
import com.example.selfmadekid.data.OneTimeTaskList;
import com.example.selfmadekid.data.RepetitiveTasksList;

import org.json.JSONObject;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.HashMap;
import java.util.Map;


public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private RepetitiveTasksList repetitiveTasks;
    private OneTimeTaskList oneTimeTasks;
    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;
    private final int ONE_TIME_TYPE_ITEM = 2;
    private final int ONE_TIME_TYPE_HEADER = 3;
    private int selectedChildId;
    private int repetitiveSize = 0;
    private int oneTimeSize = 0;
    private DayOfWeek dayOfTheWeek;
    private LocalDate localDate;
    private Goal goal;
    private int notHidedOneTimeTask = 0;
    private View oneTimeTaskHeader;

    // data is passed into the constructor
    public TaskRecyclerAdapter(Context context, Goal goal, int selectedChildID, LocalDate localDate  ) {
        this.context = context;
        this.selectedChildId = selectedChildID;
        this.mInflater = LayoutInflater.from(context);
        this.dayOfTheWeek = localDate.getDayOfWeek();
        this.localDate = localDate;
        this.goal = goal;
        if (goal != null){
            oneTimeTasks = goal.getOneTimeTaskContainer();
            repetitiveTasks =  goal.getDayOfTheWeekContainer(dayOfTheWeek);
        }
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

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        int type = getItemViewType(position);
        System.out.println(holder.mHeaderText);
        final ChildTask childTask = getItem(position);
        switch (type) {
            case TYPE_HEADER:
                String str = repetitiveTasks.get(position).getStartHour() + ":00";
                holder.mHeaderText.setText(str);
            case TYPE_ITEM:
                holder.child_item_text.setText(childTask.getTaskText());
                Integer state = 0;
                if (goal != null){
                    HashMap<LocalDate, Integer> hashMap = goal.getCheckedDates().get(childTask.getTask_id());
                    if (hashMap != null){
                        state = hashMap.get(localDate);
                    }
                }
                if (state == null){
                    state = 0;
                }
                if (AppData.getCurrentState() == AppData.PARENT){
                    switch ( state ){
                        case 0:
                            holder.mImgCheck.setVisibility(View.GONE);
                            break;
                        case 1:
                            if (holder.mImgCheck!=null) {
                                holder.mImgCheck.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((Animatable) ((ImageButton) v).getDrawable()).start();
                                        new CheckRepetitiveTask(AppData.getCurrentState(), childTask.getTask_id(), localDate).execute();
                                    }
                                });
                            }
                            break;
                        case 2:
                            holder.mImgCheck.setImageDrawable(context.getDrawable(R.drawable.ic_check_confirmed));
                            break;
                    }

                }else if (AppData.getCurrentState() == AppData.CHILD){
                    switch ( state ){
                        case 0:
                            if (holder.mImgCheck!=null) {
                                holder.mImgCheck.setColorFilter(ContextCompat.getColor(context, R.color.silverColor), android.graphics.PorterDuff.Mode.SRC_IN);
                                holder.mImgCheck.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((Animatable) ((ImageButton) v).getDrawable()).start();
                                        new CheckRepetitiveTask(AppData.getCurrentState(), childTask.getTask_id(), localDate).execute();
                                    }
                                });
                            }
                            break;
                        case 1:
                            holder.mImgCheck.setColorFilter(ContextCompat.getColor(context, R.color.silverColor), android.graphics.PorterDuff.Mode.SRC_IN);
                            holder.mImgCheck.setImageDrawable(context.getDrawable(R.drawable.ic_check_confirmed));
                            break;
                        case 2:
                            holder.mImgCheck.setImageDrawable(context.getDrawable(R.drawable.ic_check_confirmed));
                            break;
                    }
                }

                //holder.progressBar.setProgress(getItem(position).getCurrentProgress());
                //holder.mProgressText.setText(childTask.getCurrentProgress() + "/" + childTask.getFinishReward());
                break;
            case ONE_TIME_TYPE_HEADER:
                oneTimeTaskHeader = holder.mOneTimeTaskHeader;
            case ONE_TIME_TYPE_ITEM:
                if ( localDate.isAfter(((OneTimeTask) getItem(position)).getDeadlineDate() )){
                    notHidedOneTimeTask --;
                    if (notHidedOneTimeTask == 0){
                        hideOneTimeTaskHeader();
                    }
                    holder.child_layout.setVisibility(View.GONE);
                    holder.child_layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    break;
                }
                holder.child_item_text.setText(childTask.getTaskText());
                LocalDate date = ((OneTimeTask) getItem(position)).getDeadlineDate();
                holder.mUntilDate.setText(context.getString(R.string.do_until_date, date.getDayOfMonth(),date.getMonthValue(),date.getYear() ));
                System.out.println ( "getConfirmed" + ((OneTimeTask) childTask).getConfirmed() );
                if (AppData.getCurrentState() == AppData.PARENT){
                    switch ( ((OneTimeTask) childTask).getConfirmed() ){
                        case 0:
                            holder.mImgCheck.setVisibility(View.GONE);
                            break;
                        case 1:
                            if (holder.mImgCheck!=null) {
                                holder.mImgCheck.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((Animatable) ((ImageButton) v).getDrawable()).start();
                                        new CheckOneTimeTask(AppData.getCurrentState(), childTask.getTask_id(), ((OneTimeTask) childTask)).execute();
                                    }
                                });
                            }
                            break;
                        case 2:
                            holder.mImgCheck.setImageDrawable(context.getDrawable(R.drawable.ic_check_confirmed));
                            break;
                    }

                }else if (AppData.getCurrentState() == AppData.CHILD){
                    switch ( ((OneTimeTask) childTask).getConfirmed() ){
                        case 0:
                            if (holder.mImgCheck!=null) {
                                holder.mImgCheck.setColorFilter(ContextCompat.getColor(context, R.color.silverColor), android.graphics.PorterDuff.Mode.SRC_IN);
                                holder.mImgCheck.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((Animatable) ((ImageButton) v).getDrawable()).start();
                                        new CheckOneTimeTask(AppData.getCurrentState(), childTask.getTask_id(), ((OneTimeTask) childTask)).execute();
                                    }
                                });
                            }
                            break;
                        case 1:
                            holder.mImgCheck.setColorFilter(ContextCompat.getColor(context, R.color.silverColor), android.graphics.PorterDuff.Mode.SRC_IN);
                            holder.mImgCheck.setImageDrawable(context.getDrawable(R.drawable.ic_check_confirmed));
                            break;
                        case 2:
                            holder.mImgCheck.setImageDrawable(context.getDrawable(R.drawable.ic_check_confirmed));
                            break;
                    }
                }
        }
    }
    private void hideOneTimeTaskHeader(){
        if (oneTimeTaskHeader!=null){
            oneTimeTaskHeader.setVisibility(View.GONE);
            oneTimeTaskHeader.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        int size = 0;
        if (repetitiveTasks != null ){
            repetitiveSize = repetitiveTasks.size();
            size += repetitiveSize;
        }else{
            repetitiveSize = 0;
        }
        if (oneTimeTasks != null){
            if (notHidedOneTimeTask == 0){
                notHidedOneTimeTask = oneTimeTasks.size();
            }
            oneTimeSize = oneTimeTasks.size();
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
        ImageView mImgCheck;
        View child_layout;
        View mOneTimeTaskHeader;

        ViewHolder(View itemView) {
            super(itemView);
            child_layout = itemView.findViewById(R.id.child_layout);
            mHeaderText = itemView.findViewById(R.id.header_schedule_text);
            child_item_text = itemView.findViewById(R.id.child_item_text);
            progressBar = itemView.findViewById(R.id.child_progress_bar);
            mUntilDate = itemView.findViewById(R.id.child_item_until_date);
            mProgressText = itemView.findViewById(R.id.progress_text);
            mOneTimeTaskHeader = itemView.findViewById(R.id.one_time_task_header_schedule_text);
            mImgCheck = itemView.findViewById(R.id.goal_check);
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
            if (repetitiveTasks !=null){
                return repetitiveTasks.get(id);
            }
        }
        else {
            if (oneTimeTasks != null){
                return oneTimeTasks.get(id-repetitiveSize);
            }

        }

        return null;
    }


    @Override
    public int getItemViewType(int position) {
        if (position < repetitiveSize) {
            if (repetitiveTasks !=null) {
                if (!(position == 0) && (repetitiveTasks.get(position).getStartHour()) == repetitiveTasks.get(position - 1).getStartHour()) {
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



    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    public class CheckOneTimeTask extends AsyncTask<Void, Void, Boolean> {

        int state;
        int task_id;
        OneTimeTask task;

        public CheckOneTimeTask(int state, int task_id, OneTimeTask task) {
            this.state = state;
            this.task_id = task_id;
            this.task = task;
            System.out.println("state " + state + "task_id " + task_id + "user " + AppData.getCurrentUserID() );
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                StringRequest stringRequest;
                stringRequest = new StringRequest(Request.Method.POST,
                        context.getString(R.string.check_one_time_task),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.get("error").equals("")){
                                        onPostExecute(true);
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("state", Integer.valueOf(state).toString());
                        params.put("task_id", Integer.valueOf(task_id).toString());
                        params.put("user_id", Integer.valueOf(AppData.getCurrentUserID()).toString());
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            }catch (Exception e) {
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            task.setConfirmed(state);

        }

        @Override
        protected void onCancelled() {

        }
    }

    public class CheckRepetitiveTask extends AsyncTask<Void, Void, Boolean> {

        int state;
        int task_id;
        LocalDate localDate;

        public CheckRepetitiveTask(int state, int task_id, LocalDate localDate) {
            this.state = state;
            this.task_id = task_id;
            this.localDate = localDate;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                StringRequest stringRequest;
                stringRequest = new StringRequest(Request.Method.POST,
                        context.getString(R.string.check_repetitive_task),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println(response);
                                try {
                                    onPostExecute(true);
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("state", Integer.valueOf(state).toString());
                        params.put("task_id", Integer.valueOf(task_id).toString());
                        params.put("year", Integer.valueOf(localDate.getYear()).toString());
                        params.put("month", Integer.valueOf(localDate.getMonthValue()).toString());
                        params.put("day", Integer.valueOf(localDate.getDayOfMonth()).toString());
                        params.put("user_id", Integer.valueOf(AppData.getCurrentUserID()).toString());
                        System.out.println("state " + state + " task_id " + task_id + " user_id " + AppData.getCurrentUserID() + " month " + localDate.getDayOfMonth() + " day " + localDate.getDayOfMonth());
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            }catch (Exception e) {
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success){
                AppData.getChildren().get(selectedChildId).getCurrentGoal().getCheckedDates().get(task_id).put(localDate,
                        state);
            }
        }

        @Override
        protected void onCancelled() {

        }
    }



}
