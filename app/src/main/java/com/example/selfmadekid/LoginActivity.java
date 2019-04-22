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
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
public class LoginActivity extends AppCompatActivity  {


    private static final int REQUEST_READ_CONTACTS = 0;

    private UserLoginTask mAuthTask = null;

    private boolean isRegistration = false;

    private Context context;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private EditText mPasswordRepeatView;
    private Button mTopLoginOrRegButton;
    private Button mBottomLoginOrRegButton;
    private TextView mTextSwitcher;
    private int loginID = -1;
    private int roleID = -1;
    private boolean connectError = true;

    //animation
    AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
    AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;

    private final Animation.AnimationListener textChangeAnimation = new Animation.AnimationListener(){
        @Override
        public void onAnimationEnd(Animation arg0) {
            // start fadeOut when fadeIn ends (continue)
            if (mTextSwitcher.getText().toString().equals( getResources().getString(R.string.registration))) {
                mTextSwitcher.setText(R.string.action_sign_in);
            }else{
                mTextSwitcher.setText(R.string.registration);
            }
            mTextSwitcher.startAnimation(fadeIn);
        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
        }

        @Override
        public void onAnimationStart(Animation arg0) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.context = this;
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordRepeatView = (EditText) findViewById(R.id.password_repeat);
        mTopLoginOrRegButton = (Button) findViewById(R.id.email_sign_in_button);
        mBottomLoginOrRegButton = (Button) findViewById(R.id.bottom_login_or_reg_button);
        mTextSwitcher = (TextView) findViewById(R.id.text_switcher_login);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin(null);
                    return true;
                }
                return false;
            }
        });



        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        //animation settings
        fadeIn.setDuration(300);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(300);
        fadeOut.setFillAfter(true);
        fadeOut.setAnimationListener(textChangeAnimation);
        roleID = getIntent().getIntExtra("role_name", -1);
        if (roleID == SelectRoleActivity.CHILD_ROLE){
            mBottomLoginOrRegButton.setVisibility(View.GONE);
        }



    }







    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(View view) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (isRegistration && !mPasswordView.getText().toString().equals(mPasswordRepeatView.getText().toString())){
            mPasswordRepeatView.setError(getString(R.string.passwords_must_be_the_same));
            focusView = mPasswordRepeatView;
            cancel = true;
        }


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
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
            mAuthTask = new UserLoginTask(email, password);
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    public void onBottomLoginOrRegistrationPressed(View view){

        if (isRegistration){
            mPasswordRepeatView.setVisibility(View.GONE);
            mTopLoginOrRegButton.setText(R.string.action_sign_in);
            mBottomLoginOrRegButton.setText(R.string.registration);
            isRegistration = false;
        } else{
            mPasswordRepeatView.setVisibility(View.VISIBLE);
            mTopLoginOrRegButton.setText(R.string.registration);
            mBottomLoginOrRegButton.setText(R.string.action_sign_in);
            isRegistration = true;
        }
        mTextSwitcher.startAnimation(fadeOut);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                StringRequest stringRequest;
                if (isRegistration){
                    stringRequest = new StringRequest(Request.Method.POST,
                            getString(R.string.register_script),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.get("error").equals("")){
                                            loginID = Integer.valueOf(jsonObject.get("id").toString());
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

                            return params;
                        }
                    };
                }else{
                    stringRequest = new StringRequest(Request.Method.POST,
                            getString(R.string.login_script),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    System.out.println(response);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.get("error").equals("")){
                                            loginID = Integer.valueOf(jsonObject.get("id").toString());
                                            onPostExecute(true);
                                        }else{
                                            makeToast(jsonObject.get("error").toString());
                                        }
                                    } catch (Exception e){
                                        System.out.println("______ not");
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
                            params.put("type", Integer.valueOf(roleID).toString());
                            return params;
                        }
                    };
                }
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
                if (roleID == SelectRoleActivity.PARENT_ROLE){
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("id", loginID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                if (roleID == SelectRoleActivity.CHILD_ROLE){
                    Intent intent = new Intent(context, ChildMainActivity.class);
                    intent.putExtra("id", loginID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
            onCancelled();
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

