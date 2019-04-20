package com.example.selfmadekid;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;


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
import com.example.selfmadekid.parent_main_fragments.ChildSelect;
import com.example.selfmadekid.parent_main_fragments.ScheduleParent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private GetChildren childDataTask = null;
    private TextView mTextMessage;
    private ChildContainer childContainer;
    private int selectedChildID = -1;
    private int userID = -1;
    public int lastNavigationItemSelected = R.id.navigation_home;
    Context context;
    private GetTasks getTasks = null;

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
            case R.id.navigation_home:
                loadFragment(ChildSelect.newInstance());
                return true;
            case R.id.info:
                loadFragment(ChildAbout.newInstance());
                return true;
            case R.id.child_schedule:
                loadFragment(ScheduleParent.newInstance());
                return true;
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppData.setCurrentState(AppData.PARENT);
        this.context = this;
        this.setContentView(R.layout.activity_main);
        Locale locale = new Locale("RU");
        Locale.setDefault(locale);
        userID = getIntent().getIntExtra("id", -1);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(ChildSelect.newInstance());

        childDataTask = new GetChildren();
        childDataTask.execute((Void) null);




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
        if (selectedChildID != -1){
            if (AppData.getChildren().get(selectedChildID).getCurrentGoal()!= null){
                if (AppData.getChildren().get(selectedChildID).getCurrentGoal().getGoal_id()!= -1 && AppData.getChildren().get(selectedChildID).getCurrentGoal().getGoal_id()!= 0){
                    Intent intent = new Intent(this, AddNewTaskActivity.class);
                    intent.putExtra("id", selectedChildID);
                    startActivity(intent);
                }
            }
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
            childDataTask.getChild(newID);
        }
        preLoad(lastNavigationItemSelected);
    }

    public int getSelectedChildID() {
        return selectedChildID;
    }

    public void setSelectedChildID(int selectedChildID) {
        this.selectedChildID = selectedChildID;
        System.out.println(AppData.getChildren().get(selectedChildID).getCurrentGoal());
    }

    public class GetChildren extends AsyncTask<Void, Void, Boolean> {

        public GetChildren() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                StringRequest stringRequest;
                stringRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.get_children_script),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    for (int i=0; i<jsonArray.length(); i++){
                                        getChild(jsonArray.getInt(i));
                                        if (selectedChildID == -1){
                                            if (AppData.getChildren().size()!=0){

                                            }
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
                        params.put("parent_id", Integer.valueOf(userID).toString() );
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
        }

        @Override
        protected void onCancelled() {
        }


        protected ChildContainer getChild(final int childID){
            try {
                StringRequest stringRequest;
                final ChildContainer childContainer;
                stringRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.get_child_data),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (selectedChildID == -1){
                                        selectedChildID = childID;
                                        System.out.println("selectedChildID" + selectedChildID );
                                    }


                                    ChildContainer cContainer = new ChildContainer(
                                            childID,
                                            jsonObject.get("name").toString(),
                                            jsonObject.get("surname").toString(),
                                            jsonObject.get("patronymic").toString()
                                            //Goal //todo -- make goal

                                    );

                                    AppData.getChildren().append(childID, cContainer);

                                    int goalID = jsonObject.getInt("current_goal_id");
                                    if (goalID != 0){
                                        getGoal(goalID, childID);
                                    }
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
                        params.put("child_id", Integer.valueOf(childID).toString() );
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            }catch (Exception e) {
            }
            return null;
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
                                    System.out.println("getGoal" + response);
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
    }

    public void forceLoadingFragment(){
        preLoad(lastNavigationItemSelected);
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
                                    System.out.println(response);
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








    private void makeToast(String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
