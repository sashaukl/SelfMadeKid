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

import com.example.selfmadekid.data.AppData;
import com.example.selfmadekid.data.ChildContainer;
import com.example.selfmadekid.parent_main_fragments.ChildAbout;
import com.example.selfmadekid.parent_main_fragments.ScheduleParent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Locale;

public class ChildMainActivity extends AppCompatActivity {
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


    public int getSelectedChildID() {
        return userID;
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
