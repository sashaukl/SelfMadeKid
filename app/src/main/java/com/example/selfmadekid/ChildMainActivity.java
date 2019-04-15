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
import com.example.selfmadekid.child_main_fragments.ChildAboutFragmentChild;
import com.example.selfmadekid.data.AppData;
import com.example.selfmadekid.data.ChildContainer;
import com.example.selfmadekid.parent_main_fragments.ChildAbout;
import com.example.selfmadekid.parent_main_fragments.ChildSelect;
import com.example.selfmadekid.parent_main_fragments.ScheduleParent;
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
        this.context = this;
        this.setContentView(R.layout.activity_child_main);
        Locale locale = new Locale("RU");
        Locale.setDefault(locale);
        userID = getIntent().getIntExtra("id", -1);
        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(ChildAbout.newInstance());

        childDataTask = new GetChildren();
        childDataTask.execute();


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
                                            jsonObject.get("patronymic").toString()
                                            //Goal //todo -- make goal

                                    );

                                    AppData.getChildren().append(userID, childContainer);
                                    System.out.println(AppData.getChildren().get(userID));
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

    }
    private void makeToast(String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
