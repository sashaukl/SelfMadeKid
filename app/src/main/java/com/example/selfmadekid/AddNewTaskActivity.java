package com.example.selfmadekid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.selfmadekid.data.AppData;
import com.example.selfmadekid.data.OneTimeTask;
import com.example.selfmadekid.data.RepetitiveTask;
import com.example.selfmadekid.data.TimeHolder;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

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

    //repetitive_task
    private TimeHolder[] week_days = new TimeHolder[WEEK_LENGTH];




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);

        selectedChildID = getIntent().getIntExtra("id", 0);

        mNameField = ((TextView) findViewById(R.id.task_name));

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
            //todo -- connect to server and get transaction id
            int id = 3;

            if (max_points == -1){
                Toast.makeText(this, R.string.enter_points_amount, Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isTyped = false;
            TimeHolder timeHolder;
            for (int i=0; i<WEEK_LENGTH; i++){
                timeHolder = week_days[i];
                if (timeHolder!=null){
                    //todo -- get task id from server

                    AppData.getChildren().get(selectedChildID).getDayOfTheWeekContainer(DayOfWeek.of(i+1)).add(new RepetitiveTask(
                            //generated task id
                            2,
                            task_name,
                            max_points,
                            timeHolder.getHour(),
                            timeHolder.getHour()

                    ));
                    isTyped = true;
                }
            }
            if (!isTyped){
                Toast.makeText(this, R.string.choose_at_least_one_day, Toast.LENGTH_LONG).show();
            }else {
                finish();
            }

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
                System.out.println(timeNow + "  " + timeSetted);
                    Toast.makeText(this, R.string.enter_date_after_now, Toast.LENGTH_SHORT).show();
                return;
            }

            //todo -- get task id from server
            int id = 3;
            AppData.getChildren().get(selectedChildID).getOneTimeTaskContainer().add(new OneTimeTask(
                    id,
                    task_name,
                    deadline_date,
                    deadline_hour,
                    deadline_minute
            ));
            finish();
        }
    }

}
