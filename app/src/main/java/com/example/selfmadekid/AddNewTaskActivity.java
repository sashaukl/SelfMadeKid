package com.example.selfmadekid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.selfmadekid.data.AppData;
import com.example.selfmadekid.data.ChildContainer;
import com.example.selfmadekid.data.Goal;
import com.example.selfmadekid.data.OneTimeTask;
import com.example.selfmadekid.data.OneTimeTaskList;
import com.example.selfmadekid.data.RepetitiveTask;
import com.example.selfmadekid.data.RepetitiveTasksList;
import com.example.selfmadekid.data.TimeHolder;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddNewTaskActivity extends AppCompatActivity {

    private ExpandableListView mExpandableListView;
    private int lastPosition = -1;
    //private AddNewTaskExpandableAdapter expandableListAdapter;
    private MaterialCalendarView mCalendarView;
    private TimePicker mTimePicker;
    private TimePicker oneTimeTimePicker;

    private final int ONE_TIME_TASK_MODE = 0;
    private final int REPETITIVE_TASK_MODE = 1;
    private int current_mode = 0;
    private MaterialCheckBox currentCheckbox;
    private TextView mCurrentTextView;
    private View mSelectDayLayout;
    private View mTimeHeader;
    private View mDateHeader;
    private View mPointsHeader;
    private AlertDialog dialog;

    private TextView mNameField;
    private Context context;
    //data fields

    //common fields

    private Integer selectedChildID;

    private String task_name;
    private int max_points = -1;

    //one_time_task
    private int deadline_hour = -1;
    private int deadline_minute = -1;
    private LocalDate deadline_date;
    private final int WEEK_LENGTH = 7;

    private Goal goal;
    //tasks
    private TimeHolder[] week_days = new TimeHolder[WEEK_LENGTH];
    private RepetitiveTasksList repetitiveTasks;
    private OneTimeTaskList oneTimeTasks;

    private AddOneTimeTask addOneTimeTask = null;
    private int repetitive_taskID = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);
        this.context = this;
        selectedChildID = getIntent().getIntExtra("id", -1);
        System.out.println("ID ===" + selectedChildID);
        mNameField = ((TextView) findViewById(R.id.task_name));

        if (selectedChildID!= -1){
            if (AppData.getChildren().get(selectedChildID) != null) {

                goal = AppData.getChildren().get(selectedChildID).getCurrentGoal();
                System.out.println("GOAL ID ===" + goal.getGoal_id());
                if (goal != null) {
                    System.out.println("=====================" + goal.getGoalName());
                    oneTimeTasks = goal.getOneTimeTaskContainer();
                } else {
                    //todo error text
                    errorMessage();
                }
            }
        }else{
            //todo error text
            errorMessage();
        }
        mTimePicker = findViewById(R.id.time_picker_for_repetitive);
        if (mTimePicker !=null){
            mTimePicker.setIs24HourView(true);
            mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    if (current_mode == REPETITIVE_TASK_MODE){
                        if (mCurrentTextView != null){
                            mCurrentTextView.setText(getString(R.string.display_time, hourOfDay, minute));
                        }
                    }
                }
            });
        }
        oneTimeTimePicker = findViewById(R.id.time_picker_one_time_task);
        oneTimeTimePicker.setIs24HourView(true);
        oneTimeTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                ((TextView) mTimeHeader.findViewById(R.id.selected_time)).setText(
                        getString(R.string.display_time, hourOfDay, minute)
                );
                deadline_hour = hourOfDay;
                deadline_minute = minute;
            }
        });
        mSelectDayLayout = findViewById(R.id.select_day_layout);
        mCalendarView = findViewById(R.id.calendar_view);
        mTimeHeader = findViewById(R.id.header1);
        mDateHeader = findViewById(R.id.header2);
        mPointsHeader = findViewById(R.id.header3);
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
                ((TextView) mDateHeader.findViewById(R.id.selected_date)).setText(
                        getString(R.string.display_date, calendarDay.getDay(), calendarDay.getMonth(), calendarDay.getYear() )
                );
                deadline_date = calendarDay.getDate();
            }
        });



    }



    public void onOneTimeButtonClicked(View view){

            mSelectDayLayout.setVisibility(View.GONE);
            mCalendarView.setVisibility(View.GONE);

            mTimeHeader.setVisibility(View.VISIBLE);
            mDateHeader.setVisibility(View.VISIBLE);
            //mPointsHeader.setVisibility(View.VISIBLE);

            current_mode = ONE_TIME_TASK_MODE;

    }

    public void onRepetitiveButtonClicked(View view){
        if (mCalendarView != null){
            mSelectDayLayout.setVisibility(View.VISIBLE);
            mTimePicker.setVisibility(View.GONE);
            mCalendarView.setVisibility(View.GONE);
            mTimeHeader.setVisibility(View.GONE);
            mDateHeader.setVisibility(View.GONE);
            //mPointsHeader.setVisibility(View.GONE);
            current_mode = REPETITIVE_TASK_MODE;
        }
    }

    public void onDayOfWeekClicked(View mainView) {
        if(mTimePicker != null){
            mCurrentTextView = findViewById(getResources().getIdentifier("text_for_checkbox" + ((MaterialCheckBox) mainView).getTag().toString(), "id", getPackageName()));
            int dayInInteger  = Integer.valueOf(((MaterialCheckBox) mainView).getTag().toString())-1;
            if(((MaterialCheckBox) mainView).isChecked()){
                mTimePicker.setVisibility(View.VISIBLE);
                if (mCurrentTextView != null){
                    week_days[dayInInteger] = new TimeHolder(mTimePicker.getHour(),mTimePicker.getMinute());
                    mCurrentTextView.setText(getString(R.string.display_time, mTimePicker.getHour(), mTimePicker.getMinute()));
                }
            }
            else{
                if (mCurrentTextView != null){
                    week_days[dayInInteger] = null;
                    mCurrentTextView.setText(""); //disable text
                    mCurrentTextView = null;
                }
                mTimePicker.setVisibility(View.GONE);
            }
        }
    }


    public void onTimeHeaderClicked(View view){

        if(oneTimeTimePicker.getVisibility()==View.VISIBLE){
            oneTimeTimePicker.setVisibility(View.GONE);
        }else{
            oneTimeTimePicker.setVisibility(View.VISIBLE);
        }
       // mTimePicker.setVisibility(View.VISIBLE);
    }

    public void onDateHeaderClicked(View view){

        if(mCalendarView.getVisibility()==View.VISIBLE){
            mCalendarView.setVisibility(View.GONE);
        }else{
            mCalendarView.setVisibility(View.VISIBLE);
        }
        // mTimePicker.setVisibility(View.VISIBLE);
    }


    public void buildAlertDialog(View view) {
        dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(getString(R.string.point_count_to_end_task))
                .setView(getLayoutInflater().inflate(R.layout.dialog_get_duration, null))
                .setPositiveButton(getString(R.string.ok), null)
                .setNegativeButton(getString(R.string.abort), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
        Button bt = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        bt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText editDuration = (EditText) ((Dialog)
                            dialog).findViewById(R.id.points_edit_text);

                    if (editDuration.getText().toString().equals("")) {
                        editDuration.setError(getString(R.string.error_field_required));
                    }else{
                        String str = editDuration.getText().toString();
                        max_points = Integer.valueOf(str);
                        ((TextView) mPointsHeader.findViewById(R.id.selected_points)).setText(str);
                        dialog.dismiss();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    public void onAddButtonClicked(View view){
        task_name = ((TextView) findViewById(R.id.task_name)).getText().toString();
        if (mNameField.getText().toString().isEmpty()){
            mNameField.setError(getString(R.string.error_field_required));
            return;
        }
        if (current_mode == REPETITIVE_TASK_MODE){

            if (max_points == -1){
                Toast.makeText(this, R.string.enter_points_amount, Toast.LENGTH_SHORT).show();
                return;
            }
            get_repetitive_id();


        }else if(current_mode == ONE_TIME_TASK_MODE){
            if (deadline_hour == -1 || deadline_date == null){
                Toast.makeText(this, R.string.enter_deadline_time_and_date, Toast.LENGTH_SHORT).show();
                return;
            }
            if (max_points == -1){
                Toast.makeText(this, R.string.enter_points_amount, Toast.LENGTH_SHORT).show();
                return;
            }
            Calendar calendar = Calendar.getInstance();
            long timeNow = calendar.get(Calendar.HOUR_OF_DAY) * 3600000 + calendar.get(Calendar.MINUTE)*60000;
            long timeSetted = deadline_hour * 3600000 +deadline_minute*60000;
            if (deadline_date.isBefore(LocalDate.now()) ||
                    ( deadline_date.isEqual(LocalDate.now()) && timeNow>=timeSetted )) {
                    Toast.makeText(this, R.string.enter_date_after_now, Toast.LENGTH_SHORT).show();
                return;
            }

            //todo -- get task id from server
            if(addOneTimeTask == null){
                addOneTimeTask = new AddOneTimeTask(task_name, max_points, deadline_date.getYear(), deadline_date.getMonthValue(),deadline_date.getDayOfMonth(),
                        deadline_hour, deadline_minute);
                addOneTimeTask.execute();

            }
            if (oneTimeTasks != null){
                finish();
            }

        }
    }

    private void errorMessage(){
        //todo -- error if null
        Toast.makeText(this, R.string.error_field_required, Toast.LENGTH_SHORT).show();
    }




    public class AddOneTimeTask extends AsyncTask<Void, Void, Boolean> {

        private int id;
        private String name;
        private int points;
        private int year;
        private int month;
        private int day;
        private int hour;
        private int minute;

        public AddOneTimeTask(String name, int points, int year, int month, int day, int hour, int minute) {
            this.name = name;
            this.points = points;
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (goal != null) {

                try {
                    StringRequest stringRequest;
                    stringRequest = new StringRequest(Request.Method.POST,
                            getString(R.string.add_one_time_task),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {

                                        System.out.println("one time add "+ response);
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.getString("error").isEmpty()){
                                            id = jsonObject.getInt("id");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            System.out.println("GOALSLDLSDLSLDLDLS "+goal.getGoal_id());
                            params.put("goal_id", Integer.valueOf(goal.getGoal_id()).toString());
                            params.put("name", name);
                            params.put("value", Integer.valueOf(points).toString());
                            params.put("year_end", Integer.valueOf(year).toString());
                            params.put("month_end", Integer.valueOf(month).toString());
                            params.put("day_end", Integer.valueOf(day).toString());
                            params.put("hour_end", Integer.valueOf(hour).toString());
                            params.put("minute_end", Integer.valueOf(minute).toString());
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                    requestQueue.add(stringRequest);
                } catch (Exception e) {
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (oneTimeTasks!=null){
                System.out.println("HETE");
                oneTimeTasks.add(new OneTimeTask(
                        id,
                        task_name,
                        deadline_date,
                        deadline_hour,
                        deadline_minute,
                        max_points
                ));
                addOneTimeTask = null;
            }
        }

        @Override
        protected void onCancelled() {
            addOneTimeTask = null;
        }
    }


    public class AddRepetitiveTask extends AsyncTask<Void, Void, Boolean> {
        private int id ;
        private String name;
        private int points;
        private TimeHolder timeHolder;
        //private int iteration = 0;
        private boolean isTyped = false;

        public AddRepetitiveTask(String name, int points, int id) {
            this.name = name;
            this.points = points;
            this.id = id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            System.out.println("doInBackground");
            if (goal != null) {
                try {
                    for (int iteration=0; iteration<WEEK_LENGTH; iteration++){
                        final int i = iteration;
                        timeHolder = week_days[i];
                        if (timeHolder!=null){
                            StringRequest stringRequest;
                            stringRequest = new StringRequest(Request.Method.POST,
                                    getString(R.string.add_repetitive_task),
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                System.out.println("AddRepetitiveTask"+ response);
                                                JSONObject jsonObject = new JSONObject(response);
                                                if (jsonObject.getString("error").isEmpty()){
                                                    id = jsonObject.getInt("id");
                                                    goal.getDayOfTheWeekContainer(DayOfWeek.of(i+1)).add(new RepetitiveTask(
                                                            id,
                                                            name,
                                                            points,
                                                            week_days[i].getHour(),
                                                            week_days[i].getMinute()

                                                    ));

                                                    isTyped = true;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    if (i  < 7){
                                        params.put("goal_id", Integer.valueOf(goal.getGoal_id()).toString());
                                        params.put("name", name);
                                        params.put("value", Integer.valueOf(points).toString());
                                        params.put("day_of_the_week", Integer.valueOf(i+1).toString());
                                        params.put("hour", Integer.valueOf(week_days[i].getHour()).toString());
                                        params.put("minute", Integer.valueOf(week_days[i].getMinute()).toString());
                                        params.put("task_id", Integer.valueOf(id).toString());
                                    }

                                    return params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(context);
                            requestQueue.add(stringRequest);


                            isTyped = true;
                        }
                    }

                } catch (Exception e) {
                }
            }
            return isTyped;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (isTyped){
                ((AddNewTaskActivity) context).finish();
            }else{
                Toast.makeText(context, R.string.choose_at_least_one_day, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            addOneTimeTask = null;
        }


    }

    private void get_repetitive_id(){
        System.out.println("get_repetitive_id");
        StringRequest stringRequest;
        stringRequest = new StringRequest(Request.Method.POST,
                getString(R.string.get_repetitive_id),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("get_repetitive_id"+ response);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("error").isEmpty()){
                                int id = jsonObject.getInt("id");
                                new AddRepetitiveTask(task_name, max_points, id).execute();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("goal_id", Integer.valueOf(goal.getGoal_id()).toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }




}
