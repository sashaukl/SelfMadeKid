package com.example.selfmadekid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.example.selfmadekid.parent_main_fragments.ChildSelect;
import com.example.selfmadekid.parent_main_fragments.ScheduleParent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Locale;




public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private TextView mTextMessage;
    private ChildContainer childContainer;
    private int selectedChildID = -1;
    private int userID = -1;
    public int lastNavigationItemSelected = R.id.navigation_home;
    Context context;
    View mainContainer;


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
        this.setContentView(R.layout.activity_main);
        AppData.setCurrentState(AppData.PARENT);
        Log.d(TAG, "onCreate");
        this.context = this;

        Locale locale = new Locale("RU");
        Locale.setDefault(locale);
        userID = getIntent().getIntExtra("id", -1);
        AppData.setCirrentUserID(userID);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(ChildSelect.newInstance());

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

    @Override
    protected void onPostResume() {
        forceLoadingFragment();
        super.onPostResume();
    }

    public void onAddNewChildPressed(View view){
        Intent intent = new Intent(this, AddNewChildActivity.class);
        intent.putExtra("parent_id", userID);
        startActivity(intent);
    }


    public int getSelectedChildID() {
        return selectedChildID;
    }

    public void setSelectedChildID(int selectedChildID) {
        this.selectedChildID = selectedChildID;
    }

    public void forceLoadingFragment(){
        preLoad(lastNavigationItemSelected);
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
