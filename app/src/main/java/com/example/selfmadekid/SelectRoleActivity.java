package com.example.selfmadekid;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.selfmadekid.adapters.RoleSwitchingAdapter;
import com.example.selfmadekid.data.AppData;
import com.example.selfmadekid.select_role_tabs.TabChild;
import com.example.selfmadekid.select_role_tabs.TabParent;
import com.example.selfmadekid.services.ReceiveChildDataService;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;

public class SelectRoleActivity extends AppCompatActivity implements TabParent.OnFragmentInteractionListener, TabChild.OnFragmentInteractionListener {

    private TextView mTextMessage;


    View mainContainer;
    View mProgressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.role_select_activity);
        AppData.readData(this);
        if (AppData.getCurrentUserID()== -1){
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_select_role);
            tabLayout.addTab(tabLayout.newTab().setText(R.string.parent));
            tabLayout.addTab(tabLayout.newTab().setText(R.string.child));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager_select_role);
            final RoleSwitchingAdapter adapter = new RoleSwitchingAdapter(getSupportFragmentManager());
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }


            } );
        }else{
            mProgressBar = findViewById(R.id.progress_bar);
            mainContainer = findViewById(R.id.main_container);
            mProgressBar.setVisibility(View.VISIBLE);
            mainContainer.setVisibility(View.GONE);
            System.out.println("update in: " + (long)(AppData.getLastUpdate() + AppData.getUpdateDelay()));
            System.out.println("now: " + Calendar.getInstance().getTimeInMillis());

            if (AppData.getCurrentState() == AppData.PARENT ){
                if (AppData.getLastUpdate() + AppData.getUpdateDelay() <= Calendar.getInstance().getTimeInMillis() || !AppData.isLoaded()){
                    Intent intent = new Intent(this, ReceiveChildDataService.class);
                    intent.putExtra("role", AppData.PARENT);
                    AppData.setCurrentState(AppData.PARENT);
                    AppData.setCurrentUserID(AppData.getCurrentUserID());
                    intent.putExtra("userID", AppData.getCurrentUserID());
                    startService(intent);
                }else {
                    Intent tmpIntent = new Intent(this, MainActivity.class);
                    tmpIntent.putExtra("id", AppData.getCurrentUserID());
                    tmpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(tmpIntent);
                }
                //finish();
            }
            if (AppData.getCurrentState() == AppData.CHILD){
                if (AppData.getLastUpdate() + AppData.getUpdateDelay() <= Calendar.getInstance().getTimeInMillis() || !AppData.isLoaded() ) {
                    //mProgressBar.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(this, ReceiveChildDataService.class);
                    intent.putExtra("role", AppData.CHILD);
                    AppData.setCurrentState(AppData.CHILD);
                    AppData.setCurrentUserID(AppData.getCurrentUserID());
                    intent.putExtra("userID", AppData.getCurrentUserID());
                    startService(intent);
                }else {
                    Intent tmpIntent = new Intent(this, ChildMainActivity.class);
                    tmpIntent.putExtra("id", AppData.getCurrentUserID());
                    tmpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(tmpIntent);
                }
            }
        }


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public void onParentButtonPressed(View view){
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token", "Token: " + refreshedToken);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("role_name", AppData.PARENT);
        FirebaseApp.initializeApp(this);
        startActivity(intent);
        finish();
    }

    public void onChildButtonPressed(View view){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("role_name", AppData.CHILD);
        startActivity(intent);
        finish();
    }


}
