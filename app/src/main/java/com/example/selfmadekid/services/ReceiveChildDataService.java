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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class ReceiveChildDataService extends Service {

    final String LOG_TAG = "ReceiveChildDataService";
    private Context context;
    private int userID;
    private Class activityToStart;
    private boolean startActivity;
    private boolean isHasGoal = false;
    private boolean hasChild = false;
    private JSONArray childJsonArray;
    private JSONObject childJsonObject;
    private JSONObject goalJsonObject;
    private JSONObject tasksJsonObject;
    private JSONArray finishedGoals;
    private JSONObject repetitiveJsonObject;
    private RequestQueue requestQueue;
    private final int timeToUpdate = 1000 * 20;


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
            if ( Calendar.getInstance().getTimeInMillis() - AppData.getLastUpdate() > timeToUpdate ) {
                getChildren();
            }else{
                try{
                    Thread.sleep(490);
                }catch (Exception e){}
                startActivity(true);
            }
        }else if (intent.getIntExtra("role", AppData.PARENT) == AppData.CHILD){
            activityToStart = ChildMainActivity.class;
            if ( Calendar.getInstance().getTimeInMillis() - AppData.getLastUpdate() > timeToUpdate ) {
                getChild(userID);
            }else{
                try{
                    Thread.sleep(490);
                }catch (Exception e){}
                startActivity(true);
            }
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
                                Log.d("getChildren", response);
                                childJsonArray = new JSONArray(response);
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
            requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                @Override
                public void onRequestFinished(Request<Object> request) {
                    try {
                        if (childJsonArray != null){
                            for (int i = 0; i < childJsonArray.length(); i++) {
                                hasChild = true;
                                getChild(childJsonArray.getInt(i));
                            }
                            Log.d("getChildren", "ended");
                        }
                    }catch (Exception e){ }
                    startActivity(!hasChild);
                }
            });
            requestQueue.getCache().clear();
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
                                Log.d("getChild", "start");
                                childJsonObject = new JSONObject(response);

                                if (childJsonObject != null){
                                    ChildContainer cContainer = new ChildContainer(
                                            childID,
                                            childJsonObject.get("name").toString(),
                                            childJsonObject.get("surname").toString(),
                                            childJsonObject.get("patronymic").toString(),
                                            childJsonObject.getInt("points_have")
                                    );

                                    AppData.getChildren().append(childID, cContainer);
                                    int goalID = childJsonObject.getInt("current_goal_id");
                                    if (goalID != 0){
                                        getGoal(goalID, childID);
                                        isHasGoal = true;
                                    }
                                    getFinishedGoals(childID);
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
                    params.put("child_id", Integer.valueOf(childID).toString() );
                    return params;
                }
            };

            requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                @Override
                public void onRequestFinished(Request<Object> request) {
                    try {
                    }catch (Exception e){
                        startActivity(!isHasGoal);
                    }
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
                            Log.d("getGoal", "start");
                            try {
                                goalJsonObject = new JSONObject(response);
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
            requestQueue = Volley.newRequestQueue(context);
            stringRequest.setTag("update");
            requestQueue.add(stringRequest);
            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                @Override
                public void onRequestFinished(Request<Object> request) {
                    try{
                        if (goalJsonObject != null){
                            if (goalJsonObject.has("name")) {
                                Goal goal = new Goal(
                                        goalJsonObject.getString("name"),
                                        goalJsonObject.getInt("current_points"),
                                        goalJsonObject.getInt("finish_points"),
                                        goal_id
                                );
                                AppData.getChildren().get(child_id).setCurrentGoal(goal);
                                getTasks(goal_id, child_id);
                            }
                        }
                    }catch (Exception e){
                    }
                    startActivity(true);
                }
            });
            requestQueue.getCache().clear();
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
                            Log.d("getFinishedGoals", "start");
                            try {
                                finishedGoals = new JSONArray(response);
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
            requestQueue = Volley.newRequestQueue(context);
            stringRequest.setTag("update");
            requestQueue.add(stringRequest);
            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                @Override
                public void onRequestFinished(Request<Object> request) {
                    Log.d("getFinishedGoals", "ended");
                    try {
                        if (finishedGoals != null){
                            for (int i = 0; i<finishedGoals.length(); i++){
                                AppData.getChildren().get(child_id).getFinishedGoals().add(finishedGoals.get(i).toString());
                            }
                        }
                    }catch (Exception e){

                    }
                }
            });
            requestQueue.getCache().clear();
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
                                Log.d("getTasks", "start");
                                tasksJsonObject =  new JSONObject(response);
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
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            stringRequest.setTag("update");
            requestQueue.add(stringRequest);
            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                @Override
                public void onRequestFinished(Request<Object> request) {
                    Log.d("getTasks", "ended");
                    try {
                        if (tasksJsonObject != null){
                            JSONArray one_time_tasks = ((JSONArray) tasksJsonObject.get("one_time_tasks"));

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

                            JSONArray reprtitive_tasks = ((JSONArray) tasksJsonObject.get("repetitive_tasks"));
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

                        }
                    }catch (Exception e){

                    }


                }
            });
            requestQueue.getCache().clear();
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
                                Log.d("addRepetitiveCheckedTasks", "start");
                                repetitiveJsonObject =  new JSONObject(response);
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
            requestQueue = Volley.newRequestQueue(context);
            stringRequest.setTag("update");
            requestQueue.add(stringRequest);
            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                @Override
                public void onRequestFinished(Request<Object> request) {
                    Log.d("addRepetitiveCheckedTasks", "ended");
                    try {
                        if (repetitiveJsonObject != null){
                            if (repetitiveJsonObject.getString("error").isEmpty()){
                                JSONArray checked_tasks = ((JSONArray) repetitiveJsonObject.get("0"));
                                for (int i=0; i<checked_tasks.length();i++){
                                    JSONObject row = new JSONObject(checked_tasks.get(i).toString());
                                    AppData.getChildren().get(childID).getCurrentGoal().getCheckedDates().get(task_id).put(LocalDate.of(
                                            row.getInt("year"),
                                            row.getInt("month"),
                                            row.getInt("day")),row.getInt("finished"));
                                }
                            }
                        }
                    } catch (Exception e){

                    }
                }
            });
            requestQueue.getCache().clear();

        } catch (Exception e) {
        }
    }

    private void startActivity(boolean bool){
        if (bool && startActivity){
            AppData.setLastUpdate(Calendar.getInstance().getTimeInMillis());
            AppData.writeData(context);
            AppData.setLoaded(true);
            Intent tmpIntent = new Intent(context, activityToStart);
            tmpIntent.putExtra("id", userID);
            tmpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP |  Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("startActivity", "started");
            startActivity(tmpIntent);
            stopSelf();
        }
    }

}