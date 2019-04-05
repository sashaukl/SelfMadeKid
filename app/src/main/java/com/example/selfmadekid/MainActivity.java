package com.example.selfmadekid;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;


import com.example.selfmadekid.data.AppData;
import com.example.selfmadekid.data.ChildContainer;
import com.example.selfmadekid.data.ChildTask;
import com.example.selfmadekid.data.OneTimeTask;
import com.example.selfmadekid.data.RepetitiveTask;
import com.example.selfmadekid.data.RepetitiveTasksList;
import com.example.selfmadekid.data.TimeHolder;
import com.example.selfmadekid.main_fragments.ScheduleParent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import org.threeten.bp.LocalDate;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ChildContainer childContainer;
    private int selectedChildID = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.info:
                    loadFragment(ScheduleParent.newInstance());
                    return true;
                case R.id.child_schedule:
                    //mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        Locale locale = new Locale("RU");
        Locale.setDefault(locale);

        childContainer = new ChildContainer();
        AppData.getChildren().put(selectedChildID, childContainer);


        if (AppData.getChildren().get(selectedChildID) == null){
            AppData.getChildren().put(selectedChildID, new ChildContainer());
        }

        childContainer.getDayOfTheWeekContainer(LocalDate.now().getDayOfWeek()).add(new RepetitiveTask(1,"Сделать уроки",
                30, 18, 32));

        childContainer.getDayOfTheWeekContainer(LocalDate.now().getDayOfWeek()).add(new RepetitiveTask(2,"Сделать уроки12",
               50, 16, 58 ));


        childContainer.getOneTimeTaskContainer().add(new OneTimeTask(3, "Проект", LocalDate.now(), 23, 24));


        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_activity_frame_layout, fragment);
        ft.commit();
    }


    public ChildContainer getChildContainer() {
        return childContainer;
    }


    public void onAddButtonPressed(View view){
        Intent intent = new Intent(this, AddNewTaskActivity.class);
        intent.putExtra("id", selectedChildID);
        startActivity(intent);
        //finish();
    }

    public int getSelectedChildID() {
        return selectedChildID;
    }
}
