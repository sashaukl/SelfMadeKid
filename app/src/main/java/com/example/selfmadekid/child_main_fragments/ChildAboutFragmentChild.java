package com.example.selfmadekid.child_main_fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.selfmadekid.ChildMainActivity;
import com.example.selfmadekid.R;
import com.example.selfmadekid.data.ChildContainer;
import com.example.selfmadekid.data.Goal;

import androidx.fragment.app.Fragment;

public class ChildAboutFragmentChild extends Fragment {

    private TextView mGoalText;
    private Goal goal;
    private ProgressBar mProgressBar;
    private TextView mProgressText;

    public ChildAboutFragmentChild() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public static ChildAboutFragmentChild newInstance() {
        //this.context = context;
        return new ChildAboutFragmentChild();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_child_about, container, false);

        mProgressText = view.findViewById(R.id.progress_text);
        mGoalText = view.findViewById(R.id.goal_text);
        mProgressBar = view.findViewById(R.id.child_progress_bar);
        ChildContainer childContainer = ((ChildMainActivity) getActivity()).getChildContainer();
        if (childContainer != null){
            goal = ((ChildMainActivity) getActivity()).getChildContainer().getCurrentGoal();
            if (goal != null){
                mGoalText.setText(goal.getGoalName());
                Drawable drawable = getContext().getDrawable(R.drawable.progress_bar_circle);
                System.out.println(goal.getFinishPoints());
                mProgressBar.setMax(goal.getFinishPoints()); // Maximum Progress
                mProgressBar.setSecondaryProgress(goal.getFinishPoints()); // Secondary Progress
                mProgressBar.setProgressDrawable(drawable);
                mProgressBar.setProgress(goal.getCurrentPoints());
                mProgressText.setText(getString(R.string.goal_progress_bar_as_text, goal.getCurrentPoints(), goal.getFinishPoints()));
            }
        }
        return view;
    }





}
