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

import com.google.android.material.checkbox.MaterialCheckBox;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.DateFormatDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);

        mTimePicker = findViewById(R.id.time_picker);
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
            if(((MaterialCheckBox) mainView).isChecked()){
                mTimePicker.setVisibility(View.VISIBLE);
                if (mCurrentTextView != null){
                    mCurrentTextView.setText(getString(R.string.display_time, mTimePicker.getHour(), mTimePicker.getMinute()));
                }
            }
            else{
                if (mCurrentTextView != null){
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
                        ((TextView) mPointsHeader.findViewById(R.id.selected_points)).setText(editDuration.getText().toString());
                        dialog.dismiss();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


}
