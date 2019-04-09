package com.example.selfmadekid.main_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.selfmadekid.MainActivity;
import com.example.selfmadekid.R;
import com.example.selfmadekid.adapters.ChildRecyclerAdapter;
import com.example.selfmadekid.adapters.TaskRecyclerAdapter;
import com.example.selfmadekid.data.AppData;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import org.threeten.bp.LocalDate;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChildSelect  extends Fragment {





    private RecyclerView recyclerView;
    private ChildRecyclerAdapter adapter;

    private MaterialCalendarView mCalendarView;

    public ChildSelect() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public static ChildSelect newInstance() {
        return new ChildSelect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_child_select, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.task_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ChildRecyclerAdapter(getContext(), AppData.getChildren() );
        recyclerView.setAdapter(adapter);


        return view;
    }
}
