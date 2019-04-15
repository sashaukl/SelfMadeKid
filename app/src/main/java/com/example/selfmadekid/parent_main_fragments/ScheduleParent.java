package com.example.selfmadekid.parent_main_fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.selfmadekid.ChildMainActivity;
import com.example.selfmadekid.MainActivity;
import com.example.selfmadekid.R;
import com.example.selfmadekid.adapters.TaskRecyclerAdapter;
import com.example.selfmadekid.data.AppData;
import com.example.selfmadekid.data.ChildContainer;
import com.example.selfmadekid.data.Goal;
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
    private ChildContainer childContainer;
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

        childContainer = AppData.getChildren().get(getSelectedChildID());


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
                Goal goal = null;
                if (childContainer != null){
                    goal = childContainer.getCurrentGoal();
                }
                adapter = new TaskRecyclerAdapter(getContext(), goal, getSelectedChildID(), localDate.getDayOfWeek());
                recyclerView.setAdapter(adapter);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.task_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        Goal goal = null;
        if (childContainer != null){
            goal = childContainer.getCurrentGoal();
        }

        adapter = new TaskRecyclerAdapter(getContext(), goal, getSelectedChildID(), LocalDate.now().getDayOfWeek());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private int getSelectedChildID(){
        if (getActivity() instanceof MainActivity){
            return ((MainActivity) getContext()).getSelectedChildID();
        }else if (getActivity() instanceof ChildMainActivity){
            return ((ChildMainActivity) getContext()).getSelectedChildID();
        }
        return -1;
    }


}
