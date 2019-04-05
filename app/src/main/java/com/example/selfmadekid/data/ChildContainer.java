package com.example.selfmadekid.data;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;

public class ChildContainer {
    private int id;
    private String name;
    private String surname;
    private String patronymic;


    private HashMap<DayOfWeek, RepetitiveTasksList > repetitiveTaskContainer =
        new HashMap<DayOfWeek, RepetitiveTasksList>() {{
            put(DayOfWeek.MONDAY, new RepetitiveTasksList());
            put(DayOfWeek.TUESDAY, new  RepetitiveTasksList());
            put(DayOfWeek.WEDNESDAY, new RepetitiveTasksList());
            put(DayOfWeek.THURSDAY, new RepetitiveTasksList());
            put(DayOfWeek.FRIDAY, new RepetitiveTasksList());
            put(DayOfWeek.SATURDAY, new RepetitiveTasksList());
            put(DayOfWeek.SUNDAY, new RepetitiveTasksList());
        }};

    private OneTimeTaskList oneTimeTaskContainer = new OneTimeTaskList();


    public OneTimeTaskList getOneTimeTaskContainer() {
        return oneTimeTaskContainer;
    }

    public RepetitiveTasksList getDayOfTheWeekContainer(DayOfWeek dayOfWeek){
        return repetitiveTaskContainer.get(dayOfWeek);
    }

}
