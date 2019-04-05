package com.example.selfmadekid.data;

public abstract class ChildTask  {

    protected Integer task_id;
    protected String taskText;
    protected int necessaryProgress;
    protected int currentProgress = 0;

    public Integer getTask_id() {
        return task_id;
    }

    public void setTask_id(Integer task_id) {
        this.task_id = task_id;
    }

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    public int getNecessaryProgress() {
        return necessaryProgress;
    }

    public void setNecessaryProgress(int necessaryProgress) {
        this.necessaryProgress = necessaryProgress;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }
}
