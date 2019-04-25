package com.example.selfmadekid.data;


import android.util.ArraySet;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.HashMap;
import java.util.Set;

public class Goal {
    private String goalName;
    private int currentPoints = 0;
    private int finishPoints;
    private int goal_id;
    private Set<LocalDate> repetitiveTaskEnded = new ArraySet<>();
    private boolean isConfirmed;


    public Goal(String goal_name, int need_points, int id) {
        this.goalName = goal_name;
        this.finishPoints = need_points;
        this.goal_id=id;
        isConfirmed = finishPoints!=0;
    }

    public Goal(String goal_name, int currentPoints, int need_points, int id) {
        this.goalName = goal_name;
        this.finishPoints = need_points;
        this.currentPoints = currentPoints;
        this.goal_id=id;
        isConfirmed = finishPoints!=0;
    }


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


    public RepetitiveTasksList getDayOfTheWeekContainer(DayOfWeek dayOfWeek){
        return repetitiveTaskContainer.get(dayOfWeek);
    }

    private HashMap<Integer,HashMap<LocalDate,Integer>> checkedDates = new HashMap<Integer,HashMap<LocalDate,Integer>> () {};

    public HashMap<Integer,HashMap<LocalDate,Integer>> getCheckedDates() {
        return checkedDates;
    }



    private OneTimeTaskList oneTimeTaskContainer = new OneTimeTaskList();
    public OneTimeTaskList getOneTimeTaskContainer() {
        return oneTimeTaskContainer;
    }

    public Set<LocalDate> getRepetitiveTaskEnded() {
        return repetitiveTaskEnded;
    }

    public void setRepetitiveTaskEnded(ArraySet<LocalDate> repetitiveTaskEnded) {
        this.repetitiveTaskEnded = repetitiveTaskEnded;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(int currentPoints) {
        this.currentPoints = currentPoints;
    }

    public boolean addPoints(int addPoints) {
        this.currentPoints += addPoints;
        if (currentPoints != 0 && currentPoints >= finishPoints){
            return true;
        }
        return false;
    }

    public int getFinishPoints() {
        return finishPoints;
    }

    public void setFinishPoints(int finishPoints) {
        this.finishPoints = finishPoints;
        isConfirmed = finishPoints!=0;
    }

    public int getGoal_id() {
        return goal_id;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }


}
