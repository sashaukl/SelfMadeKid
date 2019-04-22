package com.example.selfmadekid.parent_main_fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.selfmadekid.ChildMainActivity;
import com.example.selfmadekid.MainActivity;
import com.example.selfmadekid.R;
import com.example.selfmadekid.data.AppData;
import com.example.selfmadekid.data.ChildContainer;
import com.example.selfmadekid.data.Goal;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChildAbout extends Fragment {

    private TextView mGoalText;
    private Goal goal;
    private ProgressBar mProgressBar;
    private TextView mProgressText;
    private ChildContainer childContainer;
    private AlertDialog dialog;
    private CardView currentGoal;
    private String newGoalName="";
    private AddGoal addGoalTask = null;
    private ImageButton goalCheckButton;
    private View mProgressBarLayout;

    private int returnedNewGoalID = -1;

    public ChildAbout() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public static ChildAbout newInstance() {
        //this.context = context;
        return new ChildAbout();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_child_about, container, false);
        childContainer = AppData.getChildren().get(getSelectedChildID());

        currentGoal = view.findViewById(R.id.current_goal);
        mProgressText = view.findViewById(R.id.progress_text);
        mGoalText = view.findViewById(R.id.goal_text);
        mProgressBar = view.findViewById(R.id.child_progress_bar);
        goalCheckButton = view.findViewById(R.id.goal_check);
        mProgressBarLayout = view.findViewById(R.id.progress_bar_layout);
        if (childContainer != null){
            goal = childContainer.getCurrentGoal();
            if (goal != null){
                System.out.println(goal.getCurrentPoints());
                mGoalText.setText(goal.getGoalName());
                Drawable drawable = getContext().getDrawable(R.drawable.progress_bar_circle);
                System.out.println("goal max points" + goal.getFinishPoints());
                if (goal.isConfirmed()){
                    mProgressBarLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setMax(goal.getFinishPoints()); // Maximum Progress
                    mProgressBar.setSecondaryProgress(goal.getFinishPoints()); // Secondary Progress
                    mProgressBar.setProgressDrawable(drawable);
                    mProgressBar.setProgress(goal.getCurrentPoints());
                    mProgressText.setText(getString(R.string.goal_progress_bar_as_text, goal.getCurrentPoints(), goal.getFinishPoints()));
                }
                else{
                    goalCheckButton.setVisibility(View.VISIBLE);
                    goalCheckButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.silverColor), android.graphics.PorterDuff.Mode.SRC_IN);
                    if (getActivity() instanceof MainActivity){
                        goalCheckButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updatePointsDialog();
                            }
                        });
                    }else {
                        goalCheckButton.setClickable(false);
                    }
                }
            }
        }
        currentGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goal == null){
                    if (getActivity() instanceof ChildMainActivity){
                        buildNameGoalDialog();
                    }
                }
            }
        });


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


    private void buildNameGoalDialog() {
        dialog = new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setTitle(getString(R.string.name_title))
                .setView(getLayoutInflater().inflate(R.layout.dialog_create_new_goal, null))
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
                    EditText mName = (EditText) ((Dialog)
                            dialog).findViewById(R.id.name_edit_text);

                    if (mName.getText().toString().equals("")) {
                        mName.setError(getString(R.string.error_field_required));
                    }else{
                        newGoalName  = mName.getText().toString();
                        addGoalTask = new AddGoal(newGoalName);
                        addGoalTask.execute();
                        dialog.dismiss();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public class AddGoal extends AsyncTask<Void, Void, Boolean> {

        private final String mGoal;

        AddGoal(String goal) {
            this.mGoal = goal;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.add_goal_script),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.get("error").equals("")){
                                        returnedNewGoalID = Integer.valueOf(jsonObject.get("goal_id").toString());
                                        onPostExecute(true);
                                    }else{
                                        makeToast(jsonObject.get("error").toString());
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
                        params.put("child_id", Integer.valueOf(getSelectedChildID()).toString() );
                        params.put("name", newGoalName);

                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(stringRequest);
            }catch (Exception e) {

            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                if (returnedNewGoalID != -1){
                    Goal goal = new Goal(newGoalName, 0, returnedNewGoalID);
                    AppData.getChildren().get(getSelectedChildID()).setCurrentGoal(goal);
                    ((ChildMainActivity) getActivity()).forceLoadingFragment();
                }
            }
            onCancelled();
        }

        @Override
        protected void onCancelled() {
            addGoalTask = null;
        }
    }



    public class ConfirmTask extends AsyncTask<Void, Void, Boolean> {

        private final int goalID;
        private final int finalPoints;

        public ConfirmTask(int goalID, int finalPoints) {
            this.goalID = goalID;
            this.finalPoints = finalPoints;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.update_goal_points),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    System.out.println("update_goal_points" + response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.get("error").equals("")){
                                        onPostExecute(true);
                                    }else{
                                        makeToast(jsonObject.get("error").toString());
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
                        params.put("goal_id", Integer.valueOf(goalID).toString() );
                        params.put("points", Integer.valueOf(finalPoints).toString() );
                        params.put("child_id", Integer.valueOf(AppData.getCurrentUserID()).toString());
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(stringRequest);
            }catch (Exception e) {

            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                AppData.getChildren().get(getSelectedChildID()).getCurrentGoal().setFinishPoints(finalPoints);
                ((MainActivity) getActivity()).forceLoadingFragment();
            }
            onCancelled();
        }

        @Override
        protected void onCancelled() {
            addGoalTask = null;
        }
    }




    private void makeToast(String str){
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }



    private void updatePointsDialog() {
        dialog = new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setTitle(getString(R.string.enter_points_count))
                .setView(getLayoutInflater().inflate(R.layout.dialog_update_points, null))
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
                    EditText mPoints = (EditText) ((Dialog)
                            dialog).findViewById(R.id.points_edit_text);

                    if (mPoints.getText().toString().equals("")) {
                        mPoints.setError(getString(R.string.error_field_required));
                    }else{
                        int points = Integer.valueOf(mPoints.getText().toString());
                        new ConfirmTask(goal.getGoal_id(), points ).execute();
                        dialog.dismiss();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }



}
