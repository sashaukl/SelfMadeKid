package com.example.selfmadekid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.example.selfmadekid.data.RepetitiveTask;
import com.example.selfmadekid.parent_main_fragments.ChildAbout;
import com.example.selfmadekid.parent_main_fragments.ScheduleParent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChildMainActivity extends AppCompatActivity {
    private GetChildren childDataTask = null;
    private TextView mTextMessage;
    private ChildContainer childContainer;
    private int userID = -1;
    private int lastNavigationItemSelected = R.id.info;
    private Context context;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return preLoad(item.getItemId());
        }
    };

    private boolean preLoad(int itemId){
        lastNavigationItemSelected = itemId;
        switch (itemId) {
            case R.id.info:
                loadFragment(ChildAbout.newInstance());
                return true;
            case R.id.child_schedule:
                loadFragment(ScheduleParent.newInstance());
                return true;
        }
        return false;
    }
    public void forceLoadingFragment(){
        preLoad(lastNavigationItemSelected);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        this.setContentView(R.layout.activity_child_main);
        AppData.setCurrentState(AppData.CHILD);
        Locale locale = new Locale("RU");
        Locale.setDefault(locale);
        userID = getIntent().getIntExtra("id", -1);
        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(ChildAbout.newInstance());

        childDataTask = new GetChildren();
        childDataTask.execute();

        AppData.setCirrentUserID(userID);
        FirebaseApp.initializeApp(this);
        new UpdateToken().execute();

    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_activity_frame_layout, fragment);
        ft.commit();
    }


    public ChildContainer getChildContainer() {
        return childContainer;
    }


    public void onAddTaskButtonPressed(View view){
        if (userID != -1){
            Intent intent = new Intent(this, AddNewTaskActivity.class);
            intent.putExtra("id", userID);
            startActivity(intent);
        }
    }

    public void onAddNewChildPressed(View view){
        Intent intent = new Intent(this, AddNewChildActivity.class);
        intent.putExtra("parent_id", userID);
        startActivity(intent);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        int newID  = getIntent().getIntExtra("new_child_id", -1);
        if (newID != -1){
            if (childDataTask == null){
                childDataTask = new GetChildren();
            }
            //childDataTask.getChild(newID);
        }
        preLoad(lastNavigationItemSelected);
    }

    public int getSelectedChildID() {
        return userID;
    }


    public class GetChildren extends AsyncTask<Void, Void, Boolean> {

        public GetChildren() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                StringRequest stringRequest;
                stringRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.get_child_data),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    System.out.println(response);
                                    System.out.println("getChild" + response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    childContainer = new ChildContainer(userID,
                                            jsonObject.get("name").toString(),
                                            jsonObject.get("surname").toString(),
                                            jsonObject.get("patronymic").toString(),
                                            jsonObject.getInt("points_have")
                                            //Goal //todo -- make goal

                                    );

                                    AppData.getChildren().append(userID, childContainer);

                                    int goalID = jsonObject.getInt("current_goal_id");
                                    if (goalID != 0){
                                        getGoal(goalID, userID);
                                    }
                                    getFinishedGoals(userID);
                                    preLoad(lastNavigationItemSelected);
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
                        params.put("child_id", Integer.valueOf(userID).toString() );
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            }catch (Exception e) {

            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            childDataTask = null;
        }

        @Override
        protected void onCancelled() {
            childDataTask = null;
        }

        protected void getGoal(final int goal_id, final int child_id ){
            try {
                StringRequest stringRequest;
                ChildContainer childContainer;
                stringRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.get_goal),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.has("name")){
                                        Goal goal = new Goal(
                                                jsonObject.getString("name"),
                                                jsonObject.getInt("current_points"),
                                                jsonObject.getInt("finish_points"),
                                                goal_id
                                        );
                                        AppData.getChildren().get(child_id).setCurrentGoal(goal);
                                        GetTasks mGetTasks =  new GetTasks(goal_id, child_id);
                                        mGetTasks.execute();
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
                        params.put("goal_id", Integer.valueOf(goal_id).toString() );
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            }catch (Exception e) {
            }
            return;
        }

        protected void getFinishedGoals(final int child_id ){
            try {
                StringRequest stringRequest;
                ChildContainer childContainer;
                stringRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.get_finished_goals),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonArray= new JSONArray(response);
                                    for (int i = 0; i<jsonArray.length(); i++){
                                        AppData.getChildren().get(child_id).getFinishedGoals().add(jsonArray.get(i).toString());
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
                        params.put("child_id", Integer.valueOf(child_id).toString() );
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            }catch (Exception e) {
            }
            return;
        }


    }

    public class GetTasks extends AsyncTask<Void, Void, Boolean> {

        private int goalID;
        private int childID;

        public GetTasks(int goalID, int childID) {
            this.goalID = goalID;
            this.childID = childID;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                StringRequest stringRequest;
                stringRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.get_tasks),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject =  new JSONObject(response);

                                    JSONArray one_time_tasks = ((JSONArray) jsonObject.get("one_time_tasks"));

                                    for (int i=0; i<one_time_tasks.length();i++){
                                        JSONObject row = new JSONObject(one_time_tasks.get(i).toString());
                                        AppData.getChildren().get(childID).getCurrentGoal().getOneTimeTaskContainer().add(new OneTimeTask(
                                                row.getInt("task_id"),
                                                row.getString("name"),
                                                //no to millis usage :(
                                                LocalDate.of(
                                                        row.getInt("year_end"),
                                                        row.getInt("month_end"),
                                                        row.getInt("day_end")
                                                ),
                                                row.getInt("hour_end"),
                                                row.getInt("minute_end"),
                                                row.getInt("value"),
                                                row.getInt("finished")
                                        ));
                                    }

                                    JSONArray reprtitive_tasks  = ((JSONArray) jsonObject.get("repetitive_tasks"));
                                    for (int i=0; i<reprtitive_tasks.length();i++){
                                        JSONObject row = new JSONObject(reprtitive_tasks.get(i).toString());
                                        AppData.getChildren().get(childID).getCurrentGoal().getDayOfTheWeekContainer(DayOfWeek.of(row.getInt("day"))).add(
                                                new RepetitiveTask(
                                                        row.getInt("task_id"),
                                                        row.getString("name"),
                                                        row.getInt("value"),
                                                        row.getInt("hour"),
                                                        row.getInt("minute")
                                                )
                                        );
                                        addRepetitiveCheckedTasks(row.getInt("task_id"));
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
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("goal_id", Integer.valueOf(goalID).toString());
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            } catch (Exception e) {
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            forceLoadingFragment();
        }

        @Override
        protected void onCancelled() {

        }

        private void addRepetitiveCheckedTasks(final int task_id){
            try {
                AppData.getChildren().get(childID).getCurrentGoal().getCheckedDates().put(task_id, new HashMap<LocalDate, Integer>());
                StringRequest stringRequest;
                stringRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.get_checked_repetitive_tasks),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    System.out.println(response);
                                    JSONObject jsonObject =  new JSONObject(response);
                                    if (jsonObject.getString("error").isEmpty()){
                                        JSONArray checked_tasks = ((JSONArray) jsonObject.get("0"));
                                        for (int i=0; i<checked_tasks.length();i++){
                                            JSONObject row = new JSONObject(checked_tasks.get(i).toString());
                                            AppData.getChildren().get(childID).getCurrentGoal().getCheckedDates().get(task_id).put(LocalDate.of(
                                                    row.getInt("year"),
                                                    row.getInt("month"),
                                                    row.getInt("day")),row.getInt("finished"));
                                        }
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
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("task_id", Integer.valueOf(task_id).toString());
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            } catch (Exception e) {
            }
        }

    }


    private class UpdateToken extends AsyncTask<Void, Void, Boolean> {
        public UpdateToken() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
                FirebaseApp.getInstance();
            }catch (Exception e) {
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        }

        @Override
        protected void onCancelled() {
        }
    }

    private void makeToast(String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
