package com.example.selfmadekid.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

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
import com.example.selfmadekid.data.OneTimeTask;
import com.example.selfmadekid.data.RepetitiveTask;

import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.HashMap;
import java.util.Map;



public class ReceiveChildDataService extends Service {

    final String LOG_TAG = "ReceiveChildDataService";

    private int selectedChildID = -1;
    private Context context;
    private int userID;
    private Class activityToStart;
    private boolean startActivity;

    private boolean hasChild = false;

    public void onCreate() {
        super.onCreate();
        context = this;
        Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        startActivity = intent.getBooleanExtra("start_new_activity", true);
        userID = intent.getIntExtra("userID", -1);
        if (intent.getIntExtra("role", AppData.PARENT) == AppData.PARENT){
            activityToStart = MainActivity.class;
            getChildren();
        }else if (intent.getIntExtra("role", AppData.PARENT) == AppData.CHILD){
            activityToStart = ChildMainActivity.class;
            getChild(userID);
        }



        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    void getChildren() {
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
                                    hasChild = true;
                                    getChild(jsonArray.getInt(i));
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
            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                @Override
                public void onRequestFinished(Request<Object> request) {
                    startActivity(!hasChild);
                }
            });
        }catch (Exception e) {
        }
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
                                Log.d("getChild", response);
                                JSONObject jsonObject = new JSONObject(response);
                                if (selectedChildID == -1){
                                    selectedChildID = childID;
                                }


                                ChildContainer cContainer = new ChildContainer(
                                        childID,
                                        jsonObject.get("name").toString(),
                                        jsonObject.get("surname").toString(),
                                        jsonObject.get("patronymic").toString(),
                                        jsonObject.getInt("points_have")

                                );

                                AppData.getChildren().append(childID, cContainer);

                                int goalID = jsonObject.getInt("current_goal_id");
                                if (goalID != 0){
                                    getGoal(goalID, childID);
                                }
                                getFinishedGoals(childID);

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
            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                @Override
                public void onRequestFinished(Request<Object> request) {
                    startActivity(true);
                }
            });
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
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("name")){
                                    Goal goal = new Goal(
                                            jsonObject.getString("name"),
                                            jsonObject.getInt("current_points"),
                                            jsonObject.getInt("finish_points"),
                                            goal_id
                                    );
                                    AppData.getChildren().get(child_id).setCurrentGoal(goal);
                                    getTasks(goal_id, child_id);
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
            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                @Override
                public void onRequestFinished(Request<Object> request) {
                    startActivity(!hasChild);
                }
            });
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



    public void getTasks(final int goalID, final int childID)  {
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
                                    addRepetitiveCheckedTasks(row.getInt("task_id"), childID);

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
        //return false;
    }

    private void addRepetitiveCheckedTasks(final int task_id, final int childID){
        try {
            AppData.getChildren().get(childID).getCurrentGoal().getCheckedDates().put(task_id, new HashMap<LocalDate, Integer>());
            StringRequest stringRequest;
            stringRequest = new StringRequest(Request.Method.POST,
                    getString(R.string.get_checked_repetitive_tasks),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
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

    private void startActivity(boolean bool){
        if (bool && startActivity){
            Intent tmpIntent = new Intent(context, activityToStart);
            tmpIntent.putExtra("id", userID);
            tmpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP |  Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(tmpIntent);
        }
    }


}