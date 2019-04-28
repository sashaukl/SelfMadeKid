package com.example.selfmadekid.main_fragments;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfmadekid.R;
import com.example.selfmadekid.SelectRoleActivity;
import com.example.selfmadekid.adapters.ChildRecyclerAdapter;
import com.example.selfmadekid.data.AppData;
import com.example.selfmadekid.services.ReceiveChildDataService;

public class ChildSelect  extends Fragment {





    private RecyclerView recyclerView;
    private ChildRecyclerAdapter adapter;
    private ImageButton mExitButton;
    private ImageButton mUpdateButton;

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


        mExitButton = view.findViewById(R.id.exit_button);
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppData.clearData(getContext());
                Intent intent = new Intent(getContext(), SelectRoleActivity.class);
                getContext().startActivity(intent);
                getActivity().finish();
            }
        });

        mUpdateButton = view.findViewById(R.id.update_button);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Animatable) mUpdateButton.getDrawable()).start();
                Intent intent = new Intent(getContext(), ReceiveChildDataService.class);
                intent.putExtra("role", AppData.getCurrentState());
                AppData.setCurrentUserID(AppData.getCurrentUserID());
                intent.putExtra("userID", AppData.getCurrentUserID());
                getActivity().startService(intent);
            }
        });

        return view;
    }




}
