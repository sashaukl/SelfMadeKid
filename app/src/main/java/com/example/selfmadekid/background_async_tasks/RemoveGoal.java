package com.example.selfmadekid.background_async_tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.selfmadekid.R;

import java.util.HashMap;
import java.util.Map;

public class RemoveGoal extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "RemoveGoalTask";

    private int child_id;
    private int goal_id;
    private Context context;

    public RemoveGoal(int child_id, int goal_id, Context context) {
        this.child_id = child_id;
        this.goal_id = goal_id;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            StringRequest stringRequest;
            stringRequest = new StringRequest(Request.Method.POST,
                    context.getString(R.string.remove_goal),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d(TAG, response);
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
                    params.put("goal_id", Integer.valueOf(goal_id).toString() );
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
}