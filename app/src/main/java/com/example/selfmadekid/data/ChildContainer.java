package com.example.selfmadekid.data;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;

public class ChildContainer {
    private int id;
    private String name;
    private String surname;
    private String patronymic;


    private HashMap<LocalDate, MyTaskList> taskContainer = new HashMap<>();


    public HashMap<LocalDate, MyTaskList> getTaskContainer() {
        return taskContainer;
    }
}
