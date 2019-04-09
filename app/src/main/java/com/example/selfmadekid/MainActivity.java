package com.example.selfmadekid;

import android.content.Context;
import android.content.Intent;
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
import com.example.selfmadekid.main_fragments.ChildAbout;
import com.example.selfmadekid.main_fragments.ChildSelect;
import com.example.selfmadekid.main_fragments.ScheduleParent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private GetChildren mAuthTask = null;
    private TextView mTextMessage;
    private ChildContainer childContainer;
    private int selectedChildID = -1;
    private int userID = -1;
    public int lastNavigationItemSelected = R.id.navigation_home;
    Context context;

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
            case R.id.navigation_notifications:
                //mTextMessage.setText(R.string.title_notifications);
                return true;
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        this.setContentView(R.layout.activity_main);
        Locale locale = new Locale("RU");
        Locale.setDefault(locale);
        userID = getIntent().getIntExtra("id", -1);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(ChildSelect.newInstance());

        mAuthTask = new GetChildren();
        mAuthTask.execute((Void) null);


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
            Intent intent = new Intent(this, AddNewTaskActivity.class);
            intent.putExtra("id", selectedChildID);
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
        preLoad(lastNavigationItemSelected);
    }

    public int getSelectedChildID() {
        return selectedChildID;
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
                                    System.out.println(response);
                                    JSONArray jsonArray = new JSONArray(response);
                                    for (int i=0; i<jsonArray.length(); i++){
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
            }catch (Exception e) {
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //showProgress(false);
            if (success) {
                //Intent intent = new Intent(context, MainActivity.class);
                //intent.putExtra("child_id", childID);
                //startActivity(intent);
                //finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
        }


        protected ChildContainer getChild(final int childID){
            try {
                StringRequest stringRequest;
                ChildContainer childContainer;
                stringRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.get_child_data),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    System.out.println(response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    ChildContainer childContainer = new ChildContainer(childID,
                                            jsonObject.get("name").toString(),
                                            jsonObject.get("surname").toString(),
                                            jsonObject.get("patronymic").toString()
                                            //Goal //todo -- make goal

                                    );
                                    AppData.getChildren().append(childID, childContainer);
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

    }
    private void makeToast(String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
