package com.example.selfmadekid.data;

import java.util.ArrayList;


public class ChildContainer {
    private int id;
    private String name;
    private String surname;
    private String patronymic;
    private Goal currentGoal;
    private int points = 0;
    private ArrayList<String> finishedGoals = new ArrayList<>();



    public ChildContainer(int id, String name, String surname, String patronymic) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
    }

    public ChildContainer(int id, String name, String surname, String patronymic, int points) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.points = points;
    }

    public ChildContainer(int id, String name, String surname, String patronymic, Goal currentGoal) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.currentGoal = currentGoal;

    }

    public ChildContainer(int id, String name, String surname, String patronymic, Goal currentGoal,  ArrayList<String> finishedGoals) {
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

    public ArrayList<String> getFinishedGoals() {
        return finishedGoals;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points+=points;
    }

    
}
