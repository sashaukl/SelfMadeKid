package com.example.selfmadekid.data;

import android.util.SparseArray;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;

public class ChildContainer {
    private int id;
    private String name;
    private String surname;
    private String patronymic;
    private int goalID=0;
    private Goal currentGoal;
    private SparseArray<Goal> finishedGoals = new SparseArray<Goal>();



    public ChildContainer(int id, String name, String surname, String patronymic) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
    }

    public ChildContainer(int id, String name, String surname, String patronymic, Goal currentGoal) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.currentGoal = currentGoal;
    }

    public ChildContainer(int id, String name, String surname, String patronymic, Goal currentGoal, SparseArray<Goal> finishedGoals) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.currentGoal = currentGoal;
        this.finishedGoals = finishedGoals;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public Goal getCurrentGoal() {
        return currentGoal;
    }

    public void setCurrentGoal(Goal currentGoal) {
        this.currentGoal = currentGoal;
    }
}
