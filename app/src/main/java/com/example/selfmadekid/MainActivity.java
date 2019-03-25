package com.example.selfmadekid;

import android.os.Bundle;
import androidx.annotation.NonNull;


import com.example.selfmadekid.data.ChildContainer;
import com.example.selfmadekid.data.ChildTask;
import com.example.selfmadekid.data.MyTaskList;
import com.example.selfmadekid.main_fragments.ScheduleParent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.widget.TextView;
import org.threeten.bp.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ChildContainer childContainer;

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

        childContainer.getTaskContainer().put(LocalDate.now(), new MyTaskList());
        childContainer.getTaskContainer().get(LocalDate.now()).add(new ChildTask(1,24, 0,1
        ,"Сделать уроки", 33));

        childContainer.getTaskContainer().get(LocalDate.now()).add(new ChildTask(2,23, 0,2
                ,"Сделать уроки111", 33));



        /*
        mCalendarView = findViewById(R.id.calendarView);

        //mCalendarView.setAccessibilityHeading(false);

        mCalendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        Calendar calendar = Calendar.getInstance();


        Calendar now = Calendar.getInstance();

        //only way why its works :(
        CalendarDay today = CalendarDay.today();
        int year = today.getYear();
        int month = today.getMonth();
        int day = today.getDay();

        mCalendarView.setCurrentDate(CalendarDay.from(year, month, day) , true);
        mCalendarView.setSelectedDate(LocalDate.now());

*/

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
}
