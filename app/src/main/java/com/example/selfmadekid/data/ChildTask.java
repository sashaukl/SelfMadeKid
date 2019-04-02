package com.example.selfmadekid.data;

public class ChildTask implements Comparable<ChildTask>{

    private Integer task_id;
    private int hourToStartTask;
    private int minuteToStartTask;
    private int duration;

    private String taskText;
    private int necessaryProgress;
    private int currentProgress = 10;


    public ChildTask(Integer task_id, int hourToStartTask, int minuteToStartTask, int duration, String taskText, int necessaryProgress) {
        this.task_id = task_id;
        this.hourToStartTask = hourToStartTask;
        this.minuteToStartTask = minuteToStartTask;
        this.duration = duration;
        this.taskText = taskText;
        this.necessaryProgress = necessaryProgress;
    }


    public int getTask_id() {
        return task_id;
    }

    public int getHourToStartTask() {
        return hourToStartTask;
    }

    public int getMinuteToStartTask() {
        return minuteToStartTask;
    }

    public String getTaskText() {
        return taskText;
    }

    public int getNecessaryProgress() {
        return necessaryProgress;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public int getDuration() {
        return duration;
    }


    public void setTask_id(Integer task_id) {
        this.task_id = task_id;
    }

    public void setHourToStartTask(int hourToStartTask) {
        this.hourToStartTask = hourToStartTask;
    }

    public void setMinuteToStartTask(int minuteToStartTask) {
        this.minuteToStartTask = minuteToStartTask;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    public void setNecessaryProgress(int necessaryProgress) {
        this.necessaryProgress = necessaryProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    @Override
    public int compareTo(ChildTask o) {
        int i = hourToStartTask-o.getHourToStartTask();
        if (i==0){
            i = minuteToStartTask-o.getMinuteToStartTask();
        }
        return i;
    }
}
