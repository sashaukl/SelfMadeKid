package com.example.selfmadekid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class AddNewChildActivity extends AppCompatActivity  {


    private static final int REQUEST_READ_CONTACTS = 0;

    private AddChildTask mAuthTask = null;

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
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
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


        if (mFirstNameView.getText().toString().equals("")){
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

        if (!mPasswordView.getText().toString().equals(mPasswordRepeatView.getText().toString())){
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

    private void makeToast(String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}

