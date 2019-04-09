package com.example.selfmadekid.main_fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.selfmadekid.AddNewTaskActivity;
import com.example.selfmadekid.LoginActivity;
import com.example.selfmadekid.MainActivity;
import com.example.selfmadekid.R;
import com.example.selfmadekid.adapters.TaskRecyclerAdapter;
import com.example.selfmadekid.data.ChildContainer;
import com.example.selfmadekid.data.Goal;
import com.example.selfmadekid.data.RepetitiveTask;
import com.example.selfmadekid.data.RepetitiveTasksList;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;


import org.threeten.bp.LocalDate;

import java.util.Calendar;


public class ScheduleParent extends Fragment {

    private RecyclerView recyclerView;
    private TaskRecyclerAdapter adapter;

    private MaterialCalendarView mCalendarView;

    public ScheduleParent() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public static ScheduleParent newInstance() {
        //this.context = context;
        return new ScheduleParent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_schedudle_parent, container, false);
        mCalendarView = view.findViewById(R.id.calendarView);

        mCalendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        final Calendar calendar = Calendar.getInstance();


        Calendar now = Calendar.getInstance();

        //only way why its works :(
        CalendarDay today = CalendarDay.today();
        int year = today.getYear();
        int month = today.getMonth();
        int day = today.getDay();

        mCalendarView.setCurrentDate(CalendarDay.from(year, month, day) , true);
        mCalendarView.setSelectedDate(LocalDate.now());
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
                //todo fine listener
                LocalDate localDate = calendarDay.getDate();
                ChildContainer childContainer = ((MainActivity) getContext()).getChildContainer();
                Goal goal = null;
                if (childContainer != null){
                    goal = childContainer.getCurrentGoal();
                }
                adapter = new TaskRecyclerAdapter(getContext(), goal, ((MainActivity) getContext()).getSelectedChildID(), localDate.getDayOfWeek());
                recyclerView.setAdapter(adapter);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.task_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        ChildContainer childContainer = ((MainActivity) getContext()).getChildContainer();
        Goal goal = null;
        if (childContainer != null){
            goal = childContainer.getCurrentGoal();
        }

                adapter = new TaskRecyclerAdapter(getContext(), goal, ((MainActivity) getContext()).getSelectedChildID(), LocalDate.now().getDayOfWeek());
        recyclerView.setAdapter(adapter);


        return view;
    }

}
