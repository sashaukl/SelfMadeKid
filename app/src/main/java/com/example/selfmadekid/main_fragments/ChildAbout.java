package com.example.selfmadekid.main_fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.selfmadekid.SelectRoleActivity;
import com.example.selfmadekid.adapters.AchievementAdapter;
import com.example.selfmadekid.data.AppData;
import com.example.selfmadekid.data.ChildContainer;
import com.example.selfmadekid.data.Goal;
import com.example.selfmadekid.services.ReceiveChildDataService;

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
    private View achievementsTextLayout;
    private ImageButton achievementsButton;
    private View achievementsHistoryLayout;
    private TextView pointsCount;
    private Button sendMoneyButton;
    private Button shareMoneyButton;
    private TextView checkGoalHint;
    private TextView achievementsCountText;
    private int returnedNewGoalID = -1;
    private ImageButton mExitButton;
    private ImageButton mUpdateButton;

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

        final View view = inflater.inflate(R.layout.fragment_points, container, false);
        childContainer = AppData.getChildren().get(getSelectedChildID());
        Log.d("childcontainer", String.valueOf(getSelectedChildID()));

        //currentGoal = view.findViewById(R.id.goal_label);
        mProgressText = view.findViewById(R.id.progress_text);
        mGoalText = view.findViewById(R.id.goal_label);
        mProgressBar = view.findViewById(R.id.child_progress_bar);
        goalCheckButton = view.findViewById(R.id.goal_check);
        mProgressBarLayout = view.findViewById(R.id.progress_bar_layout);
        achievementsTextLayout = view.findViewById(R.id.achievements_text_layout);
        achievementsButton = view.findViewById(R.id.achievements_icon);
        achievementsHistoryLayout = view.findViewById(R.id.achievements_history_layout);
        pointsCount = view.findViewById(R.id.points_count);
        sendMoneyButton = view.findViewById(R.id.send_money_button);
        shareMoneyButton = view.findViewById(R.id.share_money_button);
        checkGoalHint = view.findViewById(R.id.check_goal_hint);
        achievementsCountText = view.findViewById(R.id.achievements_count_text);

        achievementsButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                achievementsButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ConstraintLayout.LayoutParams llp = (ConstraintLayout.LayoutParams) achievementsTextLayout.getLayoutParams();
                int margin = (int)(achievementsButton.getWidth() * 0.22);
                System.out.println("goalCheckButton " + achievementsButton.getWidth() );
                llp.setMargins( margin, margin, margin, margin ); // llp.setMargins(left, top, right, bottom);
                achievementsTextLayout.setVisibility(View.VISIBLE);
                achievementsTextLayout.setLayoutParams(llp);

            }
        });
        achievementsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (achievementsHistoryLayout != null){
                    if (achievementsHistoryLayout.getVisibility() == View.GONE){
                        achievementsHistoryLayout.setVisibility(View.VISIBLE);
                    }else {
                        achievementsHistoryLayout.setVisibility(View.GONE);
                    }
                }
            }
        });


        if (childContainer != null){
            goalCheckButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    goalCheckButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    ConstraintLayout.LayoutParams llp = (ConstraintLayout.LayoutParams) checkGoalHint.getLayoutParams();
                    int margin = (int)(goalCheckButton.getWidth() * 0.22);
                    System.out.println("goalCheckButton " + achievementsButton.getWidth() );
                    llp.setMargins( margin, margin, margin, margin ); // llp.setMargins(left, top, right, bottom);
                    checkGoalHint.setVisibility(View.VISIBLE);
                    checkGoalHint.setLayoutParams(llp);

                }
            });
            if (getActivity() instanceof MainActivity){
                sendMoneyButton.setVisibility(View.VISIBLE);
                sendMoneyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMoneyToChildDialog();
                    }
                });
            } else if (getActivity() instanceof ChildMainActivity){
                shareMoneyButton.setVisibility(View.VISIBLE);
                shareMoneyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMoneyToFriendDialog();
                    }
                });
            }
            achievementsCountText.setText(String.valueOf(childContainer.getFinishedGoals().size()));
            goal = childContainer.getCurrentGoal();
            pointsCount.setText(String.valueOf(childContainer.getPoints()));
            Log.d("childContainer", "childContainer != null");

            if (goal != null){
                Log.d("goal", "goal != null");
                mGoalText.setText(goal.getGoalName());
                if (goal.getGoalName().length() > 10){
                    TextViewCompat.setAutoSizeTextTypeWithDefaults(mGoalText,
                            TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                }
                Drawable drawable = getContext().getDrawable(R.drawable.progress_bar_circle);

                if (goal.isConfirmed()){
                    System.out.println(goal);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setMax(goal.getFinishPoints()); // Maximum Progress
                    mProgressBar.setSecondaryProgress(goal.getFinishPoints()); // Secondary Progress
                    mProgressBar.setProgressDrawable(drawable);
                    mProgressBar.setProgress(goal.getCurrentPoints());
                    mProgressText.setText(getString(R.string.goal_progress_bar_as_text, goal.getCurrentPoints(), goal.getFinishPoints()));


                    mProgressBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mProgressBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            ConstraintLayout.LayoutParams llp = (ConstraintLayout.LayoutParams) mProgressText.getLayoutParams();
                            int margin = (int)(mProgressBar.getWidth() * 0.18);
                            System.out.println("margin " + margin );
                            llp.setMargins( margin, margin, margin, margin ); // llp.setMargins(left, top, right, bottom);
                            mProgressText.setVisibility(View.VISIBLE);
                            mProgressText.setLayoutParams(llp);

                        }
                    });


                } else {
                    goalCheckButton.setVisibility(View.VISIBLE);
                    checkGoalHint.setText(getString(R.string.not_confirmed));
                    goalCheckButton.setImageDrawable(getContext().getDrawable(R.drawable.ic_empty_center_circle));
                    if (getActivity() instanceof MainActivity){
                        Log.d("NO GOAL", "MainActivity");
                        checkGoalHint.setText(getString(R.string.confirm_task_action));
                        goalCheckButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updatePointsDialog();
                            }
                        });
                    }
                }
            } else {
                Log.d("NO GOAL", "here");
                checkGoalHint.setText(getString(R.string.create_goal));
                goalCheckButton.setVisibility(View.VISIBLE);
                //goalCheckButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.silverColor), android.graphics.PorterDuff.Mode.SRC_IN);
                if (getActivity() instanceof ChildMainActivity ){
                    Log.d("NO GOAL", "ChildMainActivity");
                    goalCheckButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkGoalHint.setText("");
                            ((Animatable)goalCheckButton.getDrawable()).start();
                            buildNameGoalDialog();
                        }
                    });
                }else{
                    checkGoalHint.setText(getString(R.string.not_created));
                }
            }
        }

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

        RecyclerView achievements_recycler =  view.findViewById(R.id.achievements_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        achievements_recycler.setLayoutManager(layoutManager);
        if(AppData.getChildren().get(getSelectedChildID()) != null){
            achievements_recycler.setAdapter(new AchievementAdapter(getContext(), AppData.getChildren().get(getSelectedChildID()).getFinishedGoals()) );
        }

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



    private void sendMoneyToChildDialog() {
        dialog = new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setTitle(getString(R.string.send_selfies_label))
                .setView(getLayoutInflater().inflate(R.layout.dialog_send_selfies, null))
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
                        new SendMoneyToChild(getSelectedChildID(), points).execute();
                        dialog.dismiss();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    public class SendMoneyToChild extends AsyncTask<Void, Void, Boolean> {
        private final String TAG = "SendMoneyToChild";
        private final int child_ID;
        private final int amount;
        private boolean isOK = false;

        public SendMoneyToChild(int child_ID, int amount) {
            this.child_ID = child_ID;
            this.amount = amount;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.send_tokens),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.d(TAG, response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (!jsonObject.has("error")){
                                        AppData.getChildren().get(child_ID).addPoints(amount);
                                        ((MainActivity) getActivity()).forceLoadingFragment();
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
                        params.put("id", Integer.valueOf(child_ID).toString() );
                        params.put("amount", Integer.valueOf(amount).toString());
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(stringRequest);
            }catch (Exception e) {

            }
            return isOK;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            onCancelled();
        }

        @Override
        protected void onCancelled() {
            addGoalTask = null;
        }
    }


    private void sendMoneyToFriendDialog() {
        dialog = new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setTitle(getString(R.string.send_selfies_label))
                .setView(getLayoutInflater().inflate(R.layout.dialog_share_money, null))
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
                    EditText mEmail = (EditText) ((Dialog)
                            dialog).findViewById(R.id.receiver_name);


                    if (mPoints.getText().toString().equals("")) {
                        mPoints.setError(getString(R.string.error_field_required));
                    }else if (mEmail.getText().toString().equals("")){
                        mEmail.setError(getString(R.string.error_field_required));
                    }else if (!mEmail.getText().toString().contains("@")){
                        mEmail.setError(getString(R.string.error_invalid_email));
                    }else{
                        int points = Integer.valueOf(mPoints.getText().toString());
                        new SendMoneyToFriendTask(getSelectedChildID(), points, mEmail.getText().toString()).execute();
                        dialog.dismiss();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    public class SendMoneyToFriendTask extends AsyncTask<Void, Void, Boolean> {
        private final String TAG = "SendMoneyToFriendTask";

        private final int senderId;
        private final int amount;
        private final String email;
        private boolean isOK = false;

        public SendMoneyToFriendTask(int senderId, int amount, String email) {
            this.senderId = senderId;
            this.amount = amount;
            this.email = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.send_money_to_friend),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.d(TAG, response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (!jsonObject.has("error")){
                                        AppData.getChildren().get(senderId).addPoints(-amount);
                                        ((MainActivity) getActivity()).forceLoadingFragment();
                                    }else{
                                        switch (jsonObject.get("error").toString()){
                                            case "no_money":
                                                makeToast(getString(R.string.not_enouth_money));
                                                break;
                                            case "no_user":
                                                makeToast(getString(R.string.no_such_user));
                                                break;
                                        }
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
                        params.put("sender_id", Integer.valueOf(senderId).toString() );
                        params.put("amount", Integer.valueOf(amount).toString());
                        params.put("receiver_email", email);
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(stringRequest);
            }catch (Exception e) {

            }
            return isOK;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            onCancelled();
        }

        @Override
        protected void onCancelled() {
            addGoalTask = null;
        }
    }



}
