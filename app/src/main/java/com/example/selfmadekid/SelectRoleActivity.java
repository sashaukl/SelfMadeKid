package com.example.selfmadekid;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.TextView;

import com.example.selfmadekid.adapters.RoleSwitchingAdapter;
import com.example.selfmadekid.select_role_tabs.TabChild;
import com.example.selfmadekid.select_role_tabs.TabParent;
import com.google.android.material.tabs.TabLayout;

public class SelectRoleActivity extends AppCompatActivity implements TabParent.OnFragmentInteractionListener, TabChild.OnFragmentInteractionListener {

    private TextView mTextMessage;
    public static final int PARENT_ROLE = 1;
    public static final int CHILD_ROLE = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.role_select_activity);

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
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void onParentButtonPressed(View view){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("role_name", PARENT_ROLE);
        startActivity(intent);
        finish();
    }

    public void onChildButtonPressed(View view){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("role_name", CHILD_ROLE);
        startActivity(intent);
        finish();
    }

}
