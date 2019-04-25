package com.example.selfmadekid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class AddNewChildActivity extends AppCompatActivity  {

    private final int NEW_CHILD_REGISTRATION = 1;
    private final int ADD_EXIST_CHILD = 2;

    private int mode = NEW_CHILD_REGISTRATION;


    private AddChildTask mAuthTask = null;
    private AddExistChild mAddExistChild= null;
    private Context context;

    // UI references.
    private EditText mFirstNameView;
    private EditText mSecondNameView;
    private EditText mPatronymicView;
    private EditText mEmailFormView;
    private EditText mPasswordView;
    private EditText mPasswordRepeatView;
    private Button mTopLoginOrRegButton;
    private Button mAddButton;
    private View mAddChildFormView;
    private View mProgressView;
    private int childID = -1;
    private int parentID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_child);
        this.context = this;
        parentID = getIntent().getIntExtra("parent_id",-1);
        mFirstNameView = findViewById(R.id.child_name);
        mSecondNameView = findViewById(R.id.second_name);
        mPatronymicView = findViewById(R.id.patronymic);
        mEmailFormView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mPasswordRepeatView = findViewById(R.id.password_repeat);
        mTopLoginOrRegButton = findViewById(R.id.add_button);
        mProgressView = findViewById(R.id.login_progress);
        mAddChildFormView = findViewById(R.id.login_form);

    }


    public void attemptAdding(View view) {
        if (mAuthTask != null || mAddExistChild != null) {
            System.out.println("--------------------------------------");
            return;
        }
        if (mode == NEW_CHILD_REGISTRATION) {
            // Reset errors.
            System.out.println("++++++++++++++++++++");
            mEmailFormView.setError(null);
            mPasswordView.setError(null);

            // Store values at the time of the login attempt.
            String email = mEmailFormView.getText().toString();
            String password = mPasswordView.getText().toString();
            String name = mFirstNameView.getText().toString();
            String surname = mSecondNameView.getText().toString();
            String patronymic = mPatronymicView.getText().toString();


            boolean cancel = false;
            View focusView = null;


            if (mFirstNameView.getText().toString().equals("")) {
                mFirstNameView.setError(getString(R.string.error_field_required));
                focusView = mFirstNameView;
                cancel = true;
            }

            // Check for a valid password, if the user entered one.
            if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }

            if (!mPasswordView.getText().toString().equals(mPasswordRepeatView.getText().toString())) {
                mPasswordRepeatView.setError(getString(R.string.passwords_must_be_the_same));
                focusView = mPasswordRepeatView;
                cancel = true;
            }

            if (TextUtils.isEmpty(password)) {
                mPasswordView.setError(getString(R.string.error_field_required));
                focusView = mPasswordView;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                mEmailFormView.setError(getString(R.string.error_field_required));
                focusView = mEmailFormView;
                cancel = true;
            } else if (!isEmailValid(email)) {
                mEmailFormView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailFormView;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                showProgress(true);
                mAuthTask = new AddChildTask(email, password, name, surname, patronymic);
                mAuthTask.execute((Void) null);
            }
        }else if( mode == ADD_EXIST_CHILD) {



            // Store values at the time of the login attempt.
            String email = mEmailFormView.getText().toString();
            String password = mPasswordView.getText().toString();


            boolean cancel = false;
            View focusView = null;

            // Check for a valid password, if the user entered one.
            if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }

            if (TextUtils.isEmpty(password)) {
                mPasswordView.setError(getString(R.string.error_field_required));
                focusView = mPasswordView;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                mEmailFormView.setError(getString(R.string.error_field_required));
                focusView = mEmailFormView;
                cancel = true;
            } else if (!isEmailValid(email)) {
                mEmailFormView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailFormView;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=");
                focusView.requestFocus();
            } else {
                System.out.println("================================================");
                showProgress(true);
                mAddExistChild = new AddExistChild(email, password, parentID);
                mAddExistChild.execute((Void) null);
            }
        }
    }


    public void attemptAddingExisted(View view) {
        if (mAuthTask != null) {
            return;
        }


        // Reset errors.
        mEmailFormView.setError(null);
        mPasswordView.setError(null);

        if (mFirstNameView.getVisibility() == View.VISIBLE){
            ((Button) view).setText(getString(R.string.new_child_registration));
            mFirstNameView.setVisibility(View.GONE);
            mSecondNameView.setVisibility(View.GONE);
            mPatronymicView.setVisibility(View.GONE);
            mPasswordRepeatView.setVisibility(View.GONE);
            mode = ADD_EXIST_CHILD;
        }else{
            ((Button) view).setText(getString(R.string.child_already_registered));
            mFirstNameView.setVisibility(View.VISIBLE);
            mSecondNameView.setVisibility(View.VISIBLE);
            mPatronymicView.setVisibility(View.VISIBLE);
            mPasswordRepeatView.setVisibility(View.VISIBLE);
            mode = NEW_CHILD_REGISTRATION;
        }

    }



    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAddChildFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAddChildFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAddChildFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mAddChildFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class AddChildTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mName;
        private final String mSurname;
        private final String mPatronymic;


        public AddChildTask(String mEmail, String mPassword, String mName, String mSurname, String mPatronymic) {
            this.mEmail = mEmail;
            this.mPassword = mPassword;
            this.mName = mName;
            this.mSurname = mSurname;
            this.mPatronymic = mPatronymic;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                StringRequest stringRequest;
                stringRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.add_child_script),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    System.out.println(response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.get("error").equals("")){
                                        childID = Integer.valueOf(jsonObject.get("id").toString());
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
                        params.put("email", mEmail);
                        params.put("password", mPassword);
                        params.put("name", mName);
                        params.put("surname", mSurname);
                        params.put("patronymic", mPatronymic);
                        params.put("parent_id", Integer.valueOf(parentID).toString() );
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
            showProgress(false);
            if (success) {
                Intent intent = new Intent(context, MainActivity.class);


                ChildContainer cContainer = new ChildContainer(
                        childID,
                        mName,
                        mSurname,
                        mPatronymic,
                       0

                );
                AppData.getChildren().append(childID, cContainer);
                intent.putExtra("new_child_id", childID);
                startActivity(intent);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class AddExistChild extends AsyncTask<Void, Void, Boolean> {

        private final String TAG = "AddExistChild";
        private final String mEmail;
        private final String mPassword;
        private final int parent_id;

        public AddExistChild(String mEmail, String mPassword, int parent_id) {
            this.mEmail = mEmail;
            this.mPassword = mPassword;
            this.parent_id = parent_id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                StringRequest stringRequest;
                stringRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.add_exist_child), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG, response);
                            JSONObject jsonObject = new JSONObject(response);
                            if ( !jsonObject.has("error") ){
                                getChild(jsonObject);
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
                            params.put("email", mEmail);
                            params.put("password", mPassword);
                            params.put("parent_id", Integer.valueOf(parent_id).toString() );
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
            showProgress(false);
            if (success) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("new_child_id", childID);
                startActivity(intent);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAddExistChild = null;
            showProgress(false);
        }


        protected ChildContainer getChild(JSONObject jsonObject){
            try {
                ChildContainer cContainer = new ChildContainer(
                        jsonObject.getInt("id"),
                        jsonObject.get("name").toString(),
                        jsonObject.get("surname").toString(),
                        jsonObject.get("patronymic").toString(),
                        jsonObject.getInt("points_have")

                );
                AppData.getChildren().append(jsonObject.getInt("id"), cContainer);

                int goalID = jsonObject.getInt("current_goal_id");
                if (goalID != 0){
                    getGoal(goalID, jsonObject.getInt("id"));
                }
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
                                    Log.d("getGoal", response);
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

